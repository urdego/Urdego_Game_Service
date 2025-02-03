package io.urdego.urdego_game_service.controller.client.content;

import io.urdego.urdego_game_service.controller.client.content.dto.ContentRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "content-service", url = "${feign.client.config.service.url}")
public interface ContentServiceClient {
    @GetMapping("/api/content-service/content")
    ContentRes getContent(@RequestParam Long contentId);

    @GetMapping("/api/content-service/urdego-content")
    List<ContentRes> getUrdegoContents(@RequestParam int counts);
}
