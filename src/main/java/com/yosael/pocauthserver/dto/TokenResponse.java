package com.yosael.pocauthserver.dto;

public record TokenResponse(String accessToken, String tokenType, long expiresInSeconds) {}
