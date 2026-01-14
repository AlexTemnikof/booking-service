package com.example.hotel.dto;

import com.example.hotel.model.Hotel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelDto {
    private Long id;
    private String name;
    private String city;
    private String address;
    private List<RoomDto> rooms;

    public static HotelDto fromEntity(Hotel hotel) {
        return HotelDto.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .city(hotel.getCity())
                .address(hotel.getAddress())
                .rooms(hotel.getRooms() != null ? 
                        hotel.getRooms().stream()
                                .map(RoomDto::fromEntity)
                                .collect(Collectors.toList()) : 
                        null)
                .build();
    }

    public static HotelDto fromEntityWithoutRooms(Hotel hotel) {
        return HotelDto.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .city(hotel.getCity())
                .address(hotel.getAddress())
                .build();
    }

    public Hotel toEntity() {
        return Hotel.builder()
                .id(id)
                .name(name)
                .city(city)
                .address(address)
                .build();
    }
}