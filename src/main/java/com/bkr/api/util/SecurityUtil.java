package com.bkr.api.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

public class SecurityUtil {

    public static String generateUniqueHash() {
        try {
            // 1. UUID 생성
            String uuid = UUID.randomUUID().toString();

            // 2. SHA-256 해시 생성
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(uuid.getBytes(StandardCharsets.UTF_8));

            // 3. Base64 URL-safe 인코딩 (패딩 제거)
            String base64Hash = Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes);

            return base64Hash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 알고리즘을 사용할 수 없습니다.", e);
        }
    }

}
