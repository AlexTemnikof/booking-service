package com.example.hotel.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "hotel")
@EqualsAndHashCode(exclude = "hotel")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String number;

    private int capacity;

    private long timesBooked;

    @Builder.Default
    private boolean available = true;

    @ManyToOne(fetch = FetchType.LAZY)
    private Hotel hotel;
}
