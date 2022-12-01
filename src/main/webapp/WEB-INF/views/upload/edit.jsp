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
<link rel="stylesheet" href="${contextPath}/resources/summernote-0.8.18-dist/summernote-lite.min.css">
<script src="${contextPath}/resources/summernote-0.8.18-dist/summernote-lite.min.js"></script>
<script src="${contextPath}/resources/summernote-0.8.18-dist/lang/summernote-ko-KR.min.js"></script>
<script>

	$(function(){
		fn_fileCheck();
		fn_removeAttach();
		
		function fn_fileCheck(){
			
			$('#files').change(function(){
				
				// 첨부 가능한 파일의 최대 크기
				let maxSize = 1024 * 1024 * 10;  // 10MB
				
				// 첨부된 파일 목록
				let files = this.files;
				
				// 첨부된 파일 순회
				for(let i = 0; i < files.length; i++){
					
					// 크기 체크
					if(files[i].size > maxSize){
						alert('10MB 이하의 파일만 첨부할 수 있습니다.');
						$(this).val('');  // 첨부된 파일을 모두 없애줌
						return;
					}
					
				}
				
			});
		}
			
	      function fn_removeAttach(){
	          $(document).on('click', '.btn_attach_remove', function(){
	             if(confirm('해당 파일을 삭제하시겠습니까?')){
	                $.ajax({
	                   type: 'post',
	                   url: '${contextPath}/upload/attach/remove',
	                   data: 'upNo=' + $(this).data('up_no') + '&attachNo=' + $(this).data('attach_no'),
	                   dataType: 'json',
	                   success: function(resData){  // resData = {"isRemove": true}
	                      if(resData.result){
	                         alert('파일이 삭제되었습니다.');
	                         location.reload();
	                      } 
	                   }
	                });
	             }
	          });
	       }
		
	});
</script>
</head>
<body>

	<div>
	
		<h1>수정화면</h1>
	
		<form action="${contextPath}/upload/modify" method="post" enctype="multipart/form-data">
			<input type="hidden" name="upNo" value="${upload.upNo}">
	
			<div>
				<label for="title">제목</label>
				<input type="text" id="title" name="title" value="${upload.upTitle}" required="required">
			</div>
			<div>
				<label for="files">첨부 추가</label>
				<input type="file" id="files" name="files" multiple="multiple">
			</div>
			<div>
				<label for="content">내용</label>
				<input type="text" id="content" name="content" value="${upload.upContent}">
			</div>

			<div>
				<button>수정완료</button>
				<input type="button" value="목록" onclick="location.href='${contextPath}/upload/list'">
			</div>
		</form>
		
		<div>
			<h3>첨부삭제</h3>	
			<c:forEach items="${attachList}" var="attach">
				<div>
					${attach.origin}<input type="button" value="삭제" class="btn_attach_remove" data-up_no="${upload.upNo}" data-attach_no="${attach.attachNo}">
				</div>
			</c:forEach>
		</div>
	
	</div>

</body>
</html>