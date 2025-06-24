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
    private final Boolean readPublic;
    private final String createdAt;

    @Builder
    public UserProfileDto(Long userId,
                          String nickname,
                          String email,
                          String bio,
                          String profileImage,
                          String phone,
                          Boolean readPublic,
                          String createdAt,
                          String updatedAt) {
        this.userId = userId;
        this.nickname = nickname;
        this.email = email;
        this.bio = bio;
        this.profileImage = profileImage;
        this.phone = phone;
        this.readPublic = readPublic;          // Boolean 이므로 null 허용
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
