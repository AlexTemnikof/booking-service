package com.example.hotel.dto;

import com.example.hotel.model.Room;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {
    private Long id;
    private String number;
    private int capacity;
    private long timesBooked;
    private boolean available;
    private Long hotelId;

    public static RoomDto fromEntity(Room room) {
        return RoomDto.builder()
                .id(room.getId())
                .number(room.getNumber())
                .capacity(room.getCapacity())
                .timesBooked(room.getTimesBooked())
                .available(room.isAvailable())
                .hotelId(room.getHotel() != null ? room.getHotel().getId() : null)
                .build();
    }

    public Room toEntity() {
        Room room = Room.builder()
                .id(id)
                .number(number)
                .capacity(capacity)
                .timesBooked(timesBooked)
                .available(available)
                .build();
        
        return room;
    }
}