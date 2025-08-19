package com.euni.articlehub.controller;

import com.euni.articlehub.service.CacheStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cache")
public class CacheStatsController {

    private final CacheStatsService cacheStatsService;

    @GetMapping("/stats")
    public Map<String, String> getStats() {
        return cacheStatsService.getStats();
    }
}
