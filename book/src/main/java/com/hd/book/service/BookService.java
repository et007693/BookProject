package com.hd.book.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

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


    // 베스트셀러
    public BestSellerResDto getBestSeller() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/ttb/api/ItemList.aspx")
                        .queryParam("ttbkey", webClientConfig.getAuthToken())
                        .queryParam("QueryType", "Bestseller")
                        .queryParam("SearchTarget", "Book")
                        .queryParam("output", "js")
                        .queryParam("Version", 20131101)
                        .build()

                )
                .retrieve()
                .bodyToMono(BestSellerResDto.class)
                .block();
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