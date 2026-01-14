package com.example.hotel.service;

import com.example.hotel.dto.RoomDto;
import com.example.hotel.repo.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsService {
    private final RoomRepository roomRepository;

    public List<RoomDto> getPopularRooms() {
        log.debug("Fetching popular rooms sorted by booking count");
        return roomRepository.findAll().stream()
                .sorted(Comparator.comparingLong(room -> -room.getTimesBooked())) // Descending order
                .map(RoomDto::fromEntity)
                .collect(Collectors.toList());
    }
}