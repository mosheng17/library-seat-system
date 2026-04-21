package com.library.seatsystem.service;

import com.library.seatsystem.dto.StudyRoomResponse;
import com.library.seatsystem.repository.StudyRoomRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StudyRoomService {

    private final StudyRoomRepository studyRoomRepository;

    public StudyRoomService(StudyRoomRepository studyRoomRepository) {
        this.studyRoomRepository = studyRoomRepository;
    }

    public List<StudyRoomResponse> getAllRooms() {
        return studyRoomRepository.findAll().stream()
                .map(room -> new StudyRoomResponse(
                        room.getId(),
                        room.getRoomName(),
                        room.getFloor(),
                        room.getCapacity()
                ))
                .toList();
    }
}

