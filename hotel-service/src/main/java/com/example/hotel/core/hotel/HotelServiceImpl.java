package com.example.hotel.core.hotel;

import com.example.hotel.core.reservation.ReservationStatus;
import com.example.hotel.core.room.Room;
import com.example.hotel.core.reservation.RoomReservationLock;
import com.example.hotel.core.room.RoomRepository;
import com.example.hotel.core.reservation.RoomReservationLockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class HotelServiceImpl {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final RoomReservationLockRepository lockRepository;

    public HotelServiceImpl(HotelRepository hotelRepository,
                            RoomRepository roomRepository,
                            RoomReservationLockRepository lockRepository) {
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.lockRepository = lockRepository;
    }

    public List<Hotel> listHotels() {
        return hotelRepository.findAll();
    }

    public Optional<Hotel> getHotel(Long id) {
        return hotelRepository.findById(id);
    }

    @Transactional
    public Hotel saveHotel(Hotel hotel) {
        return hotelRepository.save(hotel);
    }

    @Transactional
    public void deleteHotel(Long id) {
        hotelRepository.deleteById(id);
    }

    public List<Room> listRooms() {
        return roomRepository.findAll();
    }

    public Optional<Room> getRoom(Long id) {
        return roomRepository.findById(id);
    }

    @Transactional
    public Room saveRoom(Room room) {
        return roomRepository.save(room);
    }

    @Transactional
    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }

    @Transactional
    public RoomReservationLock holdRoom(String requestId, Long roomId, LocalDate startDate, LocalDate endDate) {
        validateDates(startDate, endDate);

        return lockRepository.findByRequestId(requestId)
                .orElseGet(() -> createNewHold(requestId, roomId, startDate, endDate));
    }

    @Transactional
    public RoomReservationLock confirmHold(String requestId) {
        RoomReservationLock lock = getActiveLock(requestId);

        if (lock.getStatus() == ReservationStatus.CONFIRMED) {
            return lock; // idempotency
        }

        if (lock.getStatus() == ReservationStatus.RELEASED) {
            throw new IllegalStateException("Hold already released");
        }

        lock.withStatus(ReservationStatus.CONFIRMED);
        incrementRoomBookingCounter(lock.getRoomId());

        return lockRepository.save(lock);
    }

    @Transactional
    public RoomReservationLock releaseHold(String requestId) {
        RoomReservationLock lock = getActiveLock(requestId);

        if (lock.getStatus() == ReservationStatus.RELEASED ||
                lock.getStatus() == ReservationStatus.CONFIRMED) {
            return lock; // idempotency
        }

        lock.withStatus(ReservationStatus.RELEASED);
        return lockRepository.save(lock);
    }

    private RoomReservationLock createNewHold(String requestId, Long roomId, LocalDate startDate, LocalDate endDate) {
        checkForConflictingReservations(roomId, startDate, endDate);

        RoomReservationLock lock = new RoomReservationLock()
                .withRequestId(requestId)
                .withRoomId(roomId)
                .withStartDate(startDate)
                .withEndDate(endDate)
                .withStatus(ReservationStatus.HELD);

        return lockRepository.save(lock);
    }

    private void checkForConflictingReservations(Long roomId, LocalDate startDate, LocalDate endDate) {
        List<RoomReservationLock> conflicts = lockRepository
                .findConflictingReservations(roomId,
                        List.of(ReservationStatus.HELD, ReservationStatus.CONFIRMED),
                        endDate, startDate);

        if (!conflicts.isEmpty()) {
            throw new IllegalStateException("Room not available for the specified dates");
        }
    }

    private RoomReservationLock getActiveLock(String requestId) {
        return lockRepository.findByRequestId(requestId)
                .orElseThrow(() -> new IllegalStateException("Hold not found for requestId: " + requestId));
    }

    private void incrementRoomBookingCounter(Long roomId) {
        roomRepository.findById(roomId).ifPresent(room -> {
            room.withTimesBooked(room.getTimesBooked() + 1);
            roomRepository.save(room);
        });
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start date cannot be in the past");
        }
    }
}