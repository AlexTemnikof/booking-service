package com.example.hotel.service;

import com.example.hotel.dto.HotelDto;
import com.example.hotel.dto.RoomDto;
import com.example.hotel.exception.HotelServiceException;
import com.example.hotel.model.Hotel;
import com.example.hotel.model.Room;
import com.example.hotel.repo.HotelRepository;
import com.example.hotel.repo.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelManagementService {
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;

    // Hotel operations
    public List<HotelDto> getAllHotels() {
        log.debug("Fetching all hotels");
        return hotelRepository.findAll().stream()
                .map(HotelDto::fromEntityWithoutRooms)
                .collect(Collectors.toList());
    }

    public HotelDto getHotelById(Long id) {
        log.debug("Fetching hotel with id: {}", id);
        return hotelRepository.findById(id)
                .map(HotelDto::fromEntity)
                .orElseThrow(() -> HotelServiceException.entityNotFound("Hotel", id));
    }

    @Transactional
    public HotelDto createHotel(HotelDto hotelDto) {
        log.debug("Creating new hotel: {}", hotelDto.getName());
        Hotel hotel = hotelDto.toEntity();
        return HotelDto.fromEntity(hotelRepository.save(hotel));
    }

    @Transactional
    public HotelDto updateHotel(Long id, HotelDto hotelDto) {
        log.debug("Updating hotel with id: {}", id);
        
        return hotelRepository.findById(id)
                .map(existingHotel -> {
                    Hotel hotel = hotelDto.toEntity();
                    hotel.setId(id);
                    hotel.setRooms(existingHotel.getRooms()); // Preserve existing rooms
                    return HotelDto.fromEntity(hotelRepository.save(hotel));
                })
                .orElseThrow(() -> HotelServiceException.entityNotFound("Hotel", id));
    }

    @Transactional
    public void deleteHotel(Long id) {
        log.debug("Deleting hotel with id: {}", id);
        
        if (!hotelRepository.existsById(id)) {
            throw HotelServiceException.entityNotFound("Hotel", id);
        }
        
        hotelRepository.deleteById(id);
    }

    // Room operations
    public List<RoomDto> getAllRooms() {
        log.debug("Fetching all rooms");
        return roomRepository.findAll().stream()
                .map(RoomDto::fromEntity)
                .collect(Collectors.toList());
    }

    public RoomDto getRoomById(Long id) {
        log.debug("Fetching room with id: {}", id);
        return roomRepository.findById(id)
                .map(RoomDto::fromEntity)
                .orElseThrow(() -> HotelServiceException.entityNotFound("Room", id));
    }

    @Transactional
    public RoomDto createRoom(RoomDto roomDto) {
        log.debug("Creating new room: {} in hotel: {}", roomDto.getNumber(), roomDto.getHotelId());
        
        Hotel hotel = hotelRepository.findById(roomDto.getHotelId())
                .orElseThrow(() -> HotelServiceException.entityNotFound("Hotel", roomDto.getHotelId()));
        
        Room room = roomDto.toEntity();
        room.setHotel(hotel);
        
        return RoomDto.fromEntity(roomRepository.save(room));
    }

    @Transactional
    public RoomDto updateRoom(Long id, RoomDto roomDto) {
        log.debug("Updating room with id: {}", id);
        
        return roomRepository.findById(id)
                .map(existingRoom -> {
                    Room room = roomDto.toEntity();
                    room.setId(id);
                    
                    // If hotel ID changed, update the hotel reference
                    if (roomDto.getHotelId() != null && 
                        (existingRoom.getHotel() == null || 
                         !roomDto.getHotelId().equals(existingRoom.getHotel().getId()))) {
                        
                        Hotel hotel = hotelRepository.findById(roomDto.getHotelId())
                                .orElseThrow(() -> HotelServiceException.entityNotFound("Hotel", roomDto.getHotelId()));
                        room.setHotel(hotel);
                    } else {
                        room.setHotel(existingRoom.getHotel());
                    }
                    
                    return RoomDto.fromEntity(roomRepository.save(room));
                })
                .orElseThrow(() -> HotelServiceException.entityNotFound("Room", id));
    }

    @Transactional
    public void deleteRoom(Long id) {
        log.debug("Deleting room with id: {}", id);
        
        if (!roomRepository.existsById(id)) {
            throw HotelServiceException.entityNotFound("Room", id);
        }
        
        roomRepository.deleteById(id);
    }
}