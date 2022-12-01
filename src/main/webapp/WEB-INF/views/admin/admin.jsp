<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<jsp:include page="../layout/header.jsp">
	<jsp:param value="1조 게시판" name="title"/>
</jsp:include>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script src="${contextPath}/resources/js/jquery-3.6.1.min.js"></script>
<script src="${contextPath}/resources/js/agree.js"></script>
<link rel="stylesheet" href="${contextPath}/resources/css/agree.css" />
</head>
<body>

	<h1>관리자 페이지</h1>

	<div>
		<a href="${contextPath}/admin/check">회원 조회</a>
	</div>
	
	<hr>
	
	<div>
		<a href="${contextPath}/admin/check1">자유게시판 조회</a>
	</div>
	
	<hr>
	
	<div>
		<a href="${contextPath}/admin/check2">갤러리게시판 조회</a>
	</div>
	
	<hr>
	
	<div>
		<a href="${contextPath}/admin/check3">업로드게시판 조회</a>
	</div>
	
	<hr>
	
	<div>
		<a href="${contextPath}/admin/check4">다운로드 조회</a>
	</div>

	<hr>
		
	<div>
		<a href="${contextPath}/user/logout">로그아웃</a>
	</div>
</body>
</html>