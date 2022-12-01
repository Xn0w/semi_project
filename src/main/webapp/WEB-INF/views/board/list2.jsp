<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script src="${contextPath}/resources/js/jquery-3.6.1.min.js"></script>
<script>
	

	$(function(){
		
		var loginUser = "${loginUser}";
		
		$('#link_write').click(function(){
			if(loginUser == ''){
				alert('로그인이 필요한 항목입니다. 로그인을 해주세요.');
				location.href='${contextPath}/user/login/form';
			} else{
				location.href='${contextPath}/board/write';
			}
		});
		
		$('#btn_re').click(function(){
			if(loginUser == ''){
				alert('로그인이 필요한 항목입니다. 로그인을 해주세요.');
				location.href='${contextPath}/user/login/form';
			}
		});
		
	});	

	$(function(){
		
		if('${loginUser.name}' == null) {
			$('#link_login').show();
		} else {
			$('#link_login').hide();
			$('#link_mypage').show();
			
		}
		
	})
	
	$(function() {
		
	})
	
</script>
<style>

ul{
	list-style:none;
	float:left;
	display:inline;
}

li {
    float: left;
    margin-right: 20px;
}

li a {
	float:left;
	padding:4px;
	margin-right:3px;
	width:15px;
	color:#000;
	font:bold 12px tahoma;
	border:1px solid #eee;
	text-align:center;
	text-decoration:none;
}

ul li a:hover, ul li a:focus, ul li a:active {
	color:#fff;
	border:1px solid #f40;
	background-color:#f40;
}

a {
	text-decoration-line: none;
	cursor: pointer;
}
	
.blind {
	display: none;
}


table {
  border-collapse: collapse;
  border-spacing: 0;
}
section.board {
  margin: 0 auto;
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
.th {

}

.btn_reply {
	text-align: left;
	border-color: #00bfff;
	border-radius: 3px;
    background-color: #00bfff;
    color: #6e6e6e;

}

textarea {
	width: 100%;
	height: 100px;
	padding: 10px;
	box-sizing: border-box;
	border: solid 2px #1E90FF;
	border-radius: 5px;
	font-size: 14px;
	resize: none;
}

</style>
</head>
<body>
<!-- 원글만 content 있고 댓글 달리는 식. -->
<section class="board">
	<div class="page-title">
		<div class="container">
			<h3> 
				<span>자유 게시판</span> 
			</h3>
		</div>
	</div>	
	
	<br>
	
	<div>
		<%-- <input type="button" onclick="location.href='${contextPath}/board/write'" value="새 글 작성"> --%>
		<a id="link_write">인삿말 남기기</a>
		&nbsp; | &nbsp;
		<a href="${contextPath}/user/login/form">홈으로 가기</a>
	</div>
	
	<hr>
	
	
	
	
	<%-- <div>
		<span id="link_login"><a href="${contextPath}/user/login/form">로그인</a> 후 안부글을 작성할 수 있습니다.</span>
		<span id="link_mypage">안녕하세요! <a href="${contextPath}/user/mypage"> ${loginUser.id}</a>님 :) </span>
	</div> --%>
	<div>
		<c:if test="${loginUser ne null}">
			<a href="${contextPath}/user/mypage">${loginUser.name}</a>님 환영합니다.  &nbsp;&nbsp; Point : ${loginUser.point}점
		</c:if>
	</div>
	
	<br>
	<br>
	<br>
	<br>
	<div><a href="${contextPath}/board/list?page=1" style="text-align:left">1페이지로 가기</a></div>
		<div>
		<table>
			<thead>
				<tr>
					<th style="font-size:16px; color:gray;">No&nbsp;&nbsp;</th>
					<th scope="col">안부 인사</th>
					<th scope="col">작성자</th>
					<th scope="col">날짜</th>
					<th scope="col">ip</th>
					<th scope="col">&nbsp;&nbsp;</th>
				</tr>
			</thead>
	
			<hr>
			
			<tbody>
				<c:forEach var="bd" items="${boardList}" varStatus="vs">
				
					<c:if test="${bd.bdState == 1}">
						<tr>
							<td style="font-size:12px; color:gray;" rowspan="2">${beginNo - vs.index}</td>
							<td id="bdtitle" width="500">
								<c:forEach begin="1" end="${bd.bdDepth}" step="1">
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								</c:forEach>
								<c:if test="${bd.bdDepth > 0}">
									[re]
								</c:if>
								<%--
								${bd.bdTitle}
								<input type="button" value="답글" class="btn_reply">
								 --%>
								${bd.bdTitle}
								<c:if test="${bd.bdDepth == 0}">
									<input type="button" value="re" id="btn_re" class="btn_reply">
								</c:if> 
								<script>
									$('.btn_reply').click(function(){
										$('.reply_tr').addClass('blind');
										$(this).parent().parent().next().next().removeClass('blind');
									});
								</script>
							</td>
							
							<td width="100">${loginUser.id}</td>
							<td width="180"><fmt:formatDate value="${bd.bdCreateDate}" pattern="yyyy. MM. dd HH:mm"/></td>
							<td>${bd.bdIp}</td>
							<td width="150">
								<form method="post" action="${contextPath}/board/rmv">
									<c:if test="${loginUser != null}">
										<c:if test="${loginUser.id == bd.id}">
											<input type="hidden" name="bdNo" value="${bd.bdNo}">
											<a id="lnk_remove${bd.bdNo}" style="cursor: pointer; font-size: 14px;">삭제</a>
										</c:if>
									</c:if>
								</form>
								<script>
									$('#lnk_remove${bd.bdNo}').click(function() {
										if(confirm('삭제할까요?')) {
											// $('.frm_remove').submit();  -> 이렇게 form을 부르면 아예 싹 삭제..?
										    // alert( $(this).parent().data('aaa') );  
										   <%-- 번호를 누른 <a>의 부모 <form>의 data-aaa속성을 클릭하게 한 것. /// data- 속성값을 가지고 오는 함수 :  .data('aaa') --%>
											// alert($(this).prev().val());  // 같은레벨의 이전형제, 즉 input=hidden의 value값=bbs.bbsNo 을 창을 띄워준 것
											$(this).parent().submit();
											alert('삭제되었습니다.');
										} 
									})
								</script>
							</td>
						</tr>
						<tr>
							<td colspan="4" id="content">
							<c:forEach begin="1" end="${bd.bdDepth}" step="1">
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								</c:forEach>
								<c:if test="${bd.bdDepth > 0}">
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								</c:if>
							${bd.bdContent}
							</td>
							<td>
								<form method="post" action="${contextPath}/board/edit">
								<c:if test="${loginUser != null}">
									<c:if test="${loginUser.id == bd.id}">
										<input type="hidden" name="bdNo" value="${bd.bdNo}">
										<a id="lnk_modify${bd.bdNo}" style="cursor: pointer; font-size: 14px;">수정</a>
									</c:if>
								</c:if>
								</form>
								<script>
									$('#lnk_modify${bd.bdNo}').click(function(){
										if(confirm('수정할거에여?')) {
											//location.href = '${contextPath}/board/modify';
											$(this).parent().submit();
										} 
									})
								</script> 
							</td>
						</tr>
						
						<tr class="reply_tr blind">
							<td colspan="5">
								<form method="post" action="${contextPath}/board/reply/add">
									<div>${loginUser.id}</div>
									<div>
										<label for="title" style="font-size:12px; color:gray;">제목</label>
										<input type="text" id="title" name="bdTitle" placeholder="방명록" required>
									</div>
									<div>
										<label for="content" style="font-size:12px; color:gray;">내용</label>
									</div>
									<div>
										<textarea rows="8" cols="30" name="bdContent" id="content" placeholder="남기실 말을 적어주세요 :)"></textarea>
									</div>
									<input type="button" id="btn_done" value="작성완료">
									<input type="hidden" name="bdDepth" value="${bd.bdDepth}">
									<input type="hidden" name="bdGroupNo" value="${bd.bdGroupNo}">
									<input type="hidden" name="bdGroupOrder" value="${bd.bdGroupOrder}">
								</form>
								<c:if test="${loginUser != null}">
								<script>
								
									$('#btn_done').click(function(){
										//alert('잘못된 접근입니다. 로그인해주세요.')
										//location.href='${contextPath}/user/login/form';
										$(this).parent().submit();
									})
								</script>
								</c:if>
							</td>
						</tr>
					</c:if>
					<c:if test="${bd.bdState == 0}">
						<tr>
							<td style="font-size:12px; color:gray;">${beginNo - vs.index}</td>
							<td colspan="4" style="font-size:14px; color:gray;">삭제된 게시글입니다.</td>
						</tr>
					</c:if>
				</c:forEach>
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="6" style="text-align: center">${paging}</td>
				</tr>
			</tfoot>
			
			
		</table>
	</div>
</section>	
</body>
</html>