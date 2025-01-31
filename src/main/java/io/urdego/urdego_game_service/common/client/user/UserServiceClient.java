package io.urdego.urdego_game_service.common.client.user;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "user-service", url = "${feign.client.config.service.url}")
public interface UserServiceClient {
}
