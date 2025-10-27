package com.yosael.pocauthserver.dto;

import java.util.UUID;

public record RoleDto(
    UUID id,
    String name
) {}
