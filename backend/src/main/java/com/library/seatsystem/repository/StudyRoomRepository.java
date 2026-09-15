package com.library.seatsystem.repository;

import com.library.seatsystem.entity.StudyRoom;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * B 基础数据管理 —— 自习室仓储。
 */
public interface StudyRoomRepository extends JpaRepository<StudyRoom, Long> {
    boolean existsByRoomName(String roomName);
}
