package com.hd.book.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookHistoryReqDto {

    @NotNull
    private String isbn;

    @NotBlank
    @Pattern(regexp="\\d{4}-\\d{2}-\\d{2}", message = "startRead는 YYYY-MM-DD 형식이어야 합니다.")
    private String startRead;

    @NotBlank
    @Pattern(regexp="\\d{4}-\\d{2}-\\d{2}", message = "endRead는 YYYY-MM-DD 형식이어야 합니다.")
    private String endRead;

    private String memo;
}