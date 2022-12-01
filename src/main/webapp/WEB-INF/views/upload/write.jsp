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
	
  	$('#content').summernote({
		  width : 800,
		  height: 400,                 // 에디터 높이
		  lang: "ko-KR",					// 한글 설정
		  placeholder: '최대 2048자까지 쓸 수 있습니다',	//placeholder 설정
		  toolbar: [
            // [groupName, [list of button]]
            ['fontname', ['fontname']],
            ['fontsize', ['fontsize']],
            ['style', ['bold', 'italic', 'underline','strikethrough', 'clear']],
            ['color', ['forecolor','color']],
            ['table', ['table']],
            ['para', ['ul', 'ol', 'paragraph']],
            ['height', ['height']],
            ['insert',['picture','link','video']],
            ['view', ['fullscreen', 'help']]
          ]
	});	  
	
	
	fn_fileCheck();
	
	 $("#files").change(function(){
		    fileList = $("#files")[0].files;
		    fileListTag = '';
		    for(i = 0; i < fileList.length; i++){
		        fileListTag += "<div class='dv_file'>" + fileList[i].name + "</div>";
		    }
		    $('#fileList').append(fileListTag);
		$('.btn_remove').click(function(e){
			var dv_file = $('.dv_file');
			document.getElementById('#fileUpload').select();
			document.selection.clear();
		});    
	});
	 
});	
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

</script>
</head>
<body>

	<div>
		
		<h1>작성화면 ${loginUser.id}</h1>
		
		<form action="${contextPath}/upload/add" method="post" enctype="multipart/form-data">
			
			<div>
				<label for="title">제목</label>
				<input type="text" id="title" name="title" required="required">
			</div>
			
			<hr>
				
			<div>
				<input type="file" id="files" name="files" multiple="multiple">
				<div id="fileList"></div>
			</div>
			
			<div>
				<label for="title">내용</label>
				<textarea id="content" name="content"></textarea>
			</div>
			
			<div>
				<button>작성완료</button>
				<input type="button" value="목록" onclick="location.href='${contextPath}/upload/list'">
			</div>
			
		</form>
	
	</div>
	
</body>
</html>