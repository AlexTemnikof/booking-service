package com.example.hotel.web;

import com.example.hotel.dto.HotelDto;
import com.example.hotel.dto.RoomDto;
import com.example.hotel.service.HotelManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
@Tag(name = "Hotels", description = "Hotel management operations")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearer-jwt")
public class HotelController {
    private final HotelManagementService hotelService;

    @GetMapping
    @Operation(summary = "Get all hotels", description = "Returns a list of all hotels without room details")
    public List<HotelDto> getAllHotels() {
        return hotelService.getAllHotels();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get hotel by ID", description = "Returns a hotel by its ID with room details")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.getHotelById(id));
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create hotel", description = "Creates a new hotel (admin only)")
    public HotelDto createHotel(@Valid @RequestBody HotelDto hotelDto) {
        return hotelService.createHotel(hotelDto);
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update hotel", description = "Updates an existing hotel (admin only)")
    public ResponseEntity<HotelDto> updateHotel(@PathVariable Long id, @Valid @RequestBody HotelDto hotelDto) {
        return ResponseEntity.ok(hotelService.updateHotel(id, hotelDto));
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete hotel", description = "Deletes a hotel by its ID (admin only)")
    public void deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotel(id);
    }

    @GetMapping("/rooms")
    @Operation(summary = "Get all rooms", description = "Returns a list of all rooms across all hotels")
    public List<RoomDto> getAllRooms() {
        return hotelService.getAllRooms();
    }
}
