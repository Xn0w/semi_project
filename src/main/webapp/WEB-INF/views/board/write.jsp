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
<style>
table {
  border-collapse: collapse;
  border-spacing: 0;
}
section.board {
  padding: 80px 0;
}
.page-title {
  margin-bottom: 60px;
}
.page-title h3 {
  font-size: 28px;
  color: #333333;
  font-weight: 400;
  text-align: center;
}
.container {
  width: 1100px;
  margin: 0 auto;
}
.outer {
	text-align: center;
}

textarea {
	width: 70%;
	height: 100px;
	padding: 10px;
	box-sizing: border-box;
	border: solid 2px #1E90FF;
	border-radius: 5px;
	font-size: 16px;
	resize: none;
}

#title {
	width: 50%;
}

</style>
</head>
<body>
<section class="board">
	<div class="page-title">
		<div class="container">
			
			<h3> 작성 화면 </h3>
		</div>
	</div>	
	
	<div class="outer">
		<form method="post" action="${contextPath}/board/add">
			<div>
				From : ${loginUser.id}
			</div>
			<div>
				<label for="title" style="font-size:12px; color:gray;">제목</label>
				<input type="text" size="30" name="bdTitle" id="title" placeholder="남기실 안부 인사를 적어주세요!" required>
			</div>
			
			<div>
				<hr>
			</div>
			
			<div>
				<label for="content" style="font-size:12px; color:gray;">내용</label>
			</div>
			<div>
				<textarea rows="8" cols="30" id="content" name="bdContent" placeholder="남기실 말을 적어주세요 :)" required>${bd.bdContent}</textarea>
				<!-- <input type="text" name="bdContent" placeholder="남기실 안부 인사를 적어주세요!" required> -->
			</div>

			<div>
				<input type="button" value="목록" onclick="location.href='${contextPath}/board/list'"> 
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<input type="button" id="btn_done" value="작성완료">
				<script>
					$('#btn_done').click(function() {
						$(this).parent().parent().submit();
					})
				</script>
			</div>
			
		</form>
	</div>
</section>
</body>
</html>