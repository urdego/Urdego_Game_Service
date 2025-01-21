package io.urdego.urdego_game_service.common.client;

import io.urdego.urdego_game_service.common.client.dto.ContentRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "content-service")
public interface ContentServiceClient {
    @GetMapping("/api/content-service/content")
    ContentRes getContent(@RequestParam Long contentId);

    @GetMapping("/api/content-service/urdego-content")
    List<ContentRes> getUrdegoContents(@RequestParam int counts);
}
