package com.example.Event_Manager.integration;


import com.example.Event_Manager.auth.repository.UserRepository;
import com.example.Event_Manager.auth.util.JwtUtil;
import com.example.Event_Manager.models.category.Category;
import com.example.Event_Manager.models.category.repository.CategoryRepository;
import com.example.Event_Manager.models.event.Event;
import com.example.Event_Manager.models.event.repository.EventRepository;
import com.example.Event_Manager.models.interested.repository.InterestedRepository;
import com.example.Event_Manager.models.user.User;
import com.example.Event_Manager.models.user.enums.Role;
import com.example.Event_Manager.models.user.enums.Status;
import com.example.Event_Manager.models.venue.Venue;
import com.example.Event_Manager.models.venue.repository.VenueRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static com.example.Event_Manager.models.event.enums.Status.PUBLISHED;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integration.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Transactional
public class InterestedIntegrationTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private VenueRepository venueRepository;
    @Autowired
    private InterestedRepository interestedRepository;

    private User testUser;
    private Event testEvent;
    private Event testEvent2;
    private String token;

    @BeforeEach
    void setup() {
        interestedRepository.deleteAll();
        eventRepository.deleteAll();
        userRepository.deleteAll();

        testUser = createAndSaveUser();
        token = jwtUtil.generateToken(testUser);

        Category cat = categoryRepository.save(Category.builder().name("Test Cat").description("desc").build());
        Venue ven = venueRepository.save(Venue.builder().name("Venue").address("Addr").description("desc").build());

        testEvent = eventRepository.save(Event.builder()
                .name("Super Event")
                .description("Opis")
                .startTime(new Date())
                .endTime(new Date())
                .status(PUBLISHED)
                .category(cat)
                .venue(ven)
                .organizer(testUser)
                .build());

        testEvent2 = eventRepository.save(Event.builder()
                .name("Super Event2")
                .description("Opis")
                .startTime(new Date())
                .endTime(new Date())
                .status(PUBLISHED)
                .category(cat)
                .venue(ven)
                .organizer(testUser)
                .build());
    }
    private User createAndSaveUser() {
        User user = User.builder()
                .firstName("Fan")
                .lastName("Imprez")
                .email("fan@test.com")
                .phoneNumber("123456789")
                .password("pass")
                .role(Role.ATTENDEE)
                .status(Status.ACTIVE)
                .build();
        return userRepository.save(user);
    }

    @Test
    @DisplayName("Should add to interested list (first click)")
    void shouldAddToInterested() throws Exception {
        mockMvc.perform(post("/api/interested/" + testEvent.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Added to interested"));
    }

    @Test
    @DisplayName("Should remove from interested list (second click)")
    void shouldRemoveFromInterested() throws Exception {
        //dodajemy zeby miec co usunac
        mockMvc.perform(post("/api/interested/" + testEvent.getId())
                .header("Authorization", "Bearer " + token));

        //drugie klikniecie powinno usunac
        mockMvc.perform(post("/api/interested/" + testEvent.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Removed from interested"));
    }

    @Test
    @DisplayName("Should return list of interested events")
    void shouldReturnMyInterests() throws Exception {
        //dodajemy event do ulubionych
        mockMvc.perform(post("/api/interested/" + testEvent.getId())
                .header("Authorization", "Bearer " + token));
        mockMvc.perform(post("/api/interested/" + testEvent2.getId())
                .header("Authorization", "Bearer " + token));

        //sprawdzamy czy jest na liscie
        mockMvc.perform(get("/api/interested")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].eventName", is("Super Event")))
                .andExpect(jsonPath("$.content[1].eventName", is("Super Event2")));
    }

    @Test
    @DisplayName("Should automatically remove interest when event is deleted(Cascade)")
    void shouldRemoveInterestWhenEventIsDeleted() throws Exception {
        mockMvc.perform(post("/api/interested/" + testEvent.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        //upewniamy że jest na liscie
        mockMvc.perform(get("/api/interested")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        //wymuszamy czyszczenie kontekstu
        entityManager.clear();
        //usuwamy wydarzenie z bazy
        eventRepository.deleteById(testEvent.getId());

        entityManager.flush();


        //sprawdzamy listę zainteresowań, powinno usunac sie casscodowo
        mockMvc.perform(get("/api/interested")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    @DisplayName("Should return 401 Forbidden for unauthenticated user")
    void shouldForbidUnauthenticated() throws Exception {
        mockMvc.perform(post("/api/interested/" + testEvent.getId()))
                .andExpect(status().isUnauthorized());
    }
    @Test
    @DisplayName("Should return 404 Not Found when interest for nonexistent event")
    void shouldReturnNotFoundForMissingEvent() throws Exception {
        long nonExistentId = 999L;

        mockMvc.perform(post("/api/interested/" + nonExistentId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

}
