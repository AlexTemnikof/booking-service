package com.example.hotel.repo;

import com.example.hotel.model.RoomReservationLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomReservationLockRepository extends JpaRepository<RoomReservationLock, Long> {
    Optional<RoomReservationLock> findByRequestId(String requestId);

    @Query("SELECT l FROM RoomReservationLock l WHERE l.roomId = :roomId " +
           "AND l.status IN :statuses " +
           "AND l.startDate <= :endDate " +
           "AND l.endDate >= :startDate")
    List<RoomReservationLock> findOverlappingReservations(
            @Param("roomId") Long roomId,
            @Param("statuses") List<RoomReservationLock.Status> statuses,
            @Param("endDate") LocalDate endDate,
            @Param("startDate") LocalDate startDate
    );
}
