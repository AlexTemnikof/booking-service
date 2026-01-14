
import com.example.hotel.dto.RoomDto;
import com.example.hotel.model.Room;
import com.example.hotel.repo.RoomRepository;
import com.example.hotel.service.StatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class StatsServiceTest {

    private RoomRepository roomRepository;
    private StatsService statsService;

    @BeforeEach
    void setUp() {
        roomRepository = mock(RoomRepository.class);
        statsService = new StatsService(roomRepository);
    }

    @Test
    void getPopularRooms_returnsSortedRooms() {
        Room r1 = Room.builder().id(1L).timesBooked(5).build();
        Room r2 = Room.builder().id(2L).timesBooked(10).build();
        Room r3 = Room.builder().id(3L).timesBooked(3).build();

        when(roomRepository.findAll()).thenReturn(List.of(r1, r2, r3));

        List<RoomDto> result = statsService.getPopularRooms();

        assertEquals(3, result.size());
        assertEquals(10, result.get(0).getTimesBooked());
        assertEquals(5, result.get(1).getTimesBooked());
        assertEquals(3, result.get(2).getTimesBooked());
    }
}
