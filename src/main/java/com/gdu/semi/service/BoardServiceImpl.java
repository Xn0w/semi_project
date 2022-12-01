package com.gdu.semi.service;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.gdu.semi.domain.board.BoardDTO;
import com.gdu.semi.domain.user.UserDTO;
import com.gdu.semi.mapper.BoardMapper;
import com.gdu.semi.util.PageUtil;
import com.gdu.semi.util.SecurityUtil;

@Service
public class BoardServiceImpl implements BoardService {

	@Autowired
	private BoardMapper boardMapper;
	@Autowired
	private PageUtil pageUtil;
	@Autowired
	private SecurityUtil securityUtil;
	
	
	
	@Override  // 목록보기
	public void getBoardLists(HttpServletRequest request, Model model) {
		
		Optional<String> opt = Optional.ofNullable(request.getParameter("page"));
		int page = Integer.parseInt(opt.orElse("1"));
		
		Optional<String> opt2 = Optional.ofNullable(request.getParameter("recordPerPage"));
		int recordPerPage = Integer.parseInt(opt2.orElse("10"));
		
		// 전체 게시글 수는 DB에서 select count로 구하고 매퍼통해서 결과값 가져오기
		int totalRecord = boardMapper.selectAllBoardCnt();
		
		// 페이징에 필요한 계산 완.
		pageUtil.setPageUtil(page,totalRecord);
		
		// DB로 보낼 map(begin, end)
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("begin", pageUtil.getBegin());
		map.put("end", pageUtil.getEnd());
		
		// db에서 목록 가져오기
		List<BoardDTO> boardList = boardMapper.selectAllList(map);

		//System.out.println(boardList);
		model.addAttribute("boardList", boardList);
		model.addAttribute("paging", pageUtil.getPaging(request.getContextPath() + "/board/list"));
		model.addAttribute("beginNo", totalRecord - (page - 1) * pageUtil.getRecordPerPage());
	}
	

	// 삽입
	@Override
	public void addBoard(HttpServletRequest request, HttpServletResponse response) {
		
		// 임시 로그인을 위해서 ID를 세션에서 꺼내와서 ID를 boardDTO 객체에 담아준다!
		
		HttpSession session = request.getSession(); 
		UserDTO loginUser = (UserDTO)session.getAttribute("loginUser");
		String id = loginUser.getId();
		
		//String id = request.getParameter("id");
		String bdTitle = securityUtil.preventXSS(request.getParameter("bdTitle"));
		String bdContent = securityUtil.preventXSS(request.getParameter("bdContent"));
		String bdIp = request.getRemoteAddr();
		
		BoardDTO board = BoardDTO.builder()
				.id(id)
				.bdTitle(bdTitle)
				.bdContent(bdContent)
				.bdIp(bdIp)
				.build();
		int result = boardMapper.insertBoard(board);
		response.setContentType("text/html; charset=UTF-8");

		try {
			PrintWriter out = response.getWriter();
			if(result > 0) {
				boardMapper.updatePoint(id);
				loginUser.setPoint(loginUser.getPoint() + 10);
				out.println("<script>");
				out.println("alert('인삿말이 등록되었습니다.');");
				out.println("location.href='" + request.getContextPath() + "/board/list'");
				out.println("</script>");
			} else {
				out.println("<script>");
				out.println("alert('인삿말이 등록되지 않았습니다.');");
				out.println("history.back();");
				out.println("</script>");
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//return boardMapper.insertBoard(board);
		
		
	}
	
	@Override
	public int removeBoard(int bdNo) {
		
		return boardMapper.deleteBoardByNo(bdNo);
	}
	
	
	@Transactional
	@Override
	public void addReply(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		UserDTO loginUser = (UserDTO)session.getAttribute("loginUser");
		String id = loginUser.getId();
		
		String bdTitle = securityUtil.preventXSS(request.getParameter("bdTitle"));
		String bdContent = securityUtil.preventXSS(request.getParameter("bdContent"));
		int bdDepth = Integer.parseInt(request.getParameter("bdDepth"));
		int bdGroupNo = Integer.parseInt(request.getParameter("bdGroupNo"));
		int bdGroupOrder = Integer.parseInt(request.getParameter("bdGroupOrder"));
		String bdIp = request.getRemoteAddr();

		
		// 원글 dto
		BoardDTO board = BoardDTO.builder()
				.bdDepth(bdDepth)
				.bdGroupNo(bdGroupNo)
				.bdGroupOrder(bdGroupOrder)
				.build();
		
		boardMapper.updatePrevReply(board);
		
		// 답글 dto
		BoardDTO reply = BoardDTO.builder()
				.id(id)
				.bdTitle(bdTitle)
				.bdContent(bdContent)
				.bdIp(bdIp)
				.bdDepth(bdDepth + 1)
				.bdGroupNo(bdGroupNo)
				.bdGroupOrder(bdGroupOrder + 1)
				.build();
		
		int result = boardMapper.insertReply(reply);
		response.setContentType("text/html; charset=UTF-8");
		
		try {
			PrintWriter out = response.getWriter();
			if(result > 0) {
				boardMapper.updatePoint(id);
				loginUser.setPoint(loginUser.getPoint() + 10);
				out.println("<script>");
				out.println("alert('답글이 등록되었습니다.');");
				out.println("location.href='" + request.getContextPath() + "/board/list'");
				out.println("</script>");
			} else {
				out.println("<script>");
				out.println("alert('답글이 등록되지 않았습니다.');");
				out.println("history.back();");
				out.println("</script>");
			}
			
			
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		//return boardMapper.insertReply(reply);
		
		
	}
	
	
	@Override
	public String findBoardByNo(int bdNo) {
		
		//model.addAttribute("board", boardMapper.selectBoardByNo(bdNo));
		return boardMapper.selectBoardByNo(bdNo);
	}
	
	
	@Override
	public int modifyBoard(HttpServletRequest request, HttpServletResponse response) {
		
		BoardDTO board = BoardDTO.builder()
				.bdTitle(securityUtil.preventXSS(request.getParameter("bdTitle")))
				.bdContent(securityUtil.preventXSS(request.getParameter("bdContent")))
				.bdNo(Integer.parseInt(request.getParameter("bdNo")))
				.build();
		int result = boardMapper.updateBoardByNo(board);
		
		response.setContentType("text/html; charset=UTF-8");
		
		try {
			PrintWriter out = response.getWriter();
			if(result>0) {
				out.println("<script>");
				out.println("alert('게시글이 수정되었습니다.');");
				out.println("location.href='" + request.getContextPath() + "/board/list';" );
				out.println("</script>");
			} else {
				out.println("<script>");
				out.println("alert('게시글이 수정되지 않았습니다. 다시 시도해주세요.');");
				out.println("history.back();");
				out.println("</script>");
			}
			out.close();
		} catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	
	
}
