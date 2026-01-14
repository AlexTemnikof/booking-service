package com.example.hotel.web;

import com.example.hotel.dto.RequestIdDto;
import com.example.hotel.dto.RoomDto;
import com.example.hotel.dto.RoomReservationDto;
import com.example.hotel.dto.RoomReservationRequestDto;
import com.example.hotel.service.HotelManagementService;
import com.example.hotel.service.RoomReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
@Tag(name = "Rooms", description = "Room management and reservation operations")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearer-jwt")
public class RoomController {
    private final HotelManagementService hotelService;
    private final RoomReservationService reservationService;

    @GetMapping("/{id}")
    @Operation(summary = "Get room by ID", description = "Returns a room by its ID")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.getRoomById(id));
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create room", description = "Creates a new room (admin only)")
    public RoomDto createRoom(@Valid @RequestBody RoomDto roomDto) {
        return hotelService.createRoom(roomDto);
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update room", description = "Updates an existing room (admin only)")
    public ResponseEntity<RoomDto> updateRoom(@PathVariable Long id, @Valid @RequestBody RoomDto roomDto) {
        return ResponseEntity.ok(hotelService.updateRoom(id, roomDto));
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete room", description = "Deletes a room by its ID (admin only)")
    public void deleteRoom(@PathVariable Long id) {
        hotelService.deleteRoom(id);
    }

    // Reservation operations
    @PostMapping("/{id}/hold")
    @Operation(summary = "Hold room", description = "Places a temporary hold on a room for reservation")
    public ResponseEntity<RoomReservationDto> holdRoom(
            @PathVariable Long id, 
            @Valid @RequestBody RoomReservationRequestDto request) {

        RoomReservationDto reservation = reservationService.holdRoom(
                request.getRequestId(), 
                id, 
                request.getStartDate(), 
                request.getEndDate()
        );

        return ResponseEntity.ok(reservation);
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "Confirm hold", description = "Confirms a previously held room reservation")
    public ResponseEntity<RoomReservationDto> confirmHold(
            @PathVariable Long id, 
            @Valid @RequestBody RequestIdDto request) {

        return ResponseEntity.ok(reservationService.confirmHold(request.getRequestId()));
    }

    @PostMapping("/{id}/release")
    @Operation(summary = "Release hold", description = "Releases a previously held room reservation")
    public ResponseEntity<RoomReservationDto> releaseHold(
            @PathVariable Long id, 
            @Valid @RequestBody RequestIdDto request) {

        return ResponseEntity.ok(reservationService.releaseHold(request.getRequestId()));
    }
}
