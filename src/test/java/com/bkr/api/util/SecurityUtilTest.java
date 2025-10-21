package com.bkr.api.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SecurityUtil.generateUniqueHash() 테스트")
class SecurityUtilTest {

    @Test
    @DisplayName("해시 생성 - null이 아니어야 함")
    void generateUniqueHash_ShouldReturnNonNull() {
        // given & when
        String hash = SecurityUtil.generateUniqueHash();

        // then
        assertNotNull(hash);
    }

    @Test
    @DisplayName("해시 생성 - 빈 문자열이 아니어야 함")
    void generateUniqueHash_ShouldReturnNonEmpty() {
        // given & when
        String hash = SecurityUtil.generateUniqueHash();

        // then
        assertNotNull(hash);
        assertFalse(hash.isEmpty());
    }

    @Test
    @DisplayName("해시 생성 - 예상 길이 확인 (Base64 URL-safe는 43자)")
    void generateUniqueHash_ShouldHaveExpectedLength() {
        // given & when
        String hash = SecurityUtil.generateUniqueHash();

        // then
        // SHA-256 (32바이트) -> Base64 인코딩 (패딩 제거) = 43자
        assertEquals(43, hash.length(), "Base64 URL-safe 인코딩된 SHA-256 해시는 43자여야 합니다");
    }

    @Test
    @DisplayName("해시 생성 - Base64 URL-safe 문자만 포함")
    void generateUniqueHash_ShouldContainOnlyBase64UrlSafeCharacters() {
        // given & when
        String hash = SecurityUtil.generateUniqueHash();

        // then
        // Base64 URL-safe는 A-Z, a-z, 0-9, -, _ 문자만 포함 (패딩 제거)
        assertTrue(hash.matches("[A-Za-z0-9_-]+"), "해시는 Base64 URL-safe 문자만 포함해야 합니다");
    }

    @RepeatedTest(100)
    @DisplayName("해시 생성 - 100번 반복 시 모두 고유해야 함")
    void generateUniqueHash_ShouldBeUnique() {
        // given
        Set<String> hashes = new HashSet<>();
        int iterations = 100;

        // when
        for (int i = 0; i < iterations; i++) {
            String hash = SecurityUtil.generateUniqueHash();
            hashes.add(hash);
        }

        // then
        assertEquals(iterations, hashes.size(), "모든 해시가 고유해야 합니다");
    }

    @Test
    @DisplayName("해시 생성 - 연속 생성 시 서로 다른 값")
    void generateUniqueHash_ConsecutiveCalls_ShouldReturnDifferentValues() {
        // given & when
        String hash1 = SecurityUtil.generateUniqueHash();
        String hash2 = SecurityUtil.generateUniqueHash();
        String hash3 = SecurityUtil.generateUniqueHash();

        // then
        assertNotEquals(hash1, hash2);
        assertNotEquals(hash2, hash3);
        assertNotEquals(hash1, hash3);
    }

    @Test
    @DisplayName("해시 생성 - 멀티스레드 환경에서 고유성 보장")
    void generateUniqueHash_MultiThreaded_ShouldBeUnique() throws InterruptedException {
        // given
        int threadCount = 10;
        int hashesPerThread = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        Set<String> hashes = ConcurrentHashMap.newKeySet();

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < hashesPerThread; j++) {
                        String hash = SecurityUtil.generateUniqueHash();
                        hashes.add(hash);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        assertEquals(threadCount * hashesPerThread, hashes.size(),
                "멀티스레드 환경에서도 모든 해시가 고유해야 합니다");
    }

    @Test
    @DisplayName("해시 생성 - 대량 생성 시 성능 확인")
    void generateUniqueHash_Performance_ShouldBeReasonablyFast() {
        // given
        int iterations = 10000;
        long startTime = System.currentTimeMillis();

        // when
        for (int i = 0; i < iterations; i++) {
            SecurityUtil.generateUniqueHash();
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // then
        assertTrue(duration < 5000,
                String.format("10000개 해시 생성이 5초 이내에 완료되어야 합니다 (실제: %dms)", duration));
    }

    @Test
    @DisplayName("해시 생성 - Base64 URL-safe 형식 확인")
    void generateUniqueHash_ShouldBeBase64UrlSafe() {
        // given & when
        String hash = SecurityUtil.generateUniqueHash();

        // then
        // Base64 URL-safe는 43자의 [A-Za-z0-9_-] 문자
        assertTrue(hash.matches("[A-Za-z0-9_-]{43}"), "해시는 43자의 Base64 URL-safe 문자여야 합니다");
        assertFalse(hash.contains("+"), "Base64 URL-safe는 + 문자를 포함하지 않아야 합니다");
        assertFalse(hash.contains("/"), "Base64 URL-safe는 / 문자를 포함하지 않아야 합니다");
        assertFalse(hash.contains("="), "패딩이 제거되어야 합니다");
    }
}