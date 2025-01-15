package io.urdego.urdego_game_service.common.client.dto;

public record ContentRes(
        String contentId,
        double latitude,
        double longitude,
        String hint
) {
}
