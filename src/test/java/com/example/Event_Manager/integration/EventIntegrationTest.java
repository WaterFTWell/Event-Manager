package com.example.Event_Manager.integration;

import com.example.Event_Manager.user.repository.UserRepository;
import com.example.Event_Manager.auth.util.JwtUtil;
import com.example.Event_Manager.category.Category;
import com.example.Event_Manager.category.repository.CategoryRepository;
import com.example.Event_Manager.event.dto.request.CreateEventDTO;
import com.example.Event_Manager.event.dto.request.UpdateEventDTO;
import com.example.Event_Manager.user.User;
import com.example.Event_Manager.user.enums.Role;
import com.example.Event_Manager.user.enums.Status;
import com.example.Event_Manager.venue.Venue;
import com.example.Event_Manager.venue.repository.VenueRepository;
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

import static org.hamcrest.Matchers.hasSize;

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
    void shouldGetAllEventsWithPagination() throws Exception {
        for (int i = 1; i <= 3; i++) {
            CreateEventDTO createEventDTO = getSampleEventDTO("Event" + i);
            String json = objectMapper.writeValueAsString(createEventDTO);
            mockMvc.perform(MockMvcRequestBuilders.post("/api/events")
                            .header("Authorization", "Bearer " + organizerToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(MockMvcResultMatchers.status().isCreated());
        }
        // 1strona
        mockMvc.perform(MockMvcRequestBuilders.get("/api/events")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.number").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.first").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.last").value(false));

        // 2strona
        mockMvc.perform(MockMvcRequestBuilders.get("/api/events")
                        .param("page", "1")
                        .param("size", "2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.number").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.first").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.last").value(true));
    }

    @Test
    void shouldGetAllEventsWithSorting() throws Exception {
        CreateEventDTO event1 = getSampleEventDTO("Zebra Event");
        CreateEventDTO event2 = getSampleEventDTO("Apple Event");
        CreateEventDTO event3 = getSampleEventDTO("Banana Event");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/events")
                        .header("Authorization", "Bearer " + organizerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event1)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/events")
                        .header("Authorization", "Bearer " + organizerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event2)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/events")
                        .header("Authorization", "Bearer " + organizerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event3)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/events")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "name,asc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("Apple Event"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].name").value("Banana Event"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[2].name").value("Zebra Event"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/events")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "name,desc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("Zebra Event"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].name").value("Banana Event"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[2].name").value("Apple Event"));
    }

    @Test
    void shouldReturnNotFoundForMissingEvent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/events/999999"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void shouldGetEventsByCategoryWithPagination() throws Exception {
        for (int i = 1; i <= 3; i++) {
            CreateEventDTO createEventDTO = getSampleEventDTO("Category Event " + i);
            String json = objectMapper.writeValueAsString(createEventDTO);
            mockMvc.perform(MockMvcRequestBuilders.post("/api/events")
                            .header("Authorization", "Bearer " + organizerToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(MockMvcResultMatchers.status().isCreated());
        }

        mockMvc.perform(MockMvcRequestBuilders.get("/api/events/category/" + categoryId)
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].category.id").value(categoryId));
    }

    @Test
    void shouldGetEventsByVenueWithPagination() throws Exception {
        for (int i = 1; i <= 3; i++) {
            CreateEventDTO createEventDTO = getSampleEventDTO("Venue Event " + i);
            String json = objectMapper.writeValueAsString(createEventDTO);
            mockMvc.perform(MockMvcRequestBuilders.post("/api/events")
                            .header("Authorization", "Bearer " + organizerToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(MockMvcResultMatchers.status().isCreated());
        }

        mockMvc.perform(MockMvcRequestBuilders.get("/api/events/venue/" + venueId)
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].venue.id").value(venueId));
    }

    @Test
    void shouldGetEventsByDateRangeWithPagination() throws Exception {
        for (int i = 1; i <= 3; i++) {
            CreateEventDTO createEventDTO = getSampleEventDTO("DateRange Event " + i);
            String json = objectMapper.writeValueAsString(createEventDTO);
            mockMvc.perform(MockMvcRequestBuilders.post("/api/events")
                            .header("Authorization", "Bearer " + organizerToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(MockMvcResultMatchers.status().isCreated());
        }

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/events/date-range")
                        .param("start", start.toString())
                        .param("end", end.toString())
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(3));
    }

    @Test
    void shouldSearchEventsByNameWithPagination() throws Exception {
        for (int i = 1; i <= 3; i++) {
            CreateEventDTO createEventDTO = getSampleEventDTO("Searchable Event " + i);
            String json = objectMapper.writeValueAsString(createEventDTO);
            mockMvc.perform(MockMvcRequestBuilders.post("/api/events")
                            .header("Authorization", "Bearer " + organizerToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(MockMvcResultMatchers.status().isCreated());
        }

        mockMvc.perform(MockMvcRequestBuilders.get("/api/events/search")
                        .param("name", "Searchable")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(3));
    }

    @Test
    void shouldUseDefaultPaginationParameters() throws Exception {
        CreateEventDTO createEventDTO = getSampleEventDTO("Default Pagination Event");
        String json = objectMapper.writeValueAsString(createEventDTO);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/events")
                        .header("Authorization", "Bearer " + organizerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated());
        // default podejscie
        mockMvc.perform(MockMvcRequestBuilders.get("/api/events"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(10))
                .andExpect(MockMvcResultMatchers.jsonPath("$.number").value(0));
    }

    @Test
    void shouldReturnEmptyPageWhenNoEventsMatch() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/events/search")
                        .param("name", "NonExistentEvent")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}