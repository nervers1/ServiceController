package com.bkr.api.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class SecurityUtilTestCo {

    @Test
    @DisplayName("해시값이 null이 아니고 비어있지 않아야 함")
    void generateUniqueHash_ShouldReturnNonEmptyString() {
        // when
        String hash = SecurityUtil.generateUniqueHash();

        // then
        assertNotNull(hash);
        assertFalse(hash.isEmpty());
    }

    @Test
    @DisplayName("해시값이 Base64 URL-safe 형식이어야 함")
    void generateUniqueHash_ShouldReturnBase64UrlSafeFormat() {
        // when
        String hash = SecurityUtil.generateUniqueHash();

        // then
        // Base64 URL-safe는 A-Z, a-z, 0-9, -, _ 만 포함
        Pattern base64UrlSafePattern = Pattern.compile("^[A-Za-z0-9_-]+$");
        assertTrue(base64UrlSafePattern.matcher(hash).matches());
    }

    @Test
    @DisplayName("SHA-256 해시의 Base64 인코딩 길이는 43자여야 함")
    void generateUniqueHash_ShouldReturn43Characters() {
        // when
        String hash = SecurityUtil.generateUniqueHash();

        // then
        // SHA-256은 32바이트(256비트)이고, Base64 인코딩 시 패딩 없이 43자
        assertEquals(43, hash.length());
    }

    @Test
    @DisplayName("매번 다른 해시값을 생성해야 함")
    void generateUniqueHash_ShouldGenerateDifferentValues() {
        // when
        String hash1 = SecurityUtil.generateUniqueHash();
        String hash2 = SecurityUtil.generateUniqueHash();

        // then
        assertNotEquals(hash1, hash2);
    }

    @RepeatedTest(100)
    @DisplayName("100번 반복 실행 시 모두 고유한 값이어야 함")
    void generateUniqueHash_ShouldGenerateUniqueValues() {
        // given
        Set<String> hashes = new HashSet<>();

        // when
        for (int i = 0; i < 100; i++) {
            String hash = SecurityUtil.generateUniqueHash();
            hashes.add(hash);
        }

        // then
        assertEquals(100, hashes.size());
    }

    @Test
    @DisplayName("패딩 문자(=)가 포함되지 않아야 함")
    void generateUniqueHash_ShouldNotContainPadding() {
        // when
        String hash = SecurityUtil.generateUniqueHash();

        // then
        assertFalse(hash.contains("="));
    }

    @Test
    @DisplayName("여러 번 호출해도 예외가 발생하지 않아야 함")
    void generateUniqueHash_ShouldNotThrowException() {
        // when & then
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 1000; i++) {
                SecurityUtil.generateUniqueHash();
            }
        });
    }
}
