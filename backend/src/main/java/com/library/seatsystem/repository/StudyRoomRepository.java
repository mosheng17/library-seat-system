package com.library.seatsystem.repository;

import com.library.seatsystem.entity.StudyRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRoomRepository extends JpaRepository<StudyRoom, Long> {
    boolean existsByRoomName(String roomName);
}
