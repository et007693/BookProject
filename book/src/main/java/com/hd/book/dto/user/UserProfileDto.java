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
    private final Boolean readHistoryVisible;
    private final String createdAt;

    @Builder
    public UserProfileDto(Long userId,
                          String nickname,
                          String email,
                          String bio,
                          String profileImage,
                          Boolean readHistoryVisible,
                          String createdAt) {
        this.userId = userId;
        this.nickname = nickname;
        this.email = email;
        this.bio = bio;
        this.profileImage = profileImage;
        this.readHistoryVisible = readHistoryVisible;          // Boolean 이므로 null 허용
        this.createdAt = createdAt;
    }
}
