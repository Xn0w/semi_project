package com.gdu.semi.mapper;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.annotations.Mapper;

import com.gdu.semi.domain.board.BoardDTO;
import com.gdu.semi.domain.user.UserDTO;

@Mapper
public interface BoardMapper {
	
	// 로그인 회원 코드
	public UserDTO selectUserByMap(Map<String, Object> map);
	
	public String selectBoardByNo(int bdNo);
	
	public int selectAllBoardCnt();
	public List<BoardDTO> selectAllList(Map<String, Object> map);   // begin, end 담아보낼 map
	public int insertBoard(BoardDTO board);   // 결과는 1 or 0인 int, 받아오는 파라미터 타입이 BoardDTO
	public int deleteBoardByNo(int bdNo);
	public int insertReply(BoardDTO board);
	
	public int updatePrevReply(BoardDTO board);
	public int updatePoint(String id);
	
	// 원글 내용 수정
	public int updateBoardByNo(BoardDTO board);
	
}
