package com.example.hotel.web;

import com.example.hotel.core.room.Room;
import com.example.hotel.core.reservation.RoomReservationLock;
import com.example.hotel.core.hotel.HotelServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/rooms")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearer-jwt")
public class RoomController {

    private static final String SCOPE_ADMIN = "hasAuthority('SCOPE_ADMIN')";

    private final HotelServiceImpl hotelService;

    public RoomController(final HotelServiceImpl hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoom(@PathVariable final Long id) {
        return hotelService.getRoom(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize(SCOPE_ADMIN)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Room createRoom(@Valid @RequestBody final Room room) {
        return hotelService.saveRoom(room);
    }

    @PreAuthorize(SCOPE_ADMIN)
    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable final Long id, @Valid @RequestBody final Room room) {
        return hotelService.getRoom(id)
                .map(existing -> {
                    room.withId(id);
                    final Room updatedRoom = hotelService.saveRoom(room);
                    return ResponseEntity.ok(updatedRoom);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize(SCOPE_ADMIN)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteRoom(@PathVariable final Long id) {
        hotelService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{id}/hold")
    public ResponseEntity<RoomReservationLock> holdRoom(
            @PathVariable final Long id,
            @Valid @RequestBody final HoldRequest request) {

        try {
            final RoomReservationLock lock = hotelService.holdRoom(
                    request.requestId(), id, request.startDate(), request.endDate());
            return ResponseEntity.ok(lock);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<RoomReservationLock> confirmHold(
            @PathVariable final Long id,
            @Valid @RequestBody final IdRequest request) {

        try {
            final RoomReservationLock lock = hotelService.confirmHold(request.requestId());
            return ResponseEntity.ok(lock);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/{id}/release")
    public ResponseEntity<RoomReservationLock> releaseHold(
            @PathVariable Long id,
            @Valid @RequestBody final IdRequest request) {

        try {
            final RoomReservationLock lock = hotelService.releaseHold(request.requestId());
            return ResponseEntity.ok(lock);
        } catch (IllegalStateException e) {
            return ResponseEntity.notFound().build();
        }
    }
}