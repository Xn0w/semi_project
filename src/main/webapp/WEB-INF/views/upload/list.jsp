<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<jsp:include page="../layout/header.jsp">
	<jsp:param value="1조 게시판" name="title"/>
</jsp:include>


<script src="${contextPath}/resources/js/jquery-3.6.1.min.js"></script>
<script>

	$(function(){	
		$('#btn_write').click(function(){
				location.href='${contextPath}/upload/write';
		});
	});	
</script>
<style>
table {
  border-collapse: collapse;
  border-spacing: 0;
}


.page-title h3 {
  font-size: 28px;
  color: #333333;
  font-weight: 400;
  text-align: center;
}

#board-search .search-window {
  padding: 15px 0;
  background-color: #f9f7f9;
}
#board-search .search-window .search-wrap {
  position: relative;
/*   padding-right: 124px; */
  margin: 0 auto;
  width: 80%;
  max-width: 564px;
}
#board-search .search-window .search-wrap input {
  height: 40px;
  width: 100%;
  font-size: 14px;
  padding: 7px 14px;
  border: 1px solid #ccc;
}
#board-search .search-window .search-wrap input:focus {
  border-color: #333;
  outline: 0;
  border-width: 1px;
}
#board-search .search-window .search-wrap .btn {
  position: absolute;
  right: 0;
  top: 0;
  bottom: 0;
  width: 108px;
  padding: 0;
  font-size: 16px;
}

.board-table {
  font-size: 13px;
  width: 100%;
  border-top: 1px solid #ccc;
  border-bottom: 1px solid #ccc;
}

.board-table a {
  color: #333;
  display: inline-block;
  line-height: 1.4;
  word-break: break-all;
  vertical-align: middle;
}
.board-table a:hover {
  text-decoration: underline;
}
.board-table th {
  text-align: center;
}

.board-table .th-num {
  width: 100px;
  text-align: center;
}

.board-table .th-date {
  width: 200px;
}

.board-table th, .board-table td {
  padding: 14px 0;
}

.board-table tbody td {
  border-top: 1px solid #e7e7e7;
  text-align: center;
}

.board-table tbody th {
  padding-left: 28px;
  padding-right: 14px;
  border-top: 1px solid #e7e7e7;
  text-align: left;
}

.board-table tbody th p{
  display: none;
}

.btn {
  display: inline-block;
  padding: 0 30px;
  font-size: 15px;
  font-weight: 400;
  background: transparent;
  text-align: center;
  white-space: nowrap;
  vertical-align: middle;
  -ms-touch-action: manipulation;
  touch-action: manipulation;
  cursor: pointer;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;
  border: 1px solid transparent;
  text-transform: uppercase;
  -webkit-border-radius: 0;
  -moz-border-radius: 0;
  border-top-letf-radius :5px;
  border-bottom-left-radius : 5px;
  margin-right:-4px;
   border-top-right-radius :5px;
  border-bottom-right-radius : 5px;
  margin-left:-4px;
  -webkit-transition: all 0.3s;
  -moz-transition: all 0.3s;
  -ms-transition: all 0.3s;
  -o-transition: all 0.3s;
  transition: all 0.3s;
}

.btn-dark {
  background: #555;
  color: #fff;
}

.btn-dark:hover, .btn-dark:focus {
  background: #373737;
  border-color: #373737;
  color: #fff;
}

.btn-dark {
  background: #555;
  color: #fff;
}

.btn-dark:hover, .btn-dark:focus {
  background: #373737;
  border-color: #373737;
  color: #fff;
}

/* reset */

* {
  list-style: none;
  text-decoration: none;
  padding: 0;
  margin: 0;
  box-sizing: border-box;
}
.clearfix:after {
  content: '';
  display: block;
  clear: both;
}
.container {
  width: 1100px;
  margin: 0 auto;
}
.blind {
  position: absolute;
  overflow: hidden;
  clip: rect(0 0 0 0);
  margin: -1px;
  width: 1px;
  height: 1px;
  
.paging {
    padding: 20px 0;
    width: 800px;
}

.paging>ul {
    display: flex;
    justify-content: center;
}

.paging>ul>li {
    padding: 8px;
    background: #efefef;
    border: 1px solid #999;
    font-size: 12px;
    margin-left: 10px;
}

.paging>ul>li:hover {
    cursor: pointer;
    background: #333;
    color: #fff;
}
}
</style>
</head>
<body>

	<div>
		<div>
			<input type="button" id="btn_write" value="게시글 작성" class="btn btn-dark">
		</div>
		<div>
			<c:if test="${loginUser ne null}">
				${loginUser.name}님 환영합니다.  &nbsp;&nbsp; Point : ${loginUser.point}
			</c:if>
		</div>
	</div>
	 
	 <div>
	 	
	 	<table class="board-table">
	 		<thead>
	 			<tr>
	 				<th>번호</th> 
	 				<th>작성자</th>
	 				<th>제목</th> 
	 				<th>첨부개수</th> 
	 				<th>작성일</th> 	
	 				<th>조회수</th>
	 			</tr>
	 		</thead>
	 		<tbody>
	 			<c:if test="${empty uploadList}">
	 				<tr>
	 					<td colspan="6">게시물이 없습니다.</td>
	 				</tr>
	 			</c:if>
	 			
	 			<c:if test="${uploadList ne null}">
		 			<c:forEach items="${uploadList}" var="upload">
		 				<tr>
		 					<td>${upload.upNo}</td>
		 					<td>${upload.name}</td>	
		 					<td><a href="${contextPath}/upload/increase/hit?upNo=${upload.upNo}">${upload.upTitle}</a></td>
		 					<td>${upload.attachCnt}</td>
		 					<td><fmt:formatDate value="${upload.upCreateDate}" pattern="yyyy.M.d"/></td>
		 					<td>${upload.upHit}</td>
		 				</tr>			
		 			</c:forEach>
	 			</c:if>
	 		</tbody>
			<tfoot>
				<tr >
					<td colspan="6">
						${paging}
					</td>
				</tr>
			</tfoot>
	 	</table>
	 </div>

	
</body>
</html>