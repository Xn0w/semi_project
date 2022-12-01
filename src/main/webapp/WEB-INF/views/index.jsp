<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script src="${contextPath}/resources/js/jquery-3.6.1.min.js"></script>
</head>
<body>

	<!-- 로그인이 안 된 상태 -->
	<c:if test="${loginUser == null}">
		<a href="${contextPath}/user/login/form">로그인페이지</a>
		<div>
			<div>
				<a href="${contextPath}/board/list">자유게시판</a>
				<a href="${contextPath}/gallery/list">갤러리게시판</a>
				<a href="${contextPath}/upload/list">업로드게시판</a>
			</div>
		</div>
	</c:if>
	
</body>
</html>