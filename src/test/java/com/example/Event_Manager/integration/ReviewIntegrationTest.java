package com.example.Event_Manager.integration;

import com.example.Event_Manager.auth.repository.UserRepository;
import com.example.Event_Manager.auth.util.JwtUtil;
import com.example.Event_Manager.models.category.Category;
import com.example.Event_Manager.models.category.repository.CategoryRepository;
import com.example.Event_Manager.models.event.Event;
import com.example.Event_Manager.models.event.repository.EventRepository;
import com.example.Event_Manager.models.review.Review;
import com.example.Event_Manager.models.review.dto.request.CreateReviewDTO;
import com.example.Event_Manager.models.review.dto.request.UpdateReviewDTO;
import com.example.Event_Manager.models.review.repository.ReviewRepository;
import com.example.Event_Manager.models.user.User;
import com.example.Event_Manager.models.user.enums.Role;
import com.example.Event_Manager.models.user.enums.Status;
import com.example.Event_Manager.models.venue.Venue;
import com.example.Event_Manager.models.venue.repository.VenueRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static com.example.Event_Manager.models.event.enums.Status.PUBLISHED;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integration.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Transactional
public class ReviewIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private VenueRepository venueRepository;

    private User testUser;
    private Event testEvent;
    private Category testCategory;
    private Venue testVenue;
    private String testUserToken;

    @BeforeEach
    void setup() {
        reviewRepository.deleteAll();
        eventRepository.deleteAll();
        userRepository.deleteAll();
        venueRepository.deleteAll();
        categoryRepository.deleteAll();

        testUser = createAndSaveUser("testuser@example.com", "123456789");
        testCategory = createAndSaveCategory("Test Category");
        testVenue = createAndSaveVenue("Test Venue");
        testEvent = createAndSaveEvent("Test Event", testUser, testCategory, testVenue);

        // jwt dla usera testowego
        testUserToken = jwtUtil.generateToken(testUser);
    }

    private User createAndSaveUser(String email, String phone) {
        User user = User.builder()
                .firstName("Test")
                .lastName("User")
                .email(email)
                .phoneNumber(phone)
                .password("password")
                .role(Role.ATTENDEE)
                .status(Status.ACTIVE)
                .build();
        return userRepository.save(user);
    }

    private Category createAndSaveCategory(String name) {
        Category category = Category.builder()
                .name(name)
                .description("Test description")
                .build();
        return categoryRepository.save(category);
    }

    private Venue createAndSaveVenue(String name) {
        Venue venue = Venue.builder()
                .name(name)
                .address("Test Address")
                .description("Test venue description")
                .build();
        return venueRepository.save(venue);
    }

    private Event createAndSaveEvent(String name, User user, Category category, Venue venue) {
        LocalDateTime now = LocalDateTime.now();
        Event event = Event.builder()
                .name(name)
                .description("Event description")
                .organizer(user)
                .category(category)
                .venue(venue)
                .startTime(Date.from(now.plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
                .endTime(Date.from(now.plusDays(1).plusHours(2).atZone(ZoneId.systemDefault()).toInstant()))
                .status(PUBLISHED)
                .build();
        return eventRepository.save(event);
    }

    private Review createAndSaveReview(Event event, User user, int rating, String comment) {
        Review review = Review.builder()
                .event(event)
                .user(user)
                .rating(rating)
                .comment(comment)
                .build();
        return reviewRepository.save(review);
    }

    @Test
    @DisplayName("Should create review when user is authenticated")
    void shouldCreateReviewWhenAuthenticated() throws Exception {
        CreateReviewDTO createDto = new CreateReviewDTO(testEvent.getId(), 8L, 5,"Great authenticated event!");

        mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + testUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.comment", is("Great authenticated event!")))
                .andExpect(jsonPath("$.rating", is(5)));
    }

    @Test
    @DisplayName("Should update review when user is authenticated")
    void shouldUpdateReviewWhenAuthenticated() throws Exception {
        Review review = createAndSaveReview(testEvent, testUser, 5, "Initial comment");
        UpdateReviewDTO updateDto = new UpdateReviewDTO(9L,2,"Updated comment");

        mockMvc.perform(put("/api/reviews/" + review.getId())
                        .header("Authorization", "Bearer " + testUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment", is("Updated comment")))
                .andExpect(jsonPath("$.rating", is(2)));
    }

    @Test
    @DisplayName("Should delete review when user is authenticated")
    void shouldDeleteReviewWhenAuthenticated() throws Exception {
        Review review = createAndSaveReview(testEvent, testUser, 7, "To be deleted");

        mockMvc.perform(delete("/api/reviews/" + review.getId())
                        .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isNoContent());

        assertFalse(reviewRepository.existsById(review.getId()), "Review should be deleted from the database");
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when creating review without user")
    void shouldReturnUnauthorizedWhenCreatingReviewWithoutUser() throws Exception {
        CreateReviewDTO createDto = new CreateReviewDTO(testEvent.getId(), 8L,7, "Great event!");

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when updating review without user")
    void shouldReturnUnauthorizedWhenUpdatingReviewWithoutUser() throws Exception {
        Review review = createAndSaveReview(testEvent, testUser, 5, "Initial comment");
        UpdateReviewDTO updateDto = new UpdateReviewDTO(9L,10, "Updated comment");

        mockMvc.perform(put("/api/reviews/" + review.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when deleting review without user")
    void shouldReturnUnauthorizedWhenDeletingReviewWithoutUser() throws Exception {
        Review review = createAndSaveReview(testEvent, testUser, 7, "To be deleted");

        mockMvc.perform(delete("/api/reviews/" + review.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should get reviews for a specific event")
    void shouldGetReviewsForEvent() throws Exception {
        createAndSaveReview(testEvent, testUser, 8, "First review");
        User anotherUser = createAndSaveUser("another@user.com", "987654321");
        createAndSaveReview(testEvent, anotherUser, 6, "Second review");

        mockMvc.perform(get("/api/reviews/event/" + testEvent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].comment", is("First review")))
                .andExpect(jsonPath("$[1].rating", is(6)));
    }

    @Test
    @DisplayName("Should return 404 Not Found when getting reviews for a non-existent event")
    void shouldReturnNotFoundWhenGettingReviewsForMissingEvent() throws Exception {
        mockMvc.perform(get("/api/reviews/event/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should get event review summary successfully")
    void shouldGetEventReviewSummary() throws Exception {
        createAndSaveReview(testEvent, testUser, 10, "Perfect!");
        User anotherUser = createAndSaveUser("another@user.com", "987654321");
        createAndSaveReview(testEvent, anotherUser, 6, "It was okay");

        mockMvc.perform(get("/api/reviews/event/" + testEvent.getId() + "/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName", is(testEvent.getName())))
                .andExpect(jsonPath("$.totalReviews", is(2)))
                .andExpect(jsonPath("$.averageRating", is(8.0)));
    }
}