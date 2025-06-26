package com.hd.book.dto.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileDto {
    private final Long userId;
    private final String nickname;
    private final String email;
    private final String bio;
    private final String profileImage;
    @Builder.Default
    private final Boolean readHistoryVisible = true;
    private final String createdAt;
}
