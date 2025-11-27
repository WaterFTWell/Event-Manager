package com.example.Event_Manager.integration;

import com.example.Event_Manager.auth.dto.request.AuthRequest;
import com.example.Event_Manager.auth.dto.request.RegisterRequest;
import com.example.Event_Manager.auth.repository.UserRepository;
import com.example.Event_Manager.models.user.enums.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integration.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Transactional
public class AuthIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        //czyszczenie bazy przed kazdym testem
        userRepository.deleteAll();
    }
    @Test
    void shouldRegisterNewUser() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .firstName("Jan")
                .lastName("Janowski")
                .email("jan@test.com")
                .phoneNumber("1234567890")
                .password("password123")
                .role(Role.ATTENDEE)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.email", is("jan@test.com")))
                .andExpect(jsonPath("$.message", is("User registered successfully")));
    }
    @ParameterizedTest
    @MethodSource("provideInvalidRegisterRequests")
    void shouldReturnBadRequest_WhenInputIsInvalid(RegisterRequest invalidRequest) throws Exception {
        //ten test wykona się tyle razy dokladnie ile jest obiektów ma metoda provideInvalidRegisterRequests

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    private static Stream<RegisterRequest> provideInvalidRegisterRequests() {
        return Stream.of(
                RegisterRequest.builder().firstName("").lastName("Janowski").email("jan@test.com").phoneNumber("123456789").password("password123").role(Role.ATTENDEE).build(),//puste imie
                RegisterRequest.builder().firstName("Jan").lastName("").email("jan@test.com").phoneNumber("123456789").password("password123").role(Role.ATTENDEE).build(),//puste nazwisko
                RegisterRequest.builder().firstName("Jan").lastName("Janowski").email("janT_Ttest.com").phoneNumber("123456789").password("password123").role(Role.ATTENDEE).build(),//zły format email
                RegisterRequest.builder().firstName("Jan").lastName("Janowski").email("jan@test.com").phoneNumber("123456789").password("").role(Role.ATTENDEE).build(),//puste haslo
                RegisterRequest.builder().firstName("Jan").lastName("Janowski").email("jan@test.com").phoneNumber("").password("password123").role(Role.ATTENDEE).build()//pusty numer telefonu
        );
    }
    @Test
    void shouldFailRegisteringSameEmail() throws Exception {
        //rejestrujemy pierwszego
        RegisterRequest request1 = RegisterRequest.builder()
                .firstName("User1")
                .lastName("User1")
                .email("user@test.com")
                .phoneNumber("111111111")
                .password("password111")
                .role(Role.ATTENDEE)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        // probujemy zarejestrowac drugiego z tym samym mailem
        RegisterRequest request2 = RegisterRequest.builder()
                .firstName("User2")
                .lastName("User2")
                .email("user@test.com")
                .phoneNumber("222222222")
                .password("password222")
                .role(Role.ATTENDEE)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Email already exists")))
                .andExpect(jsonPath("$.token").doesNotExist());
    }

    @Test
    void shouldLoginExistingUser() throws Exception {
        //rejestrujemy uzytkownika zeby miec kogo logowac
        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstName("Jan")
                .lastName("Janowski")
                .email("jan@test.com")
                .phoneNumber("123456789")
                .password("password123")
                .role(Role.ORGANIZER)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        //próba logowania
        AuthRequest loginRequest = new AuthRequest("jan@test.com", "password123");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.message", is("Login successful")));
    }
    @Test
    void shouldFailLoginWithWrongPassword() throws Exception {
        // rejestrujemy usera
        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstName("Jan")
                .lastName("Janowski")
                .email("jan@test.com")
                .phoneNumber("123456789")
                .password("password123")
                .role(Role.ATTENDEE)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        // probujemy sie zalogowac ze zlym haslem
        AuthRequest badLogin = new AuthRequest("jan@test.com", "zleHaslo321");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badLogin)))
                .andExpect(status().isUnauthorized()); //spring security domyslnie wyrzuca 401
    }

}
