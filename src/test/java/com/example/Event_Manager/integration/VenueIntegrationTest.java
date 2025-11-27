package com.example.Event_Manager.integration;

import com.example.Event_Manager.auth.repository.UserRepository;
import com.example.Event_Manager.auth.util.JwtUtil;
import com.example.Event_Manager.models.city.City;
import com.example.Event_Manager.models.city.repository.CityRepository;
import com.example.Event_Manager.models.country.Country;
import com.example.Event_Manager.models.country.repository.CountryRepository;
import com.example.Event_Manager.models.user.User;
import com.example.Event_Manager.models.user.enums.Role;
import com.example.Event_Manager.models.user.enums.Status;
import com.example.Event_Manager.models.venue.Venue;
import com.example.Event_Manager.models.venue.dto.request.CreateVenueDTO;
import com.example.Event_Manager.models.venue.dto.request.UpdateVenueDTO;
import com.example.Event_Manager.models.venue.repository.VenueRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integration.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc()
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Transactional
public class VenueIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private String adminToken;
    private String attendeeToken;
    private City city;
    private City city2;

    @BeforeEach
    void setup() {
        venueRepository.deleteAll();
        cityRepository.deleteAll();
        countryRepository.deleteAll();
        userRepository.deleteAll();

        User adminUser = createAndSaveUser("Admin", "admin@event.com", "123456789", Role.ADMIN);
        adminToken = jwtUtil.generateToken(adminUser);

        User user = createAndSaveUser("Attendee", "user@event.com", "987654321", Role.ATTENDEE);
        attendeeToken = jwtUtil.generateToken(user);

        Country country = createAndSaveCountry("PL", "Poland");
        city = createAndSaveCity("Warsaw", country);
        city2 = createAndSaveCity("Krakow", country);
    }

    private User createAndSaveUser(
            String firstName,
            String email,
            String phone,
            Role role
    ) {
        User user = User.builder()
                .firstName(firstName)
                .lastName("User")
                .email(email)
                .phoneNumber(phone)
                .password("password")
                .role(role)
                .status(Status.ACTIVE)
                .build();

        return userRepository.save(user);
    }

    private Country createAndSaveCountry(String code, String name) {
        Country country = Country.builder()
                .code(code)
                .name(name)
                .build();
        return countryRepository.save(country);
    }

    private City createAndSaveCity(String name, Country country) {
        City city = City.builder()
                .name(name)
                .country(country)
                .build();
        return cityRepository.save(city);
    }

    private Venue createAndSaveVenue(String name, String address, String description, City city) {
        Venue venue = Venue.builder()
                .name(name)
                .address(address)
                .description(description)
                .city(city)
                .build();
        return venueRepository.save(venue);
    }

    @Test
    void create_validData_returnsCreatedAndPersistsToDatabase() throws Exception {
        CreateVenueDTO createDto = new CreateVenueDTO(
                "PGE Narodowy",
                "Al. Poniatowskiego 1",
                "Stadion Narodowy w Warszawie",
                city.getId()
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/venues")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("PGE Narodowy")))
                .andExpect(jsonPath("$.address", is("Al. Poniatowskiego 1")))
                .andExpect(jsonPath("$.description", is("Stadion Narodowy w Warszawie")))
                .andExpect(jsonPath("$.city.name", is("Warsaw")));

        Optional<Venue> savedVenue = venueRepository.findAll().stream()
                .filter(v -> v.getName().equals("PGE Narodowy"))
                .findFirst();
        assertThat(savedVenue).isPresent();
        assertThat(savedVenue.get().getAddress()).isEqualTo("Al. Poniatowskiego 1");
    }

    @Test
    void create_nonExistentCity_returnsNotFound() throws Exception {
        CreateVenueDTO createDto = new CreateVenueDTO(
                "Test Venue",
                "Test Address",
                "Test Description",
                9999L
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/venues")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isNotFound());

        assertThat(venueRepository.findAll()).isEmpty();
    }

    @Test
    void update_validData_returnsOkAndPersistsChanges() throws Exception {
        Venue venue = createAndSaveVenue(
                "Tauron Arena",
                "ul. Lecha 1",
                "Hala widowiskowo-sportowa",
                city
        );
        Long venueId = venue.getId();

        UpdateVenueDTO updateDto = new UpdateVenueDTO(
                "Tauron Arena Krakow",
                "ul. Lecha 1",
                "Nowoczesna hala widowiskowo-sportowa w Krakowie",
                city.getId()
        );

        mockMvc.perform(MockMvcRequestBuilders.put("/api/venues/" + venueId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Tauron Arena Krakow")))
                .andExpect(jsonPath("$.description", is("Nowoczesna hala widowiskowo-sportowa w Krakowie")));

        Venue updatedVenue = venueRepository.findById(venueId).orElseThrow();
        assertThat(updatedVenue.getName()).isEqualTo("Tauron Arena Krakow");
        assertThat(updatedVenue.getDescription()).isEqualTo("Nowoczesna hala widowiskowo-sportowa w Krakowie");
    }

    @Test
    void update_nonExistentVenue_returnsNotFound() throws Exception {
        UpdateVenueDTO updateDto = new UpdateVenueDTO(
                "Venue",
                "Test Address",
                "Test Description",
                city.getId()
        );

        mockMvc.perform(MockMvcRequestBuilders.put("/api/venues/9999")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_existingVenue_returnsNoContentAndRemovesFromDatabase() throws Exception {
        Venue venue = createAndSaveVenue(
                "Spodek",
                "ul. Korfantego 35",
                "Kultowa hala widowiskowo-sportowa",
                city
        );
        Long venueId = venue.getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/venues/" + venueId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        Optional<Venue> deletedVenue = venueRepository.findById(venueId);
        assertThat(deletedVenue).isEmpty();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/venues/" + venueId))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_nonExistentVenue_returnsNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/venues/9999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getById_existingVenue_returnsOk() throws Exception {
        Venue venue = createAndSaveVenue(
                "PGE Narodowy",
                "Al. Poniatowskiego 1",
                "Stadion Narodowy w Warszawie",
                city
        );

        mockMvc.perform(MockMvcRequestBuilders.get("/api/venues/" + venue.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("PGE Narodowy")))
                .andExpect(jsonPath("$.address", is("Al. Poniatowskiego 1")))
                .andExpect(jsonPath("$.city.name", is("Warsaw")));
    }

    @Test
    void getById_nonExistentVenue_returnsNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/venues/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_multipleVenues_returnsPaginatedList() throws Exception {
        createAndSaveVenue("PGE Narodowy", "Al. Poniatowskiego 1", "Stadion Narodowy", city);
        createAndSaveVenue("Tauron Arena", "ul. Lecha 1", "Hala sportowa", city);
        createAndSaveVenue("Spodek", "ul. Korfantego 35", "Hala widowiskowa", city);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/venues"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].name", is("PGE Narodowy")))
                .andExpect(jsonPath("$.content[1].name", is("Tauron Arena")))
                .andExpect(jsonPath("$.content[2].name", is("Spodek")))
                .andExpect(jsonPath("$.totalElements", is(3)));
    }

    @Test
    void getAll_withPagination_returnsCorrectPage() throws Exception {
        createAndSaveVenue("Venue 1", "Address 1", "Description 1", city);
        createAndSaveVenue("Venue 2", "Address 2", "Description 2", city);
        createAndSaveVenue("Venue 3", "Address 3", "Description 3", city);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/venues?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(3)))
                .andExpect(jsonPath("$.totalPages", is(2)))
                .andExpect(jsonPath("$.number", is(0)));
    }

    @Test
    void getAll_filterByName_returnsMatchingVenues() throws Exception {
        createAndSaveVenue("PGE Narodowy", "Al. Poniatowskiego 1", "Stadion", city);
        createAndSaveVenue("Tauron Arena", "ul. Lecha 1", "Arena", city);
        createAndSaveVenue("Spodek", "ul. Korfantego 35", "Hala", city);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/venues?name=Arena"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Tauron Arena")));
    }

    @Test
    void getAll_filterByCities_returnsMatchingVenues() throws Exception {

        createAndSaveVenue("Warsaw Venue", "Address 1", "Description 1", city);
        createAndSaveVenue("Krakow Venue", "Address 2", "Description 2", city2);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/venues?cities=" + city2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Krakow Venue")))
                .andExpect(jsonPath("$.content[0].city.name", is("Krakow")));
    }

    @Test
    void getAll_filterByNameAndCities_returnsMatchingVenues() throws Exception {
        createAndSaveVenue("Arena Warsaw", "Address 1", "Description 1", city);
        createAndSaveVenue("Arena Krakow", "Address 2", "Description 2", city2);
        createAndSaveVenue("Stadium Krakow", "Address 3", "Description 3", city2);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/venues?name=Arena&cities=" + city2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Arena Krakow")));
    }

    @Test
    void getAll_noVenues_returnsEmptyPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/venues"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));
    }

    @Test
    void getAll_noMatchingFilter_returnsEmptyPage() throws Exception {
        createAndSaveVenue("PGE Narodowy", "Al. Poniatowskiego 1", "Stadion", city);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/venues?name=NonExistingVenue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));
    }

    @Test
    void create_noAuthentication_returnsUnauthorized() throws Exception {
        CreateVenueDTO createDto = new CreateVenueDTO(
                "Test Venue",
                "Test Address",
                "Test Description",
                city.getId()
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isUnauthorized());

        assertThat(venueRepository.findAll()).isEmpty();
    }

    @Test
    void update_noAuthentication_returnsUnauthorized() throws Exception {
        Venue venue = createAndSaveVenue("Test Venue", "Test Address", "Test Description", city);
        UpdateVenueDTO updateDto = new UpdateVenueDTO(
                "Updated Venue",
                "Updated Address",
                "Updated Description",
                city.getId()
        );

        mockMvc.perform(MockMvcRequestBuilders.put("/api/venues/" + venue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isUnauthorized());

        Venue unchangedVenue = venueRepository.findById(venue.getId()).orElseThrow();
        assertThat(unchangedVenue.getName()).isEqualTo("Test Venue");
    }

    @Test
    void delete_noAuthentication_returnsUnauthorized() throws Exception {
        Venue venue = createAndSaveVenue("Test Venue", "Test Address", "Test Description", city);
        Long venueId = venue.getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/venues/" + venueId))
                .andExpect(status().isUnauthorized());

        Optional<Venue> stillExists = venueRepository.findById(venueId);
        assertThat(stillExists).isPresent();
    }

    @Test
    void create_notAdmin_returnsForbidden() throws Exception {
        CreateVenueDTO createDto = new CreateVenueDTO(
                "Test Venue",
                "Test Address",
                "Test Description",
                city.getId()
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/venues")
                        .header("Authorization", "Bearer " + attendeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isForbidden());

        assertThat(venueRepository.findAll()).isEmpty();
    }

    @Test
    void update_notAdmin_returnsForbidden() throws Exception {
        Venue venue = createAndSaveVenue("Test Venue", "Test Address", "Test Description", city);
        UpdateVenueDTO updateDto = new UpdateVenueDTO(
                "Updated Venue",
                "Updated Address",
                "Updated Description",
                city.getId()
        );

        mockMvc.perform(MockMvcRequestBuilders.put("/api/venues/" + venue.getId())
                        .header("Authorization", "Bearer " + attendeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isForbidden());

        Venue unchangedVenue = venueRepository.findById(venue.getId()).orElseThrow();
        assertThat(unchangedVenue.getName()).isEqualTo("Test Venue");
    }

    @Test
    void delete_notAdmin_returnsForbidden() throws Exception {
        Venue venue = createAndSaveVenue("Test Venue", "Test Address", "Test Description", city);
        Long venueId = venue.getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/venues/" + venueId)
                        .header("Authorization", "Bearer " + attendeeToken))
                .andExpect(status().isForbidden());

        Optional<Venue> stillExists = venueRepository.findById(venueId);
        assertThat(stillExists).isPresent();
    }
}
