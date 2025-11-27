package com.example.Event_Manager.integration;

import com.example.Event_Manager.auth.repository.UserRepository;
import com.example.Event_Manager.auth.util.JwtUtil;
import com.example.Event_Manager.models.favorite.repository.FavoriteRepository;
import com.example.Event_Manager.models.user.User;
import com.example.Event_Manager.models.user.enums.Role;
import com.example.Event_Manager.models.user.enums.Status;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
import org.springframework.transaction.annotation.Transactional;


import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
public class FavoriteIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private FavoriteRepository favoriteRepository;
    @Autowired private JwtUtil jwtUtil;

    @PersistenceContext
    private EntityManager entityManager;

    private User fan;
    private User organizer;
    private String fanToken;

    @BeforeEach
    void setup() {
        favoriteRepository.deleteAll();
        userRepository.deleteAll();

        //tworzymy Attendee ktory będzie dodawał do ulubionych
        fan = User.builder()
                .firstName("Fan")
                .lastName("Testowy")
                .email("fan@test.com")
                .phoneNumber("111111111")
                .password("pass")
                .role(Role.ATTENDEE)
                .status(Status.ACTIVE)
                .build();
        userRepository.save(fan);
        fanToken = jwtUtil.generateToken(fan);

        //tworzymy organizatora
        organizer = User.builder()
                .firstName("Super")
                .lastName("Organizer")
                .email("org@test.com")
                .phoneNumber("222222222")
                .password("pass")
                .role(Role.ORGANIZER)
                .status(Status.ACTIVE)
                .build();
        userRepository.save(organizer);
    }

    @Test
    @DisplayName("Should add organizer to favorites")
    void shouldAddToFavorites() throws Exception {
        mockMvc.perform(post("/api/favorites/" + organizer.getId())
                        .header("Authorization", "Bearer " + fanToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Added to favorites"));

        //sprawdzamy w bazie
        assertEquals(1, favoriteRepository.findAll().size());
    }

    @Test
    @DisplayName("Should remove from favorites")
    void shouldRemoveFromFavorites() throws Exception {
        //dodajemy uzytkownikowi organizatora do ulubionych
        mockMvc.perform(post("/api/favorites/" + organizer.getId())
                        .header("Authorization", "Bearer " + fanToken))
                .andExpect(status().isOk());

        //usuwamy, toggle
        mockMvc.perform(post("/api/favorites/" + organizer.getId())
                        .header("Authorization", "Bearer " + fanToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Removed from favorites"));

        //sprawdzamy w bazie
        assertEquals(0, favoriteRepository.findAll().size());
    }

    @Test
    @DisplayName("Should return list of favorites")
    void shouldReturnFavoritesPage() throws Exception {
        //dodajemy uzytkownikowi organizatora do ulubionych
        mockMvc.perform(post("/api/favorites/" + organizer.getId())
                .header("Authorization", "Bearer " + fanToken));

        //pobieramy get /api/favorites
        mockMvc.perform(get("/api/favorites")
                        .header("Authorization", "Bearer " + fanToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].organizerEmail", is("org@test.com")));
    }

    @Test
    @DisplayName("Should what happens when favorited organizer is deleted")
    void shouldHandleDeletedOrganizer() throws Exception {
        //uzytkownik dodaje organizatora do ulubionych
        mockMvc.perform(post("/api/favorites/" + organizer.getId())
                .header("Authorization", "Bearer " + fanToken));

        //usuwamy organizatora (Admin action simulation)
        //czyscimy cache zeby Hibernate widział zmiany w bazie
        entityManager.clear();
        userRepository.deleteById(organizer.getId());
        entityManager.flush();

        //pobieramy listę ulubionych
        mockMvc.perform(get("/api/favorites")
                        .header("Authorization", "Bearer " + fanToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].organizerEmail", is("org@test.com")));

        // Weryfikujemy dodatkowo, że organizator faktycznie ma status INACTIVE w bazie
        User deletedOrg = userRepository.findById(organizer.getId()).get();
        assertEquals(Status.INACTIVE, deletedOrg.getStatus());
    }
    @Test
    @DisplayName("Should return 401 Forbidden when trying to add favorite without token")
    void shouldForbidUnauthenticatedRequest() throws Exception {
        mockMvc.perform(post("/api/favorites/" + organizer.getId()))
                .andExpect(status().isUnauthorized());
    }
}