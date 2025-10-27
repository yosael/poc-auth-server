package com.yosael.pocauthserver.dto;

import java.util.List;
import java.util.UUID;

public record UserSummaryDto(
    UUID id,
    String username,
    boolean enabled,
    List<String> roles
) {}
