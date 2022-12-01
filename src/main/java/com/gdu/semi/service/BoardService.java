package com.gdu.semi.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import com.gdu.semi.domain.board.BoardDTO;

public interface BoardService {
	
	public void getBoardLists(HttpServletRequest request, Model model);
	public void addBoard(HttpServletRequest request, HttpServletResponse response);
	public int removeBoard(int bdNo);
	public void addReply(HttpServletRequest request, HttpServletResponse response);

	public int modifyBoard(HttpServletRequest request, HttpServletResponse response);
	
	public String findBoardByNo(int bdNo);

}
