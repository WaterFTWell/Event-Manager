package com.example.Event_Manager.models.venue;

import com.example.Event_Manager.models.city.City;
import com.example.Event_Manager.models.event.Event;
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
@Table(name = "venues")
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "city_id", referencedColumnName = "id")
    private City city;

    @Column(nullable = false, name= "name")
    private String name;

    @Column(nullable = false, name= "address")
    private String address;

    @Column(nullable = false, length = 500, name = "description", columnDefinition = "TEXT")
    private String description;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "venue", cascade = CascadeType.ALL)
    private Set<Event> events;

}
