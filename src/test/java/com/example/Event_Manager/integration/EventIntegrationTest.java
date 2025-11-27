package com.example.Event_Manager.integration;

import com.example.Event_Manager.auth.repository.UserRepository;
import com.example.Event_Manager.auth.util.JwtUtil;
import com.example.Event_Manager.models.category.Category;
import com.example.Event_Manager.models.category.repository.CategoryRepository;
import com.example.Event_Manager.models.event.dto.request.CreateEventDTO;
import com.example.Event_Manager.models.event.dto.request.UpdateEventDTO;
import com.example.Event_Manager.models.user.User;
import com.example.Event_Manager.models.user.enums.Role;
import com.example.Event_Manager.models.user.enums.Status;
import com.example.Event_Manager.models.venue.Venue;
import com.example.Event_Manager.models.venue.repository.VenueRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integration.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class EventIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VenueRepository venueRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    private Long venueId;
    private Long categoryId;
    private User organizer;
    private String organizerToken;

    @BeforeAll
    void setup() {
        userRepository.deleteAll();
        venueRepository.deleteAll();
        categoryRepository.deleteAll();
        organizer = User.builder()
                .firstName("Organizer")
                .lastName("Test")
                .email("organizer@test.com")
                .password(passwordEncoder.encode("password"))
                .phoneNumber("1234567890")
                .role(Role.ORGANIZER)
                .status(Status.ACTIVE)
                .build();
        userRepository.save(organizer);
        organizerToken = jwtUtil.generateToken(organizer);
        Venue venue = Venue.builder()
                .name("Test Venue")
                .address("123 Test Street")
                .description("A place for tests")
                .build();
        venueId = venueRepository.save(venue).getId();
        Category category = Category.builder()
                .name("Test Category")
                .description("Category for integration test")
                .build();
        categoryId = categoryRepository.save(category).getId();
    }

    private CreateEventDTO getSampleEventDTO(String name) {
        LocalDateTime startTime = LocalDateTime.now().plusDays(2);
        LocalDateTime endTime = startTime.plusHours(2);
        return new CreateEventDTO(
                name,
                "Opis testowy",
                startTime,
                endTime,
                venueId,
                categoryId
        );
    }

    @Test
    void shouldCreateAndGetEvent() throws Exception {
        CreateEventDTO createEventDTO = getSampleEventDTO("Test Event");
        String json = objectMapper.writeValueAsString(createEventDTO);
        String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/events")
                        .header("Authorization", "Bearer " + organizerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long eventId = objectMapper.readTree(response).get("id").asLong();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/events/" + eventId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Event"));
    }

    @Test
    void shouldUpdateEvent() throws Exception {
        CreateEventDTO createEventDTO = getSampleEventDTO("Event to update");
        String json = objectMapper.writeValueAsString(createEventDTO);
        String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/events")
                        .header("Authorization", "Bearer " + organizerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long eventId = objectMapper.readTree(response).get("id").asLong();
        LocalDateTime newStartTime = LocalDateTime.now().plusDays(3);
        LocalDateTime newEndTime = newStartTime.plusHours(2);
        UpdateEventDTO updateEventDTO = new UpdateEventDTO(
                "Updated Event",
                "Nowy opis",
                newStartTime,
                newEndTime,
                venueId,
                categoryId
        );
        String updateJson = objectMapper.writeValueAsString(updateEventDTO);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/events/" + eventId)
                        .header("Authorization", "Bearer " + organizerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Updated Event"));
    }

    @Test
    void shouldDeleteEvent() throws Exception {
        CreateEventDTO createEventDTO = getSampleEventDTO("Delete Event");
        String json = objectMapper.writeValueAsString(createEventDTO);
        String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/events")
                        .header("Authorization", "Bearer " + organizerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long eventId = objectMapper.readTree(response).get("id").asLong();
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/events/" + eventId)
                        .header("Authorization", "Bearer " + organizerToken))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/events/" + eventId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void shouldGetAllEvents() throws Exception {
        CreateEventDTO createEventDTO = getSampleEventDTO("Event1");
        String json = objectMapper.writeValueAsString(createEventDTO);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/events")
                        .header("Authorization", "Bearer " + organizerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/events"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Event1"));
    }

    @Test
    void shouldReturnNotFoundForMissingEvent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/events/999999"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void shouldGetEventsByCategory() throws Exception {
        CreateEventDTO createEventDTO = getSampleEventDTO("Category Event");
        String json = objectMapper.writeValueAsString(createEventDTO);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/events")
                        .header("Authorization", "Bearer " + organizerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/events/category/" + categoryId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Category Event"));
    }

    @Test
    void shouldGetEventsByVenue() throws Exception {
        CreateEventDTO createEventDTO = getSampleEventDTO("Venue Event");
        String json = objectMapper.writeValueAsString(createEventDTO);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/events")
                        .header("Authorization", "Bearer " + organizerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/events/venue/" + venueId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].venue.id").value(venueId));
    }

    @Test
    void shouldGetEventsByDateRange() throws Exception {
        CreateEventDTO createEventDTO = getSampleEventDTO("DateRange Event");
        String json = objectMapper.writeValueAsString(createEventDTO);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/events")
                        .header("Authorization", "Bearer " + organizerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated());
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/events/date-range")
                        .param("start", start.toString())
                        .param("end", end.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("DateRange Event"));
    }

    @Test
    void shouldSearchEventsByName() throws Exception {
        CreateEventDTO createEventDTO = getSampleEventDTO("Searchable Event");
        String json = objectMapper.writeValueAsString(createEventDTO);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/events")
                        .header("Authorization", "Bearer " + organizerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/events/search")
                        .param("name", "Searchable"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Searchable Event"));
    }
}