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
	.blind{
		display : none;
	}
</style>
<script>

	$(function(){
		fn_selectUsersList();
		fn_retire();
	});
	
	function fn_selectUsersList() {
		$.ajax({
			type: 'get',
			url : '${contextPath}/admin/check/list.json',
			dataType: 'json',
			success: function(resData) {
				$('#users_list').empty();
				$.each(resData, function(i, semi){
					$('<tr>')
					.append( $('<td>').text(semi.userNo) )
					.append( $('<td>').text(semi.id) )
					.append( $('<td class="uid blind">').text(semi.id) )
					.append( $('<td>').text(semi.name) )
					.append( $('<td>').text(semi.gender) )
					.append( $('<td>').text(semi.email) )
					.append( $('<td>').text(semi.mobile) )
					.append( $('<td>').text(semi.birthyear) )
					.append( $('<td>').text(semi.birthday) )
					.append( $('<td>').text(semi.point) )
					.append( $('<td>').text(semi.agreeCode) )
					.append( $('<td>').text(semi.joinDate) )
					.append( $('<td>').text(semi.retireDate) )
					.append( $('<td>').append('<input type="button" value="회원탈퇴" class="retire">') )
					.appendTo('#users_list')
					
				});
				
			}
		});
	}

</script>

<script>

	function fn_retire(){
		$(document).on('click', '.retire', function(){
			var id = $(this).parent().parent().children('.uid').text();		
			
			if(confirm('회원을 삭제하시겠습니까?')){
				$("#retire").attr("action","${contextPath}/user/remove").submit();
				$.ajax({
					type : 'post',
					url : '${contextPath}/user/remove',
					data : 'id=' + id,
					dataType : 'json',
					success : function(resData) {
						if(resData.state == 200) {
							alert(id + ' 의 삭제가 완료되었습니다.');
							fn_selectUsersList();
						}  else if(id == admin) {
							alert('관리자는 삭제할 수 없습니다.');
						} else {
							alert('회원 삭제가 실패했습니다.');
						}
					}
				});
			} 
			
		});
		
	}
	
	
</script>
</head>
<body>
	<!--
	<div>
		<form id="user_search" action="${contextPath}/user/search">
			<select id="column" name="column">
				<option value="">:::선택:::</option>
				<option value="USERS">회원</option>
				<option value="RETIRE_USERS">탈퇴</option>
				<option value="SLLEP_USERS">휴면</option>
			</select>
		</form>
	</div>
	-->

	<h1>전체 회원 조회</h1>

	<div>
			<table border="1">
				<thead>
					<tr>
						<td>회원번호</td>
						<td>회원 ID</td>
						<td>회원 이름</td>
						<td>성별</td>
						<td>이메일</td>
						<td>전화번호</td>
						<td>출생년도</td>
						<td>생일</td>
						<td>포인트</td>
						<td>동의여부</td>
						<td>join</td>
						<td>retire</td>
						<td>회원관리</td>
					</tr>
				</thead>
				<tbody id="users_list">

				</tbody>

			</table>
	</div>

	<hr>
	
	<div>
		<a href="${contextPath}/admin/admin">돌아가기</a>
	</div>
</body>
</html>