package com.euni.articlehub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 인기글/조회수 관련 Redis 처리를 담당하는 서비스.
 * - 조회수 카운트(빠르고 원자적: INCR)
 * - 인기글 랭킹(ZSET: ZINCRBY)
 * - (옵션) 같은 사용자가 짧은 시간에 중복 조회한 건 카운트 제외(SETNX + TTL)
 */
@Service
@RequiredArgsConstructor
public class PopularityService {

    /**
     * Spring Data Redis가 제공하는 템플릿.
     * - 문자열 기반으로 간단하게 Redis 명령을 실행할 수 있게 도와줌.
     * - opsForValue()  : String 값 (GET/SET/INCR 등)
     * - opsForZSet()   : Sorted Set (ZADD/ZINCRBY/ZRANGE 등)
     * - expire(key, t) : 키에 TTL(만료시간) 설정
     */
    private final StringRedisTemplate redis;

    /**
     * 특정 게시글(postId)에 대한 "조회"를 기록.
     * 1) (옵션) 같은 viewer가 짧은 시간(예: 10분) 안에 여러 번 본 건 1회로만 인정
     * 2) 조회수 카운터(post:views:{postId}) 증가
     * 3) DB 반영용 델타(post:views:delta:{postId})도 같이 증가
     * 4) 인기글 랭킹(ranking:posts:views_24h) ZSET에 점수 +1
     *
     * @param postId    조회된 게시글 ID
     * @param viewerKey "누가 봤는지"를 구분하는 키 (예: 로그인 유저면 "u:123", 비로그인이면 "ip:1.2.3.4")
     *                  null 또는 빈 문자열이면 중복 방지 없이 무조건 카운트
     */
    public void recordView(Long postId, String viewerKey) {
        // ========== [A] 중복 조회 방지(선택) ==========
        // 같은 사람이 새로고침/연타(F5) 해도 일정 시간(예: 10분) 동안은 '1회'만 카운트하고 싶을 때 사용
        boolean isUnique = true; // 기본값: 고유 조회로 본다
        if (viewerKey != null && !viewerKey.isBlank()) {
            // "viewed:{postId}:{viewerKey}" 형태의 키를 만든다.
            // 예: viewed:42:u:7  or  viewed:42:ip:203.0.113.10
            String dedupeKey = "viewed:%d:%s".formatted(postId, viewerKey);

            // setIfAbsent = SETNX (키가 없을 때만 SET)
            //   - true  : 처음 본 것 → 이번 조회는 고유(유니크)
            //   - false : 이미 최근에 본 적 있음 → 이번 조회는 카운트에서 제외
            // Duration.ofMinutes(10) : 10분 TTL(원하는 값으로 조절 가능)
            Boolean ok = redis.opsForValue().setIfAbsent(dedupeKey, "1", Duration.ofMinutes(10));
            isUnique = Boolean.TRUE.equals(ok);
        }

        // ========== [B] 실제 카운트/랭킹 증가 ==========
        if (isUnique) {
            // 1) 조회수 카운터: post:views:{postId}
            // String 값에 대해 INCR(원자적 증가)
            String viewsKey = "post:views:%d".formatted(postId);
            redis.opsForValue().increment(viewsKey);

            // 2) DB 반영용 델타: post:views:delta:{postId}
            //   - 일정 주기(예: 60초)로 이 값을 DB에 더해주고 0으로 리셋(GETSET)하면 됨
            String deltaKey = "post:views:delta:%d".formatted(postId);
            redis.opsForValue().increment(deltaKey);

            // 3) 인기글 랭킹: ranking:posts:views_24h (ZSET)
            //   - 멤버: postId(문자열)
            //   - 점수: 조회수 증가분 (여기서는 +1)
            String rankingKey = "ranking:posts:views_24h";
            redis.opsForZSet().incrementScore(rankingKey, postId.toString(), 1.0);

            // (선택) 랭킹 키 TTL 부여
            //   - 랭킹을 아주 오래 유지할 게 아니라면 기간을 정해 자동 정리되게 할 수 있음
            //   - 예: 2일 TTL (상황에 맞게 조절)
            redis.expire(rankingKey, Duration.ofDays(2));
        }
        // isUnique == false이면, "최근 본 적 있으니 이번 조회는 카운트 안 함"
    }

    /**
     * 인기글 Top N의 게시글 ID 목록을 가져온다.
     * - ZREVRANGE(점수 높은 순)과 동일: reverseRangeWithScores(0, limit-1)
     * - 반환은 postId(Long) 리스트 (점수는 여기선 쓰지 않지만 필요하면 함께 리턴 구조로 바꿔도 됨)
     */
    public java.util.List<Long> topPostIds(int limit) {
        String rankingKey = "ranking:posts:views_24h";

        var tuples = redis.opsForZSet().reverseRangeWithScores(rankingKey, 0, limit - 1);
        if (tuples == null || tuples.isEmpty()) return java.util.List.of();

        return tuples.stream()
                .map(t -> Long.valueOf((String) t.getValue())) // 멤버(문자열) → Long postId
                .toList();
    }
}
