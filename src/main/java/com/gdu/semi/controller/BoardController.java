package com.gdu.semi.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gdu.semi.service.BoardService;

@Controller
public class BoardController {

	@Autowired
	private BoardService boardService;
	
	@GetMapping("/")
	public String index() {
		return "index";
	}
	
	
	
	@GetMapping("/board/list")
	public String board(HttpServletRequest request, Model model) {
		boardService.getBoardLists(request, model);
		return "/board/list2";
	}
	
	@GetMapping("/board/write")
	public String write() {
		return "/board/write";
	}
	
	@PostMapping("/board/add")  // 글 추가 후 목록보기로 이동
	public void addBoard(HttpServletRequest request, HttpServletResponse response) {
		boardService.addBoard(request, response);
		
		//return "redirect:/board/list";
	}
	
	@PostMapping("/board/rmv")
	public String rmvBoard(@RequestParam("bdNo") int bdNo) {
		boardService.removeBoard(bdNo);
		return "redirect:/board/list";
	}
	
	
	@PostMapping("/board/reply/add")
	public void addReply(HttpServletRequest request, HttpServletResponse response) {
		boardService.addReply(request, response);
	
		//return "redirect:/board/list";
	}
	
	@PostMapping("/board/edit")
	public String editBoard(int bdNo, Model model) {
		model.addAttribute("board", boardService.findBoardByNo(bdNo));
		return "/board/edit";
	}
	
	@PostMapping("/board/modify")
	public void modifyBoard(HttpServletRequest request, HttpServletResponse response) {
		boardService.modifyBoard(request, response);
		
	}
	
}



