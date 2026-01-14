package com.example.hotel.dto;

import com.example.hotel.model.RoomReservationLock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomReservationDto {
    private String requestId;
    private Long roomId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;

    public static RoomReservationDto fromEntity(RoomReservationLock lock) {
        return RoomReservationDto.builder()
                .requestId(lock.getRequestId())
                .roomId(lock.getRoomId())
                .startDate(lock.getStartDate())
                .endDate(lock.getEndDate())
                .status(lock.getStatus().name())
                .build();
    }

    public RoomReservationLock toEntity() {
        return RoomReservationLock.builder()
                .requestId(requestId)
                .roomId(roomId)
                .startDate(startDate)
                .endDate(endDate)
                .status(status != null ? RoomReservationLock.Status.valueOf(status) : null)
                .build();
    }
}