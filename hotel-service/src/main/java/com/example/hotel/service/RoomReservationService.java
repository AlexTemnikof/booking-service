package com.example.hotel.service;

import com.example.hotel.dto.RoomReservationDto;
import com.example.hotel.dto.RoomReservationRequestDto;
import com.example.hotel.exception.HotelServiceException;
import com.example.hotel.model.RoomReservationLock;
import com.example.hotel.repo.RoomRepository;
import com.example.hotel.repo.RoomReservationLockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomReservationService {
    private final RoomRepository roomRepository;
    private final RoomReservationLockRepository lockRepository;

    @Transactional
    public RoomReservationDto holdRoom(String requestId, Long roomId, LocalDate startDate, LocalDate endDate) {
        log.debug("Holding room {} from {} to {} with request ID {}", roomId, startDate, endDate, requestId);
        
        // Check for existing reservation with the same request ID (idempotency)
        return lockRepository.findByRequestId(requestId)
                .map(RoomReservationDto::fromEntity)
                .orElseGet(() -> createNewHold(requestId, roomId, startDate, endDate));
    }

    private RoomReservationDto createNewHold(String requestId, Long roomId, LocalDate startDate, LocalDate endDate) {
        // Check for conflicting reservations
        List<RoomReservationLock> conflicts = lockRepository.findOverlappingReservations(
                roomId,
                Arrays.asList(RoomReservationLock.Status.HELD, RoomReservationLock.Status.CONFIRMED),
                endDate,
                startDate
        );
        
        if (!conflicts.isEmpty()) {
            log.warn("Room {} is not available from {} to {}", roomId, startDate, endDate);
            throw HotelServiceException.roomNotAvailable();
        }
        
        // Create new hold
        RoomReservationLock lock = RoomReservationLock.builder()
                .requestId(requestId)
                .roomId(roomId)
                .startDate(startDate)
                .endDate(endDate)
                .status(RoomReservationLock.Status.HELD)
                .build();
        
        return RoomReservationDto.fromEntity(lockRepository.save(lock));
    }

    @Transactional
    public RoomReservationDto confirmHold(String requestId) {
        log.debug("Confirming hold with request ID {}", requestId);
        
        RoomReservationLock lock = lockRepository.findByRequestId(requestId)
                .orElseThrow(HotelServiceException::holdNotFound);
        
        // Idempotency check
        if (lock.getStatus() == RoomReservationLock.Status.CONFIRMED) {
            return RoomReservationDto.fromEntity(lock);
        }
        
        if (lock.getStatus() == RoomReservationLock.Status.RELEASED) {
            throw HotelServiceException.holdAlreadyReleased();
        }
        
        lock.setStatus(RoomReservationLock.Status.CONFIRMED);
        
        // Update room booking statistics
        roomRepository.findById(lock.getRoomId()).ifPresent(room -> {
            room.setTimesBooked(room.getTimesBooked() + 1);
            roomRepository.save(room);
        });
        
        return RoomReservationDto.fromEntity(lockRepository.save(lock));
    }

    @Transactional
    public RoomReservationDto releaseHold(String requestId) {
        log.debug("Releasing hold with request ID {}", requestId);
        
        RoomReservationLock lock = lockRepository.findByRequestId(requestId)
                .orElseThrow(HotelServiceException::holdNotFound);
        
        // Idempotency checks
        if (lock.getStatus() == RoomReservationLock.Status.RELEASED) {
            return RoomReservationDto.fromEntity(lock);
        }
        
        if (lock.getStatus() == RoomReservationLock.Status.CONFIRMED) {
            return RoomReservationDto.fromEntity(lock); // Already confirmed, do nothing
        }
        
        lock.setStatus(RoomReservationLock.Status.RELEASED);
        return RoomReservationDto.fromEntity(lockRepository.save(lock));
    }
}