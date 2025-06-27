package com.hd.book.repository;

import com.hd.book.constant.HistoryStatus;
import com.hd.book.entity.BookEntity;
import com.hd.book.entity.HistoryEntity;
import com.hd.book.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // ⭐ 추가 임포트 ⭐
import org.springframework.data.repository.query.Param; // ⭐ 추가 임포트 ⭐
import org.springframework.stereotype.Repository;

import java.time.LocalDate; // ⭐ 추가 임포트 ⭐
import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<HistoryEntity, Long> {

    // 유저의 모든 이력 조회
    List<HistoryEntity> findByUserUserId(Long userId);

    // 이미 기존에 기록이 있는지 확인
    boolean existsByBookIsbn(String isbn);

    // 상태별 조회
    List<HistoryEntity> findByUserUserIdAndStatus(Long UserId, String status);

    boolean existsByUserAndBook(UserEntity user, BookEntity book);

    // 독서 기록 최신순 조회
    List<HistoryEntity> findAllByUserOrderByStartDateDesc(UserEntity user);

    /**
     * 특정 사용자의 캘린더 월에 해당하는 독서 기록을 조회합니다.
     * 기록의 시작일 또는 종료일이 조회 대상 월에 걸쳐있으면 모두 포함합니다.
     *
     * @param user 조회할 사용자 엔티티
     * @param startOfMonth 조회 월의 첫째 날 (inclusive, 예: 2025-07-01)
     * @param endOfMonth 조회 월의 다음 달 첫째 날 (exclusive, 예: 2025-08-01)
     * @return 해당 월에 걸쳐있는 독서 기록 리스트
     */
    @Query("SELECT h FROM HistoryEntity h " +
            "WHERE h.user = :user AND " +
            "      ((h.startDate < :endOfMonth AND h.endDate >= :startOfMonth) OR " + // 기간이 월에 걸쳐있는 경우
            "       (h.endDate IS NULL AND h.startDate < :endOfMonth))") // endDate가 null인 (아직 읽는 중인) 기록의 경우
    List<HistoryEntity> findHistoriesForCalendarMonth(@Param("user") UserEntity user,
                                                      @Param("startOfMonth") LocalDate startOfMonth,
                                                      @Param("endOfMonth") LocalDate endOfMonth);
}