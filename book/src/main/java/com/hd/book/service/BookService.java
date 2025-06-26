package com.hd.book.service;

import com.hd.book.config.WebClientConfig;
import com.hd.book.dto.book.BestSellerResDto;
import com.hd.book.entity.BookEntity;
import com.hd.book.repository.BookRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
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
}
