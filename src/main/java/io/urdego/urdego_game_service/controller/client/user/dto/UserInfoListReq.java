package io.urdego.urdego_game_service.controller.client.user.dto;

import java.util.List;

public record UserInfoListReq(
        List<Long> userIds
) {
}
