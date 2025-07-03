package com.hd.book.service;

import com.hd.book.constant.BoardType;
import com.hd.book.dto.board.BoardLikeGroupResDto;
import com.hd.book.dto.board.BoardResDto;
import com.hd.book.dto.board.BoardWeeklyBestResDto;
import com.hd.book.dto.board.BoardWriteDto;
import com.hd.book.entity.BoardEntity;
import com.hd.book.entity.BookEntity;
import com.hd.book.entity.UserEntity;
import com.hd.book.repository.BoardReactionRepository;
import com.hd.book.repository.BoardRepository;
import com.hd.book.util.UserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {
    private final BoardReactionRepository boardReactionRepository;
    private final BoardRepository boardRepository;
    private final BookService bookService;
    private final UserUtil userUtil;

    // 게시글 등록
    public boolean postBoard(BoardWriteDto boardWriteDto) {
        try {
            // 책 정보가 없을 경우를 대비해 조회 or 생성
            BookEntity book = bookService.getOrCreateBook(boardWriteDto.getIsbn());
            BoardEntity board = convertDtoToEntity(boardWriteDto);
            // book 연결
            board.setBook(book);
            boardRepository.save(board);
            return true;
        } catch (Exception e) {
            log.error("게시글 등록에 실패했습니다. {}", e.getMessage());
            return false;
        }
    }

    // 게시글 수정
    public boolean updateBoard(Long boardId, BoardWriteDto boardWriteDto) {
        try {
            BookEntity book = bookService.getOrCreateBook(boardWriteDto.getIsbn());
            BoardEntity board = boardRepository.findById(boardId)
                            .orElseThrow(() -> new RuntimeException("해당 게시물이 존재하지 않습니다."));
            String currentUserEmail = userUtil.getUserEmail();
            if (!board.getUser().getEmail().equals(currentUserEmail)) throw new AccessDeniedException("게시글 수정 권한이 없습니다.");

            board.setType(BoardType.valueOf(boardWriteDto.getType().toUpperCase()));
            board.setTitle(boardWriteDto.getTitle());
            board.setContent(boardWriteDto.getContent());
            board.setBook(book);
            // board.setImage(boardWriteDto.getImage());
            boardRepository.save(board);
            return true;

        } catch (Exception e) {
            log.error("게시글 수정에 실패했습니다. {}", e.getMessage());
            return false;
        }
    }

    // 게시글 삭제
    public boolean deleteBoard(Long boardId) {
        try {
            BoardEntity board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new RuntimeException("해당 게시물이 존재하지 않습니다."));
            boardRepository.delete(board);
            return true;
        } catch (Exception e) {
            log.error("게시글 삭제에 실패했습니다. {}", e.getMessage());
            return false;
        }
    }

    // 게시글 목록
    public Page<BoardResDto> boardList(Pageable pageable) {
        Page<BoardEntity> boards = boardRepository.findAll(pageable);
        return boards.map(this::convertEntityToDto);
    }

    // 게시글 조회
    public BoardResDto boardDetail(Long boardId) {
        BoardEntity book = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("해당 게시물이 존재하지 않습니다."));
        return convertEntityToDto(book);
    }

    // 도서로 게시글 조회
    public Page<BoardResDto> bookBoardList(String isbn, Pageable pageable) {
        Page<BoardEntity> boards = boardRepository.findByBookIsbn(isbn, pageable);
        return boards.map(this::convertEntityToDto);
    }

    // 좋아요 상위 게시글 조회
    public BoardLikeGroupResDto bookLikeList() {
        List<BoardEntity> bookEntities = boardRepository.findTop5ByTypeOrderByLikeCountDesc(BoardType.BOOK);
        List<BoardEntity> forumEntities = boardRepository.findTop5ByTypeOrderByLikeCountDesc(BoardType.FORUM);
        List<BoardResDto> bookBoards = bookEntities.stream()
                .map(this::convertEntityToDto)
                .toList();

        List<BoardResDto> forumBoards = forumEntities.stream()
                .map(this::convertEntityToDto)
                .toList();
        return new BoardLikeGroupResDto(bookBoards, forumBoards);
    }

    // 주간 인기글 조회
    public List<BoardWeeklyBestResDto> getWeeklyBest(int limit) {
        // 1. 최근 7일 이내 게시글만 조회
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);

        // 2. likeCount 내림차순, 0페이지부터 limit개
        Pageable pageable = PageRequest.of(0, limit, Sort.by("likeCount").descending());

        // 3. BOOK 타입 엔티티 조회
        List<BoardEntity> entities = boardRepository
                .findByTypeAndCreatedAtAfter(BoardType.BOOK, weekAgo, pageable);

        // 4. 엔티티 → DTO 매핑 (루프 사용)
        List<BoardWeeklyBestResDto> bookBoards = new ArrayList<>();
        for (BoardEntity e : entities) {
            BoardWeeklyBestResDto dto = new BoardWeeklyBestResDto();
            dto.setBoardId(e.getBoardId());
            dto.setType(e.getType().name());
            dto.setTitle(e.getTitle());
            dto.setContent(e.getContent());
            dto.setLikeCount(e.getLikeCount());
            dto.setCreatedAt(e.getCreatedAt());
            dto.setIsbn(e.getBook().getIsbn());
            dto.setUserEmail(e.getUser().getEmail());
            dto.setNickname(e.getUser().getNickname());
            bookBoards.add(dto);
        }

        return bookBoards;
    }


    // TODO : 이미지 처리
    // DTO -> Entity
     private BoardEntity convertDtoToEntity(BoardWriteDto boardWriteDto) {
         UserEntity user = userUtil.getUser();

         BoardEntity board = new BoardEntity();
         board.setType(BoardType.valueOf(boardWriteDto.getType().toUpperCase()));
         board.setTitle(boardWriteDto.getTitle());
         board.setContent(boardWriteDto.getContent());
         // board.setImage(boardWriteDto.getImage());
         board.setUser(user);return board;
     }

     // Entity -> DTO
    private BoardResDto convertEntityToDto(BoardEntity boardEntity) {
        BoardResDto boardResDto = new BoardResDto();
        boardResDto.setBoardId(boardEntity.getBoardId());
        boardResDto.setType(boardEntity.getContent());
        boardResDto.setTitle(boardEntity.getTitle());
        boardResDto.setContent(boardEntity.getContent());
        boardResDto.setLikeCount(boardEntity.getLikeCount());
        boardResDto.setCreatedAt(boardEntity.getCreatedAt());
        boardResDto.setIsbn(boardEntity.getBook().getIsbn());
        boardResDto.setUserId(boardEntity.getUser().getUserId());
        boardResDto.setUsername(boardEntity.getUser().getNickname());
        boardResDto.setIsLiked(boardReactionRepository.existsByUserAndBoard(userUtil.getUser(), boardEntity));
        return boardResDto;
    }
}
