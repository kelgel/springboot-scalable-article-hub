package com.euni.articlehub.cache;

import com.euni.articlehub.dto.PostSearchRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.temporal.TemporalAccessor;

/*
검색 요청의 **모든 조건(키워드/페이지/정렬/필터/기간 등)**을 하나의 문자열 키로 깔끔하게 묶어,
캐시 충돌을 막고, 사람이 봐도 이해 가능한 Redis 키를 만드는 유틸리티
 */
public class Keys {
    private Keys(){}

    public static String search(PostSearchRequestDto r) {
        String raw = String.format(
                "kw=%s|p=%d|s=%d|sort=%s|dir=%s|author=%s|from=%s|to=%s",
                norm(r.getKeyword()),
                n(r.getPage()),
                n(r.getSize()),
                nz(r.getSortBy()),
                nz(r.getSortDirection()),
                nz(r.getAuthor()),
                nz(dateStr(r.getStartDate())),
                nz(dateStr(r.getEndDate()))
        );
        return "searchKey:" + shortHash(raw) + "|" + raw;
    }

    private static String norm(String s){
        if (s == null) return "";
        return s.trim().toLowerCase();
    }
    private static int n(Integer i) {
        return i == null ? 0 : i;
    }
    private static String nz(Object o) {
        return o == null ? "" : o.toString();
    }
    private static String dateStr(Object o) {
        if (o == null) return "";
        if (o instanceof TemporalAccessor) return o.toString();
        return o.toString();
    }
    private static String shortHash(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 8; i++) sb.append(String.format("%02x", d[i]));
            return sb.toString();
        }  catch (Exception e) {
            return Integer.toHexString(s.hashCode());
        }
    }
}
