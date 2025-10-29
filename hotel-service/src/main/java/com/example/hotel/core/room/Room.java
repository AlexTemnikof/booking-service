package com.example.hotel.core.room;

import com.example.hotel.core.hotel.Hotel;
import jakarta.persistence.*;

@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String number;

    private int capacity;

    private long timesBooked;

    private boolean available = true;

    @ManyToOne(fetch = FetchType.LAZY)
    private Hotel hotel;

    public Long getId() {
        return id;
    }

    public Room withId(final Long id) {
        this.id = id;
        return this;
    }

    public String getNumber() {
        return number;
    }

    public Room withNumber(final String number) {
        this.number = number;
        return this;
    }

    public int getCapacity() {
        return capacity;
    }

    public Room withCapacity(final int capacity) {
        this.capacity = capacity;
        return this;
    }

    public long getTimesBooked() {
        return timesBooked;
    }

    public Room withTimesBooked(final long timesBooked) {
        this.timesBooked = timesBooked;
        return this;
    }

    public boolean isAvailable() {
        return available;
    }

    public Room withAvailable(final boolean available) {
        this.available = available;
        return this;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public Room withHotel(final Hotel hotel) {
        this.hotel = hotel;
        return this;
    }
}


