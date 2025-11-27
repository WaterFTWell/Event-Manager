package com.example.Event_Manager.integration;

import com.example.Event_Manager.auth.repository.UserRepository;
import com.example.Event_Manager.auth.util.JwtUtil;
import com.example.Event_Manager.models.user.User;
import com.example.Event_Manager.models.user.enums.Role;
import com.example.Event_Manager.models.user.enums.Status;
import com.example.Event_Manager.models.category.Category;
import com.example.Event_Manager.models.category.dto.request.CreateCategoryDTO;
import com.example.Event_Manager.models.category.dto.request.UpdateCategoryDTO;
import com.example.Event_Manager.models.category.repository.CategoryRepository;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integration.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc()
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Transactional
public class CategoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private User adminUser;
    private String adminToken;

    @BeforeEach
    void setup() {
        categoryRepository.deleteAll();
        userRepository.deleteAll();
        adminUser = createAndSaveAdminUser("admin@event.com", "123456789");
        adminToken = jwtUtil.generateToken(adminUser);
    }

    private User createAndSaveAdminUser(String email, String phone) {
        User user = User.builder()
                .firstName("Admin")
                .lastName("User")
                .email(email)
                .phoneNumber(phone)
                .password("password")
                .role(Role.ADMIN)
                .status(Status.ACTIVE)
                .build();
        return userRepository.save(user);
    }

    private Category createAndSaveCategory(String name, String description) {
        Category category = Category.builder()
                .name(name)
                .description(description)
                .build();
        return categoryRepository.save(category);
    }

    @Test
    @DisplayName("Should return 409 Conflict when creating category with duplicate name")
    void shouldReturnConflictWhenCreatingCategoryWithDuplicateName() throws Exception {
        createAndSaveCategory("Sport", "Wydarzenia sportowe");
        CreateCategoryDTO createDto = new CreateCategoryDTO("Sport", "Inny opis dla kategorii sportowej");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/categories")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Should update category successfully")
    void shouldUpdateCategory() throws Exception {
        Category category = createAndSaveCategory("Sztuka", "Wystawy i wernisaże");
        UpdateCategoryDTO updateDto = new UpdateCategoryDTO("Sztuka Nowoczesna", "Wystawy sztuki współczesnej");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/categories/" + category.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Sztuka Nowoczesna")))
                .andExpect(jsonPath("$.description", is("Wystawy sztuki współczesnej")));
    }

    @Test
    @DisplayName("Should delete category successfully")
    void shouldDeleteCategory() throws Exception {
        Category category = createAndSaveCategory("Teatr", "Spektakle teatralne");

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/categories/" + category.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/categories/" + category.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return a list of all categories")
    void shouldGetAllCategories() throws Exception {
        createAndSaveCategory("Biznes", "Konferencje i spotkania biznesowe");
        createAndSaveCategory("Edukacja", "Warsztaty i kursy");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Biznes")))
                .andExpect(jsonPath("$[1].name", is("Edukacja")));
    }

    @Test
    @DisplayName("Should return 404 Not Found for a missing category ID")
    void shouldReturnNotFoundForMissingCategory() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/categories/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 404 Not Found when getting all categories and none exist")
    void shouldReturnNotFoundWhenNoCategoriesExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/categories"))
                .andExpect(status().isNotFound());
    }
}