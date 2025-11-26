package com.example.Event_Manager.integration;

import com.example.Event_Manager.auth.repository.UserRepository;
import com.example.Event_Manager.auth.util.JwtUtil;
import com.example.Event_Manager.models.user.User;
import com.example.Event_Manager.models.user.dto.request.ChangePasswordRequest;
import com.example.Event_Manager.models.user.dto.request.UpdateUserDTO;
import com.example.Event_Manager.models.user.enums.Role;
import com.example.Event_Manager.models.user.enums.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integration.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Transactional
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @PersistenceContext
    private EntityManager entityManager;

    private User attendee;
    private String attendeeToken;

    private User admin;
    private String adminToken;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        //zwykły użytkownik
        attendee = User.builder()
                .firstName("Marek")
                .lastName("Mostowiak")
                .email("marek@test.com")
                .phoneNumber("123456789")
                .password(passwordEncoder.encode("pass12345"))
                .role(Role.ATTENDEE)
                .status(Status.ACTIVE)
                .build();
        userRepository.save(attendee);
        attendeeToken = jwtUtil.generateToken(attendee);

        //Admin
        admin = User.builder()
                .firstName("Admin")
                .lastName("Boss")
                .email("admin@test.com")
                .phoneNumber("999999999")
                .password(passwordEncoder.encode("pass123"))
                .role(Role.ADMIN)
                .status(Status.ACTIVE)
                .build();
        userRepository.save(admin);
        adminToken = jwtUtil.generateToken(admin);
    }
    @Test
    void getCurrentUser_ShouldReturnProfile_WhenAuthenticated() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + attendeeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Marek")))
                .andExpect(jsonPath("$.email", is("marek@test.com")));
    }
    @Test
    void updateUser_ShouldUpdate_WhenDataIsValid() throws Exception {
        UpdateUserDTO updateDTO = new UpdateUserDTO("Jan", "Janowski", "987654321");

        mockMvc.perform(put("/api/users/me")
                        .header("Authorization", "Bearer " + attendeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Jan")))
                .andExpect(jsonPath("$.phoneNumber", is("987654321")));
    }

    @Test
    void changePassword_ShouldSucceed_WhenDataIsValid() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("pass12345", "noweHaslo123", "noweHaslo123");

        mockMvc.perform(patch("/api/users/me/password")
                        .header("Authorization", "Bearer " + attendeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        //weryfikacja czy hasło się zmieniło w bazie
        User updatedUser = userRepository.findById(attendee.getId()).get();
        assertTrue(passwordEncoder.matches("noweHaslo123", updatedUser.getPassword()));
    }

    @Test
    void changePassword_ShouldFail_WhenOldPasswordIsWrong() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("ZleHaslo", "noweHaslo", "noweHaslo");

        mockMvc.perform(patch("/api/users/me/password")
                        .header("Authorization", "Bearer " + attendeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException));
    }

    @Test
    void deleteUser_ShouldSucceed_WhenAdminDeletes() throws Exception {
        //Usuwamy zwyklego usera
        mockMvc.perform(delete("/api/users/" + attendee.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
        //zeby hibarnate zaktualizowal stan bazy i pobrał go na nowo, bo jest zapamietany w sesji
        entityManager.flush();
        entityManager.clear();

        User deletedUser = userRepository.findById(attendee.getId()).orElse(null);

        assertTrue(deletedUser != null, "User should still exist in database but should be inactive");
        assertEquals(Status.INACTIVE, deletedUser.getStatus());
    }
}