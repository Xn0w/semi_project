package com.gdu.semi.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gdu.semi.domain.user.RetireUserDTO;
import com.gdu.semi.domain.user.SleepUserDTO;
import com.gdu.semi.domain.user.UserDTO;
import com.gdu.semi.mapper.UserMapper;
import com.gdu.semi.util.SecurityUtil;

@PropertySource(value = {"classpath:email.properties"})
@Service
public class UserServiceImpl implements UserService {

	@Value(value = "${mail.username}")
	private String username;
	
	@Value(value="${mail.password}")
	private String password;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private SecurityUtil securityUtil;
	
	@Override
	public Map<String, Object> isReduceId(String id) {
		
		// 조회 조건으로 사용할 Map
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("isUser", userMapper.selectUserByMap(map) != null);
		result.put("isRetireUser", userMapper.selectRetireUserById(id) != null);
		return result;
		
	}
	
	@Override
	public Map<String, Object> isReduceEmail(String email) {
		
		// 조회 조건으로 사용할 Map
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("email", email);
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("isUser", userMapper.selectUserByMap(map) != null);
		return result;
		
	}
	
	@Override
	public Map<String, Object> sendAuthCode(String email) {
		
		
		
		// 인증코드 만들기
		String authCode = securityUtil.getAuthCode(6);  
		System.out.println("발송된 인증코드 : " + authCode);
		
		// 이메일 전송을 위한 필수 속성을 Properties 객체로 생성
		Properties properties = new Properties();
		properties.put("mail.smtp.host", "smtp.gmail.com");  // 구글 메일로 보냄(보내는 메일은 구글 메일만 가능)
		properties.put("mail.smtp.port", "587");             // 구글 메일로 보내는 포트 번호
		properties.put("mail.smtp.auth", "true");            // 인증된 메일
		properties.put("mail.smtp.starttls.enable", "true"); // TLS 허용
		
		// 사용자 정보를 javax.mail.Session에 저장
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		
		// 이메일 작성 및 전송
		try {
			
			Message message = new MimeMessage(session);
			
			message.setFrom(new InternetAddress(username, "인증코드관리자"));            // 보내는사람
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));  // 받는사람
			message.setSubject("[Application] 인증 요청 메일입니다.");                   // 제목
			message.setContent("인증번호는 <strong>" + authCode + "</strong>입니다.", "text/html; charset=UTF-8");  // 내용
			
			Transport.send(message);  // 이메일 전송
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		// join.jsp로 생성한 인증코드를 보내줘야 함
		// 그래야 사용자가 입력한 인증코드와 비교를 할 수 있음
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("authCode", authCode);
		return result;
		
	}
	
	@Transactional  // INSERT,UPDATE,DELETE 중 2개 이상이 호출되는 서비스에서 필요함
	@Override
	public void join(HttpServletRequest request, HttpServletResponse response) {
		
		// 파라미터
		String id = request.getParameter("id");
		String pw = request.getParameter("pw");
		String name = request.getParameter("name");
		String gender = request.getParameter("gender");
		String mobile = request.getParameter("mobile");
		String birthyear = request.getParameter("birthyear");
		String birthmonth = request.getParameter("birthmonth");
		String birthdate = request.getParameter("birthdate");
		String postcode = request.getParameter("postcode");
		String roadAddress = request.getParameter("roadAddress");
		String jibunAddress = request.getParameter("jibunAddress");
		String detailAddress = request.getParameter("detailAddress");
		String extraAddress = request.getParameter("extraAddress");
		String email = request.getParameter("email");
		String location = request.getParameter("location");
		String promotion = request.getParameter("promotion");
		
		
		Optional<String> opt1 = Optional.ofNullable(request.getParameter("point"));
		int page = Integer.parseInt(opt1.orElse("0"));
				

		
		// 일부 파라미터는 DB에 넣을 수 있도록 가공
		pw = securityUtil.sha256(pw);
		name = securityUtil.preventXSS(name);
		String birthday = birthmonth + birthdate;
		detailAddress = securityUtil.preventXSS(detailAddress);
		int agreeCode = 0;  // 필수 동의
		if(!location.isEmpty() && promotion.isEmpty()) {
			agreeCode = 1;  // 필수 + 위치
		} else if(location.isEmpty() && !promotion.isEmpty()) {
			agreeCode = 2;  // 필수 + 프로모션
		} else if(!location.isEmpty() && !promotion.isEmpty()) {
			agreeCode = 3;  // 필수 + 위치 + 프로모션
		}
		
		// DB로 보낼 UserDTO 만들기
		UserDTO user = UserDTO.builder()
				.id(id)
				.pw(pw)
				.name(name)
				.gender(gender)
				.email(email)
				.mobile(mobile)
				.birthyear(birthyear)
				.birthday(birthday)
				.postcode(postcode)
				.roadAddress(roadAddress)
				.jibunAddress(jibunAddress)
				.detailAddress(detailAddress)
				.extraAddress(extraAddress)
				.agreeCode(agreeCode)
				.build();
				
		// 회원가입처리
		int result = userMapper.insertUser(user);
		
		// 응답
		try {
			
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			
			if(result > 0) {
				
				// 조회 조건으로 사용할 Map
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", id);
				
				// 로그인 처리를 위해서 session에 로그인 된 사용자 정보를 올려둠
				request.getSession().setAttribute("loginUser", userMapper.selectUserByMap(map));
				
				// 로그인 기록 남기기
				int updateResult = userMapper.updateAccessLog(id);
				if(updateResult == 0) {
					userMapper.insertAccessLog(id);
				}
				
				out.println("<script>");
				out.println("alert('회원 가입되었습니다.');");
				out.println("location.href='" + request.getContextPath() + "/user/login/form';");
				out.println("</script>");
				
			} else {
				
				out.println("<script>");
				out.println("alert('회원 가입에 실패했습니다.');");
				out.println("history.go(-2);");
				out.println("</script>");
				
			}
			
			out.close();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Transactional  // INSERT,UPDATE,DELETE 중 2개 이상이 호출되는 서비스에서 필요함
	@Override
	public void retire(HttpServletRequest request, HttpServletResponse response) {
		
		
		
		// 탈퇴할 회원의 userNo, id, joinDate는 session의 loginUser에 저장되어 있다.
		HttpSession session = request.getSession();
		UserDTO loginUser = (UserDTO)session.getAttribute("loginUser");
		
		// 탈퇴할 회원 RetireUserDTO 생성
		RetireUserDTO retireUser = RetireUserDTO.builder()
				.userNo(loginUser.getUserNo())
				.id(loginUser.getId())
				.joinDate(loginUser.getJoinDate())
				.build();
		
		
		
		// 탈퇴처리
		int deleteResult = userMapper.deleteUser(loginUser.getUserNo());
		int insertResult = userMapper.insertRetireUser(retireUser);
		
		// 응답
		try {
			
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			
			if(deleteResult > 0 && insertResult > 0) {
				
				// session 초기화(로그인 사용자 loginUser 삭제를 위해서)
				session.invalidate();
				
				out.println("<script>");
				out.println("alert('회원 탈퇴되었습니다.');");
				out.println("location.href='" + request.getContextPath() + "';");
				out.println("</script>");
				
			} else {
				
				out.println("<script>");
				out.println("alert('회원 탈퇴에 실패했습니다.');");
				out.println("history.back();");
				out.println("</script>");
				
			}
			
			out.close();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	  @Override
	   public void login(HttpServletRequest request, HttpServletResponse response) {
	         
	      // 파라미터
	      String url = request.getParameter("url");
	      String id = request.getParameter("id");
	      String pw = request.getParameter("pw");
	      
	      // pw는 파라미터는 DB에 저장된 데이터와 동일한 형태로 가공
	      pw = securityUtil.sha256(pw);
	      
	      // DB로 보낼 UserDTO 생성
	      /* UserDTO user = UserDTO.builder()
	            .id(id)
	            .pw(pw)
	            .build(); */
	      
	      // selectUserByMap 추가 부분
	      Map<String, Object> map = new HashMap<String, Object>();
	      map.put("id", id);
	      map.put("pw", pw);
	      
	      // selectUserByMap 수정 부분
	      // UserDTO loginUser = userMapper.selectUserByIdPw(user);
	      
	      UserDTO loginUser = userMapper.selectUserByMap(map);
	      
	      // id, pw가 일치하는 회원이 있다 : 로그인 기록 남기기 + session에 loginUser 저장
	      if(loginUser != null) {
	               
	         // 로그인 처리를 위해서 session에 로그인 된 사용자 정보를 올려둠
	         request.getSession().setAttribute("loginUser", loginUser);
	         
	         // 이동(로그인페이지 이전 페이지로 되돌아기기)
	         try {
	            response.sendRedirect(url);
	         } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	         }
	         
	      }
	      // id, pw가 일치하는 회원이 없다 : 로그인 페이지로 돌려보내기
	      else {
	         try {
	            
	            response.setContentType("text/html; charset=UTF-8");
	            PrintWriter out = response.getWriter();
	   
	               out.println("<script>");
	               out.println("alert('일치하는 회원정보가 없습니다.')");
	               out.println("location.href='"+ request.getContextPath() + "';");
	               out.println("</script>");
	               out.close();
	         } catch (Exception e) {
	            // TODO: handle exception
	         }
	      }
	   }
	
	@Override
	public void keepLogin(HttpServletRequest request, HttpServletResponse response) {
		
		
		// 파라미터
		String id = request.getParameter("id");
		String keepLogin = request.getParameter("keepLogin");
		
		// 로그인 유지를 체크한 경우
		if(keepLogin != null) {
			
			// session_id
			String sessionId = request.getSession().getId();
			
			// session_id를 쿠키에 저장하기
			Cookie cookie = new Cookie("keepLogin", sessionId);
			cookie.setMaxAge(60 * 60 * 24 * 15);  // 15일
			cookie.setPath(request.getContextPath());
			response.addCookie(cookie);
			
			// session_id를 DB에 저장하기
			UserDTO user = UserDTO.builder()
					.id(id)
					.sessionId(sessionId)
					.sessionLimitDate(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 15))  // 현재타임스탬프 + 15일에 해당하는 타임스탬프
					.build();

			userMapper.updateSessionInfo(user);
			
		}
		// 로그인 유지를 체크하지 않은 경우
		else {
			
			// keepLogin 쿠키 제거하기
			Cookie cookie = new Cookie("keepLogin", "");
			cookie.setMaxAge(0);  // 쿠키 유지 시간이 0이면 삭제를 의미함
			cookie.setPath(request.getContextPath());
			response.addCookie(cookie);
			
		}
		
	}
	
	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response) {
		
		// 로그아웃 처리
		HttpSession session = request.getSession();
		if(session.getAttribute("loginUser") != null) {
			session.invalidate();
		}
		
		// 로그인 유지 풀기
		Cookie cookie = new Cookie("keepLogin", "");
		cookie.setMaxAge(0);  // 쿠키 유지 시간이 0이면 삭제를 의미함
		cookie.setPath(request.getContextPath());
		response.addCookie(cookie);
		
	}
	
	@Override
	public UserDTO getUserBySessionId(Map<String, Object> map) {
		return userMapper.selectUserByMap(map);
	}
	
	@Override
	public Map<String, Object> confirmPassword(HttpServletRequest request) {
		
		// 파라미터 pw + SHA-256 처리
		String pw = securityUtil.sha256(request.getParameter("pw"));
		
		// id
		HttpSession session = request.getSession();
		String id = ((UserDTO)session.getAttribute("loginUser")).getId();
		
		// 조회 조건으로 사용할 Map
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("pw", pw);
		
		// id, pw가 일치하는 회원 조회
		UserDTO user = userMapper.selectUserByMap(map);
		
		// 결과 반환
		Map<String, Object> result= new HashMap<String, Object>();
		result.put("isUser", user != null);
		return result;
		
	}
	
	@Override
	   public void modifyInfo(HttpServletRequest request, HttpServletResponse response) {
	      
	      HttpSession session = request.getSession();
	      UserDTO loginUser = ((UserDTO)session.getAttribute("loginUser"));
	      
	      
	      String postcode = request.getParameter("postcode"); 
	      String roadAddress = request.getParameter("roadAddress");
	      String jibunAddress = request.getParameter("jibunAddress");
	      String detailAddress = request.getParameter("detailAddress");
	      String extraAddress = request.getParameter("extraAddress");
	      String mobile = request.getParameter("mobile");
	      String email = request.getParameter("email");
	      
	      
	      String id = loginUser.getId();
	      /*
	       * String postcode = loginUser.getPostcode(); String roadAddress =
	       * loginUser.getRoadAddress(); String jibunAddress =
	       * loginUser.getJibunAddress(); String detailAddress =
	       * loginUser.getDetailAddress(); String extraAddress =
	       * loginUser.getExtraAddress(); String mobile = loginUser.getMobile(); String
	       * email = loginUser.getEmail();
	       */
	      
	      UserDTO user = UserDTO.builder()
	            .id(id)
	            .postcode(postcode)
	            .roadAddress(roadAddress)
	            .jibunAddress(jibunAddress)
	            .detailAddress(detailAddress)
	            .extraAddress(extraAddress)
	            .mobile(mobile)
	            .email(email)
	            .build();
	      
	      int result = userMapper.updateUserInfo(user);
	      try {
	         
	         response.setContentType("text/html; charset=UTF-8");
	         PrintWriter out = response.getWriter();
	         
	         if(result > 0) {
	            
	        	loginUser.setPostcode(postcode);
	            loginUser.setMobile(mobile);
	            loginUser.setEmail(email);
	            loginUser.setRoadAddress(roadAddress);
	            loginUser.setJibunAddress(jibunAddress);
	            loginUser.setDetailAddress(detailAddress);
	            loginUser.setExtraAddress(extraAddress);
	            
	            
	            out.println("<script>");
	            out.println("alert('정보가 수정되었습니다.');");
	            out.println("location.href='" + request.getContextPath() + "/user/login/form';");
	            out.println("</script>");
	            
	         } else {
	            
	            out.println("<script>");
	            out.println("alert('정보가 수정되지 않았습니다.');");
	            out.println("history.back();");
	            out.println("</script>");
	            
	         }
	         
	         out.close();
	         
	      } catch(Exception e) {
	         e.printStackTrace();
	      }
	      
	   }

	   
	   @Override
	   public void modifyPassword(HttpServletRequest request, HttpServletResponse response) {
	      
	      HttpSession session = request.getSession();
	      UserDTO loginUser = ((UserDTO)session.getAttribute("loginUser"));
	      
	      String pw = securityUtil.sha256(request.getParameter("pw"));
	      
	      if(pw.equals(loginUser.getPw())) {

	         try {
	            
	            response.setContentType("text/html; charset=UTF-8");
	            PrintWriter out = response.getWriter();
	            
	            out.println("<script>");
	            out.println("alert('현재 비밀번호와 동일한 비밀번호로 변경할 수 없습니다.');");
	            out.println("history.back();");
	            out.println("</script>");
	            out.close();
	            
	         } catch(Exception e) {
	            e.printStackTrace();
	         }
	      }
	      
	      String id = loginUser.getId();
	      
	      UserDTO user = UserDTO.builder()
	            .id(id)
	            .pw(pw)
	            .build();
	      
	      int result = userMapper.updateUserPassword(user);
	      
	      try {
	         
	         response.setContentType("text/html; charset=UTF-8");
	         PrintWriter out = response.getWriter();
	         
	         if(result > 0) {
	            
	            loginUser.setPw(pw);
	            
	            out.println("<script>");
	            out.println("alert('비밀번호가 수정되었습니다.');");
	            out.println("location.href='" + request.getContextPath() + "/user/login/form';");
	            out.println("</script>");
	            
	         } else {
	            
	            out.println("<script>");
	            out.println("alert('비밀번호가 수정되지 않았습니다.');");
	            out.println("history.back();");
	            out.println("</script>");
	            
	         }
	         
	         out.close();
	         
	      } catch(Exception e) {
	         e.printStackTrace();
	      }
	      
	   }
	

	
	@Transactional
	@Override
	public void sleepUserHandle() {
		int insertCount = userMapper.insertSleepUser();
		if(insertCount > 0) {
			userMapper.deleteUserForSleep();
		}
	}
	
	@Override
	public SleepUserDTO getSleepUserById(String id) {
		return userMapper.selectSleepUserById(id);
	}
	
	@Transactional
	@Override
	public void restoreUser(HttpServletRequest request, HttpServletResponse response) {
		
		// 계정 복원을 원하는 사용자의 아이디
		HttpSession session = request.getSession();
		SleepUserDTO sleepUser = (SleepUserDTO)session.getAttribute("sleepUser");
		String id = sleepUser.getId();
		
		// 계정복구진행
		int insertCount = userMapper.insertRestoreUser(id);
		int deleteCount = 0;
		if(insertCount > 0) {
			deleteCount = userMapper.deleteSleepUser(id);
		}
		
		// 응답
		try {

			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			
			if(insertCount > 0 && deleteCount > 0) {
				
				// session에 저장된 sleepUser 제거
				session.removeAttribute("sleepUser");
				
				out.println("<script>");
				out.println("alert('휴면 계정이 복구되었습니다. 휴면 계정 활성화를 위해 곧바로 로그인을 해 주세요.');");
				out.println("location.href='" + request.getContextPath() + "/user/login/form';");
				out.println("</script>");
				
			} else {
				
				out.println("<script>");
				out.println("alert('휴면 계정이 복구되지 않았습니다.');");
				out.println("history.back();");
				out.println("</script>");
				
			}
			
			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Override
	public String getNaverLoginApiURL(HttpServletRequest request) {
	    
		String apiURL = null;
		
		try {
			
			String clientId = "V6JXYuVnYeLv8zR7ZE_M";
			String redirectURI = URLEncoder.encode("http://localhost:9090" + request.getContextPath() + "/user/naver/login", "UTF-8");  // 네이버 로그인 Callback URL에 작성한 주소 입력 
			SecureRandom random = new SecureRandom();
			String state = new BigInteger(130, random).toString();
			
			apiURL = "https://nid.naver.com/oauth2.0/authorize?response_type=code";
			apiURL += "&client_id=" + clientId;
			apiURL += "&redirect_uri=" + redirectURI;
			apiURL += "&state=" + state;
			
			HttpSession session = request.getSession();
			session.setAttribute("state", state);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return apiURL;
		
	}
	
	@Override
	public String getNaverLoginToken(HttpServletRequest request) {
		
		// access_token 받기
		
		String clientId = "V6JXYuVnYeLv8zR7ZE_M";
		String clientSecret = "R4RDqUYkON";
		String code = request.getParameter("code");
		String state = request.getParameter("state");
		
		String redirectURI = null;
		try {
			redirectURI = URLEncoder.encode("http://localhost:9090" + request.getContextPath(), "UTF-8");
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		StringBuffer res = new StringBuffer();  
		try {
			
			String apiURL;
			apiURL = "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code&";
			apiURL += "client_id=" + clientId;
			apiURL += "&client_secret=" + clientSecret;
			apiURL += "&redirect_uri=" + redirectURI;
			apiURL += "&code=" + code;
			apiURL += "&state=" + state;
			
			URL url = new URL(apiURL);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.setRequestMethod("GET");
			int responseCode = con.getResponseCode();
			BufferedReader br;
			if(responseCode == 200) {
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			}
			String inputLine;
			while ((inputLine = br.readLine()) != null) {
				res.append(inputLine);
			}
			br.close();
			con.disconnect();
		
		
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		JSONObject obj = new JSONObject(res.toString());
		String access_token = obj.getString("access_token");
		return access_token;
		
	}
	
	@Override
	public UserDTO getNaverLoginProfile(String access_token) {
		
		// access_token을 이용해서 profile 받기
		String header = "Bearer " + access_token;
		
		StringBuffer sb = new StringBuffer();
		
		try {
			
			String apiURL = "https://openapi.naver.com/v1/nid/me";
			URL url = new URL(apiURL);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Authorization", header);
			int responseCode = con.getResponseCode();
			BufferedReader br;
			if(responseCode == 200) {
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			}
			String inputLine;
			while ((inputLine = br.readLine()) != null) {
				sb.append(inputLine);
			}
			br.close();
			con.disconnect();
			
		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 받아온 profile을 UserDTO로 만들어서 반환
		UserDTO user = null;
		try {
			
			JSONObject profile = new JSONObject(sb.toString()).getJSONObject("response");
			String id = profile.getString("id");
			String name = profile.getString("name");
			String gender = profile.getString("gender");
			String email = profile.getString("email");
			String mobile = profile.getString("mobile").replaceAll("-", "");
			String birthyear = profile.getString("birthyear");
			String birthday = profile.getString("birthday").replace("-", "");
			
			user = UserDTO.builder()
					.id(id)
					.name(name)
					.gender(gender)
					.email(email)
					.mobile(mobile)
					.birthyear(birthyear)
					.birthday(birthday)
					.build();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		return user;
		
	}
	
	@Override
	public UserDTO getNaverUserById(String id) {
		
		// 조회 조건으로 사용할 Map
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		
		return userMapper.selectUserByMap(map);
		
	}
	
	@Transactional
	@Override
	public void naverLogin(HttpServletRequest request, UserDTO naverUser) {
		
		// 로그인 처리를 위해서 session에 로그인 된 사용자 정보를 올려둠
		request.getSession().setAttribute("loginUser", naverUser);
		
		// 로그인 기록 남기기
		String id = naverUser.getId();
		int updateResult = userMapper.updateAccessLog(id);
		if(updateResult == 0) {
			userMapper.insertAccessLog(id);
		}
		
	}
	
	@Override
	public void naverJoin(HttpServletRequest request, HttpServletResponse response) {
		
		// 파라미터
		String id = request.getParameter("id");
		String name = request.getParameter("name");
		String gender = request.getParameter("gender");
		String mobile = request.getParameter("mobile");
		String birthyear = request.getParameter("birthyear");
		String birthmonth = request.getParameter("birthmonth");
		String birthdate = request.getParameter("birthdate");
		String email = request.getParameter("email");
		String location = request.getParameter("location");
		String promotion = request.getParameter("promotion");
		
		// 일부 파라미터는 DB에 넣을 수 있도록 가공
		name = securityUtil.preventXSS(name);
		String birthday = birthmonth + birthdate;
		String pw = securityUtil.sha256(birthyear + birthday);  // 생년월일을 초기비번 8자리로 제공하기로 함
		
		int agreeCode = 0;  // 필수 동의
		if(location != null && promotion == null) {
			agreeCode = 1;  // 필수 + 위치
		} else if(location == null && promotion != null) {
			agreeCode = 2;  // 필수 + 프로모션
		} else if(location != null && promotion != null) {
			agreeCode = 3;  // 필수 + 위치 + 프로모션
		}
		
		// DB로 보낼 UserDTO 만들기
		UserDTO user = UserDTO.builder()
				.id(id)
				.pw(pw)
				.name(name)
				.gender(gender)
				.email(email)
				.mobile(mobile)
				.birthyear(birthyear)
				.birthday(birthday)
				.agreeCode(agreeCode)
				.snsType("naver")  // 네이버로그인으로 가입하면 naver를 저장해 두기로 함
				.build();
				
		// 회원가입처리
		int result = userMapper.insertNaverUser(user);
		
		// 응답
		try {
			
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			
			if(result > 0) {
				
				// 조회 조건으로 사용할 Map
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", id);
				
				// 로그인 처리를 위해서 session에 로그인 된 사용자 정보를 올려둠
				request.getSession().setAttribute("loginUser", userMapper.selectUserByMap(map));
				
				// 로그인 기록 남기기
				int updateResult = userMapper.updateAccessLog(id);
				if(updateResult == 0) {
					userMapper.insertAccessLog(id);
				}
				
				out.println("<script>");
				out.println("alert('회원 가입되었습니다.');");
				out.println("location.href='" + request.getContextPath() + "/user/login/form';");
				out.println("</script>");
				
			} else {
				
				out.println("<script>");
				out.println("alert('회원 가입에 실패했습니다.');");
				out.println("history.go(-2);");
				out.println("</script>");
				
			}
			
			out.close();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public Map<String, Object> findId(String email) {
		
		// 조회조건으로 사용할 맵
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("email", email);

		
		// 반환값으로 사용할 맵
		Map<String, Object> result = new HashMap<String, Object>();
		
		result.put("findIdInSleepUser", userMapper.selectIdSleepUser(map));
		result.put("findIdInUser", userMapper.selectUserByMap(map));
		
		System.out.println(result);
		
		return result;
		
		
		
//		Map<String, Object> result = new HashMap<String, Object>();
//		result.put("test", email);
//		return result;
		
	}
	
	@Override
	public Map<String, Object> selectIdAndEmail(String id, String email) {
		
		//조회시 사용할 맵
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("email", email);
		
		// 결과값을 받을 맵
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("isUser", userMapper.selectUserByMap(map) != null);
		
		return result;
	}
	
	
	
	
	@Override
	public Map<String, Object> sendAuthCodeAndChangePw(String id, String email) {
		
		
		
		// 인증코드 만들기
		String authCode = securityUtil.getAuthCode(6);  // String authCode = securityUtil.generateRandomString(6);
		System.out.println("생성된 임시비밀번호 : " + authCode);
		
		// 이메일 전송을 위한 필수 속성을 Properties 객체로 생성
		Properties properties = new Properties();
		properties.put("mail.smtp.host", "smtp.gmail.com");  // 구글 메일로 보냄(보내는 메일은 구글 메일만 가능)
		properties.put("mail.smtp.port", "587");             // 구글 메일로 보내는 포트 번호
		properties.put("mail.smtp.auth", "true");            // 인증된 메일
		properties.put("mail.smtp.starttls.enable", "true"); // TLS 허용
		
		// 사용자 정보를 javax.mail.Session에 저장
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		
		// 이메일 작성 및 전송
		try {
			
			Message message = new MimeMessage(session);
			
			message.setFrom(new InternetAddress(username, "인증코드관리자"));            // 보내는사람
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));  // 받는사람
			message.setSubject("[Application] 임시비밀번호입니다.");                   // 제목
			message.setContent("임시비밀번호는 <strong>" + authCode + "</strong>입니다.", "text/html; charset=UTF-8");  // 내용
			
			Transport.send(message);  // 이메일 전송
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		// 임시비밀번호를 DB에 들어갈 수 있도록 가공함
		String pw = securityUtil.sha256(authCode);
		
		// 비밀번호 인증번호로 업데이트하기
		//조회시 사용할 userdto
		UserDTO user = UserDTO.builder()
				.id(id)
				.email(email)
				.pw(pw)
				.build();
		
		System.out.println(user);
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("updatePw", userMapper.updateUserInfo(user));
		System.out.println(result);
		
		
		return result;
	}
	
	@Override
	public List<UserDTO> getAllUserList() {
		return userMapper.selectUsersList();
	}
	
	@Override
	public Map<String, Object> removeUsers(HttpServletRequest request) {
		String id = request.getParameter("id");
		System.out.println(id);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		
		UserDTO user = userMapper.selectUserByMap(map);

		RetireUserDTO retireUser = RetireUserDTO.builder()
				.userNo(user.getUserNo())
				.id(user.getId())
				.joinDate(user.getJoinDate())
				.build();
		int result = userMapper.insertRetireUser(retireUser);
		
		result += userMapper.retireUsers(id);
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if(result > 1) {
			resultMap.put("state", "200");
		} else {
			resultMap.put("state", "400");
		}
		return resultMap;
	}
	
	
	
	
}
