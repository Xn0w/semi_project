<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>   
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script src="${contextPath}/resources/js/jquery-3.6.1.min.js"></script>
<link rel="stylesheet" href="${contextPath}/resources/summernote-0.8.18-dist/summernote-lite.min.css">
<script src="${contextPath}/resources/summernote-0.8.18-dist/summernote-lite.min.js"></script>
<script src="${contextPath}/resources/summernote-0.8.18-dist/lang/summernote-ko-KR.min.js"></script>
<script>
	$(function(){
		$('#btn_download').click(function(event){
			if(confirm('현재 포인트 ${loginUser.point}  다운로드 하시겠습니까? (30 Point 차감)')){
				location.href='${contextPath}/upload/download?attachNo=${attach.attachNo}';
			} else{
				event.preventDefault();
				return;
			}
		});
	});
</script>
</head>
<body>

<div>
	<div>
		<h1>${upload.upTitle}</h1> 
		▷ 작성일 <fmt:formatDate value="${upload.upCreateDate}" pattern="yyyy.M.d"/>
		&nbsp;&nbsp;&nbsp;
		▷ 수정일 <fmt:formatDate value="${upload.upModifyDate}" pattern="yyyy.M.d"/>
		&nbsp;&nbsp;&nbsp;
		조회수 <fmt:formatNumber value="${upload.upHit}" pattern="#,##0" />
	</div>
	
	<div>
		 <h3>내용</h3> 
	</div>
		
	<div>
		<input type="text" id="content" name="content" value="${upload.upContent}" readonly="readonly">
	</div>
	
	<div>
		<h3>첨부 다운로드</h3>	
		<c:forEach items="${attachList}" var="attach">
			<div>
			<%-- 	<input type="button" id="btn_download" value="${attach.origin}"> --%>
				<a id="btn_download" href="${contextPath}/upload/download?attachNo=${attach.attachNo}&id=${loginUser.id}">${attach.origin}</a>
			</div>
		</c:forEach>
		<br>
		<div>
			<a href="${contextPath}/upload/downloadAll?upNo=${upload.upNo}">모두 다운로드</a>
		</div>
	</div>
	
	<hr>

	<div>
		<form action="frm_btn" id="frm_btn" method="post">
			<input type="hidden" name="upNo" value="${upload.upNo}">
			
			<c:if test="${loginUser.id == upload.id}">
				<input type="button" value="게시글편집" id="btn_edit">
				<input type="button" value="게시글삭제" id="btn_remove">
			</c:if>
			<input type="button" value="게시글목록" id="btn_list">
		</form>
		<script>
			$('#btn_edit').click(function(){
				$('#frm_btn').attr('action', '${contextPath}/upload/edit');
				$('#frm_btn').submit();
			});
		
			$('#btn_remove').click(function(){
				if(confirm('게시글을 삭제하시겠습니까?')){
					$('#frm_btn').attr('action', '${contextPath}/upload/remove');
					$('#frm_btn').submit();	
				}
			});
			
			// 게시글 목록
			$('#btn_list').click(function(event){
				location.href = '${contextPath}/upload/list';
			});
		</script>
	</div>
</div>	

</body>
</html>