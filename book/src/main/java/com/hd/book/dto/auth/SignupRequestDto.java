package com.hd.book.dto.auth;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequestDto {
    private String email;
    private String password;
    private String nickname;
    // 선택 필드라면 @Nullable 대신, 컨트롤러에서 체크하거나, 아래처럼 그냥 남겨둬도 됩니다.
    private String profileImage;
}