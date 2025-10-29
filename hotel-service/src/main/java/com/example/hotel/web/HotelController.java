package com.example.hotel.web;

import com.example.hotel.core.hotel.Hotel;
import com.example.hotel.core.hotel.HotelServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/hotels")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearer-jwt")
public class HotelController {

    private static final String SCOPE_ADMIN = "hasAuthority('SCOPE_ADMIN')";

    private final HotelServiceImpl hotelService;

    public HotelController(HotelServiceImpl hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping
    public List<Hotel> getAllHotels() {
        return hotelService.listHotels();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hotel> getHotel(@PathVariable final Long id) {
        return hotelService.getHotel(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize(SCOPE_ADMIN)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Hotel createHotel(@Valid @RequestBody final Hotel hotel) {
        return hotelService.saveHotel(hotel);
    }

    @PreAuthorize(SCOPE_ADMIN)
    @PutMapping("/{id}")
    public ResponseEntity<Hotel> updateHotel(@PathVariable final Long id, @Valid @RequestBody final Hotel hotel) {
        return hotelService.getHotel(id)
                .map(existing -> {
                    hotel.withId(id);
                    final Hotel updatedHotel = hotelService.saveHotel(hotel);
                    return ResponseEntity.ok(updatedHotel);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize(SCOPE_ADMIN)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteHotel(@PathVariable final Long id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }
}