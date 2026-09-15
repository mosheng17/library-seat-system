package com.library.seatsystem.service;

import com.library.seatsystem.common.ResponseMapper;
import com.library.seatsystem.dto.StudyRoomResponse;
import com.library.seatsystem.entity.StudyRoom;
import com.library.seatsystem.repository.StudyRoomRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StudyRoomService extends BaseService<StudyRoom, Long> {

    private final StudyRoomRepository studyRoomRepository;

    public StudyRoomService(StudyRoomRepository studyRoomRepository) {
        super(studyRoomRepository);
        this.studyRoomRepository = studyRoomRepository;
    }

    /** 查询全部自习室。 */
    public List<StudyRoomResponse> getAllRooms() {
        return studyRoomRepository.findAll().stream()
                .map(ResponseMapper::toStudyRoom)
                .toList();
    }
}

