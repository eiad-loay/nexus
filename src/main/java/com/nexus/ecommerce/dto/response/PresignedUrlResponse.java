package com.nexus.ecommerce.dto.response;

public record PresignedUrlResponse(String uploadUrl, String key, int expiresInMinutes) {
}