package com.hd.book.dto.book; // 실제 패키지 경로에 맞게 조정해주세요.

import com.hd.book.constant.HistoryStatus; // HistoryStatus enum이 없다면 생성해야 합니다.
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate; // java.time.LocalDate 임포트

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarHistoryDto {
    private Long historyId;
    private String bookTitle; // 책 제목
    private String isbn;      // 책 ISBN
    private LocalDate startRead; // 독서 시작 날짜 (YYYY-MM-DD)
    private LocalDate endRead;   // 독서 종료 날짜 (YYYY-MM-DD)
    private HistoryStatus status; // 독서 상태 (READING, COMPLETED 등)
    private String memo;
}