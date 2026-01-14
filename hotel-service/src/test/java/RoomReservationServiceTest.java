import com.example.hotel.dto.RoomReservationDto;
import com.example.hotel.exception.HotelServiceException;
import com.example.hotel.model.Room;
import com.example.hotel.model.RoomReservationLock;
import com.example.hotel.repo.RoomRepository;
import com.example.hotel.repo.RoomReservationLockRepository;
import com.example.hotel.service.RoomReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoomReservationServiceTest {

    private RoomRepository roomRepository;
    private RoomReservationLockRepository lockRepository;
    private RoomReservationService service;

    @BeforeEach
    void setUp() {
        roomRepository = mock(RoomRepository.class);
        lockRepository = mock(RoomReservationLockRepository.class);
        service = new RoomReservationService(roomRepository, lockRepository);
    }

    @Test
    void holdRoom_createsNewHold_ifNoExisting() {
        String requestId = "req1";
        Long roomId = 1L;
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(2);

        when(lockRepository.findByRequestId(requestId)).thenReturn(Optional.empty());
        when(lockRepository.findOverlappingReservations(eq(roomId), anyList(), eq(end), eq(start)))
                .thenReturn(List.of());
        when(lockRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        RoomReservationDto result = service.holdRoom(requestId, roomId, start, end);

        assertEquals(RoomReservationLock.Status.HELD, RoomReservationLock.Status.valueOf(result.getStatus()));
        verify(lockRepository).save(any());
    }

    @Test
    void holdRoom_throwsException_ifConflictExists() {
        String requestId = "req1";
        Long roomId = 1L;
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(2);

        when(lockRepository.findByRequestId(requestId)).thenReturn(Optional.empty());
        when(lockRepository.findOverlappingReservations(eq(roomId), anyList(), eq(end), eq(start)))
                .thenReturn(List.of(new RoomReservationLock()));

        assertThrows(HotelServiceException.class,
                () -> service.holdRoom(requestId, roomId, start, end));
    }

    @Test
    void confirmHold_confirmsHeldReservation() {
        String requestId = "req1";
        RoomReservationLock lock = RoomReservationLock.builder()
                .requestId(requestId)
                .roomId(1L)
                .status(RoomReservationLock.Status.HELD)
                .build();
        Room room = Room.builder().id(1L).timesBooked(0).build();

        when(lockRepository.findByRequestId(requestId)).thenReturn(Optional.of(lock));
        when(lockRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        RoomReservationDto dto = service.confirmHold(requestId);

        assertEquals(RoomReservationLock.Status.CONFIRMED, RoomReservationLock.Status.valueOf(dto.getStatus()));
        assertEquals(1, room.getTimesBooked());
    }

    @Test
    void releaseHold_releasesHeldReservation() {
        String requestId = "req1";
        RoomReservationLock lock = RoomReservationLock.builder()
                .requestId(requestId)
                .status(RoomReservationLock.Status.HELD)
                .build();

        when(lockRepository.findByRequestId(requestId)).thenReturn(Optional.of(lock));
        when(lockRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        RoomReservationDto dto = service.releaseHold(requestId);

        assertEquals(RoomReservationLock.Status.RELEASED, RoomReservationLock.Status.valueOf(dto.getStatus()));
    }
}
