package io.urdego.urdego_game_service.controller.client.user;

import io.urdego.urdego_game_service.controller.client.user.dto.UserInfoListReq;
import io.urdego.urdego_game_service.controller.client.user.dto.UserRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service", url = "${feign.client.config.service.url}")
public interface UserServiceClient {
    @GetMapping("/api/user-service/users/simple")
    UserRes getSimpleUser(@RequestParam Long userId);

    @PostMapping("/api/user-service/users")
    List<UserRes> getUsers(@RequestBody UserInfoListReq request);
}
