package io.urdego.urdego_game_service.common.client.dto;

public record ContentRes(
        Long contentId,
        String contentName,
        String address,
        double latitude,
        double longitude,
        String hint
) {
}