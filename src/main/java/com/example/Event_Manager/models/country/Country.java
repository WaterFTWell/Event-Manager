package com.example.Event_Manager.models.country;

import com.example.Event_Manager.models.city.City;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "countries")
public class Country {

    @Id
    @Column(name = "code")
    private String code;

    @Column(nullable = false, name = "name")
    private String name;

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<City> cities;
}
