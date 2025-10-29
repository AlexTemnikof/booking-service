package com.example.hotel.core.hotel;

import com.example.hotel.core.room.Room;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String city;

    private String address;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Room> rooms = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public Hotel withId(final Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Hotel withName(final String name) {
        this.name = name;
        return this;
    }

    public String getCity() {
        return city;
    }

    public Hotel withCity(final String city) {
        this.city = city;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public Hotel withAddress(final String address) {
        this.address = address;
        return this;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public Hotel withRooms(final List<Room> rooms) {
        this.rooms = rooms;
        return this;
    }
}


