package com.hd.book.dto.book; // 또는 dto.rating

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRatingResDto {
    private String isbn;
    private Double averageScore;
    private Long totalRatings;
    private Integer userScore;
}