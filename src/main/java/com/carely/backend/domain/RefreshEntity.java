package com.carely.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@AllArgsConstructor
@RedisHash(value = "refresh", timeToLive = 86400)
public class RefreshEntity {
    @Id
    private String refresh;
    private String username;
}
