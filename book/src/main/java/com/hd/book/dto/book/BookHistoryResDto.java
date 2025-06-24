package com.hd.book.dto.book;

import com.hd.book.constant.HistoryStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BookHistoryResDto {
    private Long historyId;
    private Long isbn;
    private String startRead;
    private String endRead;
    private HistoryStatus status;
    private String memo;
    private String createdAt;
}
