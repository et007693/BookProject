package com.hd.book.service;

import com.hd.book.dto.book.FavoriteBookReqDto;
import com.hd.book.dto.book.FavoriteBookResDto;
import com.hd.book.entity.BookEntity;
import com.hd.book.entity.FavoriteBookEntity;
import com.hd.book.entity.UserEntity;
import com.hd.book.repository.BookRepository;
import com.hd.book.repository.FavoriteBookRepository;
import com.hd.book.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteBookService {

    private final FavoriteBookRepository favoriteBookRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Transactional
    public FavoriteBookResDto addFavoriteBook(String username, FavoriteBookReqDto requestDto) {
        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. 이메일: " + username));

        BookEntity book = bookRepository.findByIsbn(requestDto.getIsbn())
                .orElseGet(() -> {
                    BookEntity newBook = new BookEntity();
                    newBook.setIsbn(requestDto.getIsbn());
                    newBook.setTitle(requestDto.getTitle());
                    return bookRepository.save(newBook);
                });

        if (favoriteBookRepository.findByUserAndBook(user, book).isPresent()) {
            throw new IllegalArgumentException("이미 즐겨찾기에 추가된 책입니다.");
        }

        FavoriteBookEntity favoriteBook = new FavoriteBookEntity(user, book);
        favoriteBookRepository.save(favoriteBook);

        return new FavoriteBookResDto(favoriteBook);
    }

    @Transactional
    public List<FavoriteBookResDto> getFavoriteBooks(String username) {
        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. 이메일: " + username));

        List<FavoriteBookEntity> favoriteBooks = favoriteBookRepository.findByUser(user);

        return favoriteBooks.stream()
                .map(FavoriteBookResDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeFavoriteBook(String username, String isbn) {
        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. 이메일: " + username));

        BookEntity book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 책 정보를 찾을 수 없습니다. ISBN: " + isbn));

        FavoriteBookEntity favoriteBook = favoriteBookRepository.findByUserAndBook(user, book)
                .orElseThrow(() -> new IllegalArgumentException("즐겨찾기에 없는 책입니다. ISBN: " + isbn));

        favoriteBookRepository.delete(favoriteBook);
    }
}