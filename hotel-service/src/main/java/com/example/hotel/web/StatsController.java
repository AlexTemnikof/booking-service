package com.example.hotel.web;

import com.example.hotel.dto.RoomDto;
import com.example.hotel.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
@Tag(name = "Statistics", description = "Hotel and room statistics operations")
public class StatsController {
    private final StatsService statsService;

    @GetMapping("/rooms/popular")
    @Operation(summary = "Get popular rooms", description = "Returns a list of rooms sorted by booking count (most popular first)")
    public List<RoomDto> getPopularRooms() {
        return statsService.getPopularRooms();
    }
}
