package com.gdu.semi.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gdu.semi.domain.user.UserDTO;
import com.gdu.semi.service.UserService;

@Controller
public class UserController {

	@Autowired
	private UserService userService;
	
	@GetMapping("/user/join")
	public String board() {
		return "/user/join";
	}
	
	@GetMapping("/user/agree")
	public String agree() {
		return "/user/agree";
	}
	
	@GetMapping("/user/join/write")
	public String joinWrite(@RequestParam(required=false) String location
			              , @RequestParam(required = false) String promotion
			              , Model model) {
		model.addAttribute("location", location);
		model.addAttribute("promotion", promotion);
		return "user/join";
	}
	
	@ResponseBody
	@GetMapping(value="/user/checkReduceId", produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> checkReduceId(String id){
		return userService.isReduceId(id);
	}
	
	@ResponseBody
	@GetMapping(value="/user/checkReduceEmail", produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> checkReduceEmail(String email){
		return userService.isReduceEmail(email);
	}
	
	@ResponseBody
	@GetMapping(value="/user/sendAuthCode", produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> sendAuthCode(String email){
		return userService.sendAuthCode(email);
	}
	
	@PostMapping("/user/join")
	public void join(HttpServletRequest request, HttpServletResponse response) {
		userService.join(request, response);
	}
	
	
	@PostMapping("/user/retire")
	public void retire(HttpServletRequest request, HttpServletResponse response) {
		
		userService.retire(request, response);
	}
	
	@GetMapping("/user/login/form")
	public String loginForm(HttpServletRequest request, Model model) {
		
		// ?????? ?????? referer : ?????? ???????????? ????????? ??????
				model.addAttribute("url", "http://localhost:9090/semi/user/login/form");  // ????????? ??? ????????? ??? ?????? url

		
		// ????????? ?????????
		model.addAttribute("apiURL", userService.getNaverLoginApiURL(request));
		
		return "user/login";
		
	}
	
	@PostMapping("/user/login")
	public void login(HttpServletRequest request, HttpServletResponse response) {
		userService.login(request, response);
	}
	
	@GetMapping("/user/naver/login")
	public String naverLogin(HttpServletRequest request, Model model) {
		
		String access_token = userService.getNaverLoginToken(request);
		UserDTO profile = userService.getNaverLoginProfile(access_token);  // ???????????????????????? ????????? ????????? ??????
		UserDTO naverUser = userService.getNaverUserById(profile.getId()); // ?????? ???????????????????????? ????????? ??????????????? DB??? ????????? ??????
		
		// ???????????????????????? ??????????????? ?????? : ???????????????????????? ??????
		if(naverUser == null) {
			model.addAttribute("profile", profile);
			return "user/naver_join";
		}
		// ???????????????????????? ?????? ????????? ?????? : ????????? ??????
		else {
			userService.naverLogin(request, naverUser);
			return "redirect:/";
		}
		
	}
	
	@PostMapping("/user/naver/join")
	public void naverJoin(HttpServletRequest request, HttpServletResponse response) {
		userService.naverJoin(request, response);
	}

	@GetMapping("/user/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		userService.logout(request, response);
		return "redirect:/";
	}
	
	@GetMapping("/user/check/form")
	public String requiredLogin_checkForm() {
		return "user/check";
	}
	
	@ResponseBody
	   @PostMapping(value="/user/check/pw", produces="application/json")
	   public Map<String, Object> requiredLogin_checkPw(HttpServletRequest request) {
	      return userService.confirmPassword(request);
	  }

	
	@GetMapping("/user/mypage")
	public String requiredLogin_mypage() {
		return "user/mypage";
	}
	
	@PostMapping("/user/modify/info")
	   public void requiredLogin_modifyInfo(HttpServletRequest request, HttpServletResponse response) {
	      userService.modifyInfo(request, response);
	   }

	
	@PostMapping("/user/modify/pw")
	   public void requiredLogin_modifyPw(HttpServletRequest request, HttpServletResponse response) {
	      userService.modifyPassword(request, response);
	   }
	
	@GetMapping("/user/sleep/display")
	public String sleepDisplay() {
		return "user/sleep";
	}
	
	@PostMapping("/user/restore")
	public void restore(HttpServletRequest request, HttpServletResponse response) {
		userService.restoreUser(request, response);
	}
	
	// index???????????? ??????. "/"???????????? ?????????????????? ???????????? ????????? index??? ???????????? ?????? ??????
		@GetMapping("/move/index")
		public String moveindex() {
			return "index";
		}
		
		// index.jsp?????? ??????????????? ?????? ????????? JSP??????
		@GetMapping("/user/findId")
		public String findId() {
			return "user/findId";
		}
		
		// findId.jsp?????? ???????????????
		@ResponseBody
		@PostMapping(value="/user/findId/Form", produces="application/json")
		public Map<String, Object> findIdForm(@RequestParam(value="email") String email) {
			return userService.findId(email);
		}
		
		
		@GetMapping("/user/findPw")
		public String findPw() {
			return "user/findPw";
		}
		
		@ResponseBody
		@PostMapping(value="/user/sendAuthCodeAndChangePw", produces="application/json")
		public Map<String, Object> sendAuthCodeAndChangePw(@RequestParam(value="id") String id, @RequestParam(value="email") String email){
			
			return userService.sendAuthCodeAndChangePw(id,email);
		}
			
		@ResponseBody
		@PostMapping(value="/user/checkReduceIDAndEmail",  produces="application/json")
		public Map<String, Object> checkReduceIDAndEmail(@RequestParam(value="id") String id, @RequestParam(value="email") String email){
			return userService.selectIdAndEmail(id,email);
		}

		@GetMapping("/admin/admin")
		public String admin() {
			return "/admin/admin";
		}
		

		@GetMapping("/admin/check")
		public String check() {
			return "/admin/check";
		}

		
		@ResponseBody
		@GetMapping(value="/admin/check/list.json", produces="application/json; charset=UTF-8")
		public List<UserDTO> list() {
			return userService.getAllUserList();
		}
		
		@ResponseBody
		@PostMapping(value="/user/remove", produces="application/json; charset=UTF-8")
		public Map<String, Object> remove(HttpServletRequest request) {
			return userService.removeUsers(request);
		}
			
		

	
	
	
}

