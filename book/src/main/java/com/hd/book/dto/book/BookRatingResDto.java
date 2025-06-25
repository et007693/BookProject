package com.hd.book.dto.book;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRatingResDto {
    private String isbn;
    private Double averageScore;
    private Long totalRatings;
    private Integer userScore;
    private Map<Integer, Long> scoreCounts;
}