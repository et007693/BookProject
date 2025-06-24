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
    private final String phone;
    private final String createdAt;
    private final String updatedAt;
}
