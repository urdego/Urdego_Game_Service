package io.urdego.urdego_game_service.domain.room.repository;

import io.urdego.urdego_game_service.domain.room.entity.Room;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends CrudRepository<Room, String> {
}
