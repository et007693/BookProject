package com.hd.book.controller.user;

import com.hd.book.dto.response.ApiResponseDto;
import com.hd.book.dto.user.UserProfileDto;
import com.hd.book.jwt.JwtUtil;
import com.hd.book.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    // 내 프로필 조회
    @GetMapping("/me")
    public ResponseEntity<ApiResponseDto<UserProfileDto>> getMyProfile(
            @RequestHeader("Authorization") String bearerToken
    ) {
        try {
            String token = bearerToken
                    .replaceFirst("(?i)^Bearer\\s+", "")
                    .trim();
            String email = jwtUtil.getUserEmail(token);
            UserProfileDto profile = userService.getMyProfile(email);
            ApiResponseDto<UserProfileDto> response = new ApiResponseDto<>(true, "사용자 정보 조회 성공", profile);
            return ResponseEntity.ok(response);

        } catch (AuthenticationCredentialsNotFoundException e) {
            // Authorization 헤더 자체가 없거나 잘못된 경우
            ApiResponseDto<UserProfileDto> error =
                    new ApiResponseDto<>(false, "Authorization 헤더 형식 오류 발생.", null);

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(error);
        } catch (ExpiredJwtException e) {
            // JWT 만료
            ApiResponseDto<UserProfileDto> error =
                    new ApiResponseDto<>(false, "토큰이 만료되었습니다.", null);

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(error);
        } catch (BadCredentialsException e) {
            // JWT 서명 불일치 등 검증 실패
            ApiResponseDto<UserProfileDto> error =
                    new ApiResponseDto<>(false, "유효하지 않은 토큰입니다.", null);

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(error);
        } catch (JwtException | IllegalArgumentException ex) {
            // 기타 JWT 처리 오류 또는 파라미터 오류
            ApiResponseDto<UserProfileDto> error =
                    new ApiResponseDto<>(false, "토큰 처리 중 오류가 발생했습니다.", null);
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(error);

        } catch (AccessDeniedException ex) {
            // 권한 부족
            ApiResponseDto<UserProfileDto> error =
                    new ApiResponseDto<>(false, "접근 권한이 없습니다.", null);
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(error);

        } catch (Exception ex) {
            // 나머지 서버 오류
            log.error("내 프로필 조회 중 예기치 않은 예외 발생", ex);
            ApiResponseDto<UserProfileDto> error =
                    new ApiResponseDto<>(false, "서버 내부 오류가 발생했습니다.", null);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error);
        }
    }

    // 사용자 프로필 수정
    @PatchMapping("/me")
    public ResponseEntity<ApiResponseDto<UserProfileDto>> updateMyProfile(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody @Validated UserProfileDto userProfileDto
    ) {
        try {
            String token = bearerToken
                    .replaceFirst("(?i)^Bearer\\s+", "")
                    .trim();
            if (!jwtUtil.validateToken(token)) {
                throw new BadCredentialsException("유효하지 않은 토큰입니다.");
            }
            String email = jwtUtil.getUserEmail(token);
            UserProfileDto updated = userService.updateMyProfile(email, userProfileDto);
            ApiResponseDto<UserProfileDto> response =
                    new ApiResponseDto<>(true, "내 프로필 정보가 성공적으로 수정되었습니다.", updated);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 사용자 삭제
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponseDto<Void>> deleteMyAccount(
            @RequestHeader("Authorization") String bearerToken
    ) {
        String token = bearerToken
                .replaceFirst("(?i)^Bearer\\s+", "")
                .trim();

        if (!jwtUtil.validateToken(token)) {
            throw new BadCredentialsException("유효하지 않은 토큰입니다.");
        }

        String email = jwtUtil.getUserEmail(token);

        userService.deleteByEmail(email);

        ApiResponseDto<Void> response =
                new ApiResponseDto<>(true, "계정이 성공적으로 삭제되었습니다.", null);
        return ResponseEntity.ok(response);
    }

    // 다른 사용자 정보 조회
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponseDto<UserProfileDto>> getUserProfile(
            @PathVariable Long userId
    ) {
        try {
            UserProfileDto profile = userService.getOtherProfile(userId);
            ApiResponseDto<UserProfileDto> response =
                    new ApiResponseDto<>(true, "사용자 프로필 조회에 성공했습니다.", profile);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ApiResponseDto<UserProfileDto> error =
                    new ApiResponseDto<>(false, "사용자를 찾을 수 없습니다.", null);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(error);
        }
    }
}
