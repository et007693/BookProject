package com.hd.book.dto.book;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookHistoryUpdateDto {
    @Pattern(regexp="\\d{4}-\\d{2}-\\d{2}",
            message = "startRead는 YYYY-MM-DD 형식이어야 합니다.")
    private String startRead;

    @Pattern(regexp="\\d{4}-\\d{2}-\\d{2}",
            message = "endRead는 YYYY-MM-DD 형식이어야 합니다.")
    private String endRead;

    // "READING" or "COMPLETED"
    private String status;

    private String memo;
}
