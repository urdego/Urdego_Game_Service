package io.urdego.urdego_game_service.controller.client.content.dto;

public record ContentRes(
        Long contentId,
        String contentName,
        String address,
        double latitude,
        double longitude,
        String hint
) {
}