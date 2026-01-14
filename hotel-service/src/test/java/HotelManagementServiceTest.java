import com.example.hotel.dto.HotelDto;
import com.example.hotel.dto.RoomDto;
import com.example.hotel.model.Hotel;
import com.example.hotel.model.Room;
import com.example.hotel.repo.HotelRepository;
import com.example.hotel.repo.RoomRepository;
import com.example.hotel.service.HotelManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HotelManagementServiceTest {

    private HotelRepository hotelRepository;
    private RoomRepository roomRepository;
    private HotelManagementService service;

    @BeforeEach
    void setUp() {
        hotelRepository = mock(HotelRepository.class);
        roomRepository = mock(RoomRepository.class);
        service = new HotelManagementService(hotelRepository, roomRepository);
    }

    @Test
    void getAllHotels_returnsHotels() {
        Hotel h1 = Hotel.builder().id(1L).name("A").build();
        Hotel h2 = Hotel.builder().id(2L).name("B").build();
        when(hotelRepository.findAll()).thenReturn(List.of(h1, h2));

        List<HotelDto> result = service.getAllHotels();

        assertEquals(2, result.size());
    }

    @Test
    void createHotel_savesHotel() {
        HotelDto dto = HotelDto.builder().name("New Hotel").build();
        when(hotelRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        HotelDto result = service.createHotel(dto);

        assertEquals("New Hotel", result.getName());
    }

    @Test
    void getAllRooms_returnsRooms() {
        Room r1 = Room.builder().id(1L).number("101").build();
        Room r2 = Room.builder().id(2L).number("102").build();
        when(roomRepository.findAll()).thenReturn(List.of(r1, r2));

        List<RoomDto> result = service.getAllRooms();

        assertEquals(2, result.size());
    }

    @Test
    void createRoom_setsHotelReference() {
        Hotel hotel = Hotel.builder().id(1L).name("H").build();
        RoomDto dto = RoomDto.builder().number("101").hotelId(1L).build();
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(roomRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        RoomDto result = service.createRoom(dto);

        assertEquals("101", result.getNumber());
        assertEquals(1L, result.getHotelId());
    }
}
