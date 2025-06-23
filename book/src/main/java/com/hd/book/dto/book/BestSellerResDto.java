package com.hd.book.dto.book;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BestSellerResDto {
    private int totalResults;
    private int startIndex;
    private int itemsPerPage;
    private List<BookDetailResDto> item;
}
