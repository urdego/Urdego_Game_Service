package io.urdego.urdego_game_service.controller.client.content.dto;

import java.time.LocalDateTime;

public record ContentRes(
        Long contentId,
        String url,
        String contentName,
        String address,
        double latitude,
        double longitude,
        String hint,
        LocalDateTime createdDateTime
) {
}