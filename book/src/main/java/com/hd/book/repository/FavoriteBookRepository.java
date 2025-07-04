package com.hd.book.repository;

import com.hd.book.entity.BookEntity;
import com.hd.book.entity.FavoriteBookEntity;
import com.hd.book.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteBookRepository extends JpaRepository<FavoriteBookEntity, Long> {

    List<FavoriteBookEntity> findByUser(UserEntity user);

    Optional<FavoriteBookEntity> findByUserAndBook(UserEntity user, BookEntity book);
}