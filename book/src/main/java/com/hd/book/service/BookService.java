package com.hd.book.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hd.book.config.WebClientConfig;
import com.hd.book.dto.book.BestSellerResDto;
import com.hd.book.dto.book.BookRatingResDto;
import com.hd.book.entity.BookCommentEntity;
import com.hd.book.entity.BookEntity;
import com.hd.book.entity.UserEntity;
import com.hd.book.repository.BookCommentRepository;
import com.hd.book.repository.BookRepository;
import com.hd.book.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {
    private final BookRepository bookRepository;
    private final BookCommentRepository bookCommentRepository;
    private final UserUtil userUtil;
    private final WebClient webClient;
    private final WebClientConfig webClientConfig;
    private final ObjectMapper objectMapper;


    // 베스트셀러
    public BestSellerResDto getBestSeller() {
        try {
            // 1) 원시 JSON 문자열로 받아와서
            String rawJson = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/ttb/api/ItemList.aspx")
                            .queryParam("ttbkey", webClientConfig.getAuthToken())
                            .queryParam("QueryType", "Bestseller")
                            .queryParam("SearchTarget", "Book")
                            .queryParam("output", "js")
                            .queryParam("Version", 20131101)
                            .build())
                    .retrieve()
                    // 2) HTTP 오류 상태를 잡아서 예외로 전환
                    .onStatus(
                            HttpStatusCode::isError,      // <-- 수정된 부분
                            resp -> resp.bodyToMono(String.class).flatMap(body -> {
                                String msg = String.format("알라딘 API 에러: %s, body=%s",
                                        resp.statusCode(), body);
                                log.error(msg);
                                return Mono.error(new RuntimeException(msg));
                            })
                    )
                    .bodyToMono(String.class)
                    .block();

            log.info("[Bestseller API] Raw JSON: {}", rawJson);

            // 3) ObjectMapper로 DTO 변환
            BestSellerResDto dto = objectMapper.readValue(rawJson, BestSellerResDto.class);
            log.info("[Bestseller API] DTO 변환 성공: {}", dto);
            return dto;

        } catch (WebClientResponseException e) {
            log.error("[getBestSeller] HTTP 예외: status={}, body={}",
                    e.getRawStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("외부 API 호출 실패", e);

        } catch (JsonProcessingException e) {
            log.error("[getBestSeller] JSON 파싱 실패", e);
            throw new RuntimeException("Bestseller 데이터 파싱 오류", e);

        } catch (Exception e) {
            log.error("[getBestSeller] 알 수 없는 오류", e);
            throw new RuntimeException("Bestseller 조회 중 오류가 발생했습니다.", e);
        }
    }


    // 도서가 존재하면 return, 없으면 저장
    @Transactional
    public BookEntity getOrCreateBook(String isbn) {
        return bookRepository.findById(isbn)
                .orElseGet(() -> {
                    BookEntity book = new BookEntity();
                    book.setIsbn(isbn);
                    return bookRepository.save(book);
                });
    }


    // 별점 통계
    public BookRatingResDto getBookRatingStatistics(String isbn) {
        List<BookCommentEntity> comments = bookCommentRepository.findByBookIsbn(isbn);

        Double averageScore = comments.stream()
                .mapToInt(BookCommentEntity::getRate)
                .average()
                .orElse(0.0);

        Long totalRatings = (long) comments.size();

        Map<Integer, Long> scoreCounts = comments.stream()
                .collect(Collectors.groupingBy(BookCommentEntity::getRate, Collectors.counting()));

        for (int i = 1; i <= 5; i++) {
            scoreCounts.putIfAbsent(i, 0L);
        }


        Integer userScore = null;
        try {
            UserEntity currentUser = userUtil.getUser();
            Optional<BookCommentEntity> currentUserComment =
                    bookCommentRepository.findByBook_IsbnAndUser_UserId(isbn, currentUser.getUserId());

            if (currentUserComment.isPresent()) {
                userScore = currentUserComment.get().getRate();
            }
        } catch (Exception e) {
            log.warn("사용자 별점을 조회할 수 없습니다 (비로그인 또는 오류): {}", e.getMessage());
        }

        return BookRatingResDto.builder()
                .isbn(isbn)
                .averageScore(averageScore)
                .totalRatings(totalRatings)
                .userScore(userScore)
                .scoreCounts(scoreCounts)
                .build();
    }
}