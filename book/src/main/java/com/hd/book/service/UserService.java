package com.hd.book.service;

import com.hd.book.constant.HistoryStatus;
import com.hd.book.dto.auth.SignupRequestDto;
import com.hd.book.dto.board.BoardResDto;
import com.hd.book.dto.book.BookHistoryReqDto;
import com.hd.book.dto.book.BookHistoryResDto;
import com.hd.book.dto.book.BookHistoryUpdateDto;
import com.hd.book.dto.user.UserProfileDto;
import com.hd.book.entity.BoardEntity;
import com.hd.book.entity.BookEntity;
import com.hd.book.entity.HistoryEntity;
import com.hd.book.entity.UserEntity;
import com.hd.book.repository.BoardRepository;
import com.hd.book.repository.BookRepository;
import com.hd.book.repository.HistoryRepository;
import com.hd.book.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.awt.print.Book;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BookRepository bookRepository;
    private final HistoryRepository historyRepository;
    private final BoardRepository boardRepository;

    // 회원가입
    @Transactional
    public UserEntity register(SignupRequestDto dto) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        // 닉네임 중복 체크
        if (userRepository.existsByNickname(dto.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임 입니다.");
        }

        // 엔티티 생성
        UserEntity user = new UserEntity();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname());
        user.setProfileImage(dto.getProfileImage());

        // 저장 및 반환
        UserEntity saved = userRepository.save(user);

        log.info("회원가입 성공: userId={}, email={}",
                saved.getUserId(), saved.getEmail());
        return saved;
    }

    // 내 정보 조회
    @Transactional(readOnly = true)
    public UserProfileDto getMyProfile(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저 고유 아이디를 찾을 수 없습니다."));
        return UserProfileDto.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .bio(user.getBio())
                .profileImage(user.getProfileImage())
                .readHistoryVisible(user.isReadHistoryVisible())
                .createdAt(user.getCreatedAt().toString())
                .build();
    }

    // 사용자 정보 수정
    @Transactional
    public UserProfileDto updateMyProfile(String email, UserProfileDto userProfileDto) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다. email=" + email));

        if (userProfileDto.getNickname() != null) user.setNickname(userProfileDto.getNickname());
        if (userProfileDto.getBio() != null) user.setBio(userProfileDto.getBio());
        if (userProfileDto.getProfileImage() != null) user.setProfileImage(userProfileDto.getProfileImage());
        if (userProfileDto.getReadHistoryVisible() != null) user.setReadHistoryVisible(userProfileDto.getReadHistoryVisible());

        return UserProfileDto.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .bio(user.getBio())
                .profileImage(user.getProfileImage())
                .readHistoryVisible(user.isReadHistoryVisible())
                .createdAt(user.getCreatedAt().toString())
                .build();
    }

    // 회원(사용자) 삭제
    @Transactional
    public void deleteByEmail(String email) {
        // 사용자 조회
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("삭제할 유저를 찾을 수 없습니다.")
                );

        // 삭제
        userRepository.delete(user);
        log.info("사용자 삭제 완료: userId={}, email={}", user.getUserId(), user.getEmail());
    }

    // 다른 사용자의 정보 조회
    @Transactional(readOnly = true)
    public UserProfileDto getOtherProfile(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("사용자를 찾을 수 없습니다. userId=" + userId));
        return UserProfileDto.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .bio(user.getBio())
                .build();
    }

    // 읽은 책 등록
    @Transactional
    public BookHistoryResDto registerReadHistory(String email, BookHistoryReqDto reqDto) {
        // 사용자 조회
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // isbn 형식 검증
        String isbn = reqDto.getIsbn();
        if (isbn == null || !(isbn.length() == 10 || isbn.length() == 13)) {
            throw new IllegalArgumentException("잘못된 ISBN 형식입니다. ISBN은 13자리 또는 10자리여야 합니다.");
        }

        // 책 조회
        BookEntity book = bookRepository.findByIsbn(reqDto.getIsbn())
                .orElseGet(() -> {
                    BookEntity newBook = new BookEntity();
                    newBook.setIsbn(reqDto.getIsbn());
                    log.info("새 책을 등록합니다. isbn={}", newBook.getIsbn());
                    return bookRepository.save(newBook);
                });

        // 중복 검사
        if (historyRepository.existsByUserAndBook(user, book)) {
            throw new IllegalArgumentException("이미 등록되어 있는 책입니다.");
        }

        // 날짜 파싱
        LocalDate start = LocalDate.parse(reqDto.getStartRead());
        LocalDate end   = LocalDate.parse(reqDto.getEndRead());

        // 상태 설정
        HistoryStatus status;
        if (reqDto.getStatus() != null) {
            status = HistoryStatus.valueOf(reqDto.getStatus().toString().toUpperCase());
        } else {
            status = end.isBefore(start)
                    ? HistoryStatus.READING
                    : HistoryStatus.COMPLETED;
        }
        // 메모 값 가져오기
        String memo = reqDto.getMemo();

        // 엔티티 생성 및 저장
        HistoryEntity history = HistoryEntity.builder()
                .user(user)
                .book(book)
                .startDate(start)
                .endDate(end)
                .status(status)
                .memo(memo)
                .build();

        HistoryEntity saved = historyRepository.save(history);
        log.info("읽은 책이 등록되었습니다. historyId={}", saved.getHistoryid());

        // ReadHistoryResDto에 맞춰 7개 인자를 넘깁니다.
        return new BookHistoryResDto(
                saved.getHistoryid(),
                Long.parseLong(book.getIsbn()),
                saved.getUser().getUserId(),
                saved.getStartDate().toString(),
                saved.getEndDate().toString(),
                status,
                saved.getMemo(),
                saved.getCreatedAt().toString(),
                saved.getUpdatedAt().toString()
        );
    }

    // 사용자가 등록한 책의 상태를 수정
    @Transactional
    public BookHistoryResDto updateReadHistory(
            String email,
            Long historyId,
            BookHistoryUpdateDto updateDto
    ) {
        // 사용자 조회
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 기존 히스토리 조회
        HistoryEntity history = historyRepository.findById(historyId)
                .orElseThrow(()-> new EntityNotFoundException("읽은책을 등록한 기록을 찾을 수 없습니다."));

        // 토큰 검증
        if (!history.getUser().getUserId().equals(user.getUserId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "본인의 기록만 수정할 수 있습니다."
            );
        }

        // 요청된 필드별로 선택적 업데이트
        if (updateDto.getStartRead() != null) {
            try {
                LocalDate start = LocalDate.parse(updateDto.getStartRead());
                history.setStartDate(start);
            } catch (Exception e) {
                throw new IllegalArgumentException("날짜 입력 형식 오류. YYYY-MM-DD 이어야 합니다.");
            }
        }

        if (updateDto.getEndRead() != null) {
            try {
                LocalDate end = LocalDate.parse(updateDto.getEndRead());
                history.setEndDate(end);
            } catch (Exception e) {
                throw new IllegalArgumentException("날짜 입력 형식 오류. YYYY-MM-DD 이어야 합니다.");
            }
        }

        if (updateDto.getMemo() != null) {
            history.setMemo(updateDto.getMemo());
        }

        if (updateDto.getStatus() != null) {
            HistoryStatus newStatus;
            try {
                newStatus = HistoryStatus.valueOf(updateDto.getStatus().toString().toUpperCase());
            } catch (Exception e) {
                throw new IllegalArgumentException("허용되지 않는 status 값입니다. READING 또는 COMPLETED 이어야 합니다.");
            }
            history.setStatus(newStatus);
        }

        // 저장
        HistoryEntity updated = historyRepository.save(history);
        log.info("등록된 책의 정보가 수정되었습니다.");

        return new BookHistoryResDto(
                updated.getHistoryid(),
                Long.parseLong(updated.getBook().getIsbn()),
                updated.getUser().getUserId(),
                updated.getStartDate().toString(),
                updated.getEndDate().toString(),
                updated.getStatus(),
                updated.getMemo(),
                updated.getCreatedAt().toString(),
                updated.getUpdatedAt().toString()
        );
    }

    // 읽은 책 목록 조회
    @Transactional
    public List<BookHistoryResDto> getMyReadBooks(Long userId) {
        // 사용자 확인
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        // 히스토리 조회
        List<HistoryEntity> histories = historyRepository.findAllByUserOrderByStartDateDesc(user);

        // DTO 변환
        return
        histories.stream()
                .map(h -> new BookHistoryResDto(
                        h.getHistoryid(),
                        Long.parseLong(h.getBook().getIsbn()),
                        user.getUserId(),
                        h.getStartDate().toString(),
                        h.getEndDate().toString(),
                        h.getStatus(),
                        h.getMemo(),
                        h.getCreatedAt().toString(),
                        h.getUpdatedAt().toString()
                ))
                .collect(Collectors.toList());
    }

    // 이메일을 통해 사용자 ID를 반환
    @Transactional(readOnly = true)
    public Long resolveUserIdByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("User not found with email: %s", email)
                ));

        // 실제 필드명이 userId라면 getUserId(), 아니면 getId() 사용
        return user.getUserId();
    }

    // 내 게시글 조회 메서드
    @Transactional(readOnly = true)
    public List<BoardResDto> getMyPosts(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "사용자를 찾을 수 없습니다."
                ));

        // 게시글 조회
        List<BoardEntity> boards = boardRepository
                .findByUserUserId(user.getUserId(), Pageable.unpaged())
                .getContent();

        // DTO 변환
        return boards.stream()
                .map(b -> {
                    BoardResDto dto = new BoardResDto();
                    dto.setBoardId(b.getBoardId());
                    dto.setType(b.getType().name());
                    dto.setTitle(b.getTitle());
                    dto.setContent(b.getContent());
                    dto.setLikeCount(b.getLikeCount());
                    dto.setCreatedAt(b.getCreatedAt());
                    dto.setIsbn(b.getBook().getIsbn());
                    dto.setUserId(b.getUser().getUserId());
                    dto.setUsername(b.getUser().getNickname());
                    return dto;
                })
                .collect(Collectors.toList());
    }


}