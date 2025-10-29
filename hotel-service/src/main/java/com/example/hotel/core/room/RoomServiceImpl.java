package com.example.hotel.core.room;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoomServiceImpl {

    private final RoomRepository repo;

    RoomServiceImpl(final RoomRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<Room> findAll() {
        return repo.findAll();
    }
}
