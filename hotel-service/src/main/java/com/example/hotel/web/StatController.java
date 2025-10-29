package com.example.hotel.web;

import com.example.hotel.core.room.Room;
import com.example.hotel.core.room.RoomRepository;
import com.example.hotel.core.room.RoomServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/stat")
public class StatController {
    private final RoomServiceImpl roomService;

    public StatController(final RoomServiceImpl roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/popular-rooms")
    public List<Room> popularRooms() {
        return roomService.findAll().stream()
                .sorted(Comparator.comparingLong(Room::withTimesBooked).reversed())
                .toList();
    }
}


