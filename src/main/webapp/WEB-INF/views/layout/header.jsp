<%@page import="java.util.Optional"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
   Optional<String> opt = Optional.ofNullable(request.getParameter("title"));
   String title = opt.orElse("welcome");
   pageContext.setAttribute("title", title);   // EL"C:/Users/sjkh9/Downloads/9954AF345C094AEE15/coco/src/main/webapp/resources/semantic.min.css"사용을 위함 (${title})
   pageContext.setAttribute("contextPath", request.getContextPath());
   
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>${title}</title>
<style>
   #board_list{
      display : flex;
       flex-wrap: nowrap;
       align-content: flex-end;
       justify-content: center;
   }

    .page-title {
        margin-bottom: 60px;
    }
    .page-title h1 {
        font-size: 28px;
        color: #333333;
        padding-top : 20px;
        font-weight: 400;
        text-align: center;
      
    }
    ul{
       display : flex;
      justify-content: space-around;
      padding-bottom : 30px;
    
    li{
       display : flex;
    }
    
    /*
    .info{
       text-align: right;
    }
    */
    
    
 .userName_area{
       text-align: right;
    }
   
</style>
<!-- 스크립트의 src에 적는 ${contexPath}/resources는 servlet-context.xml의 resources의 매핑요청임 // 경로가 아닌 매핑요청임 -->
<script src="${contextPath}/resources/js/jquery-3.6.1.min.js"></script>
<script src="${contextPath}/resources/js/moment-with-locales.js"></script>
<script src="${contextPath}/resources/summernote-0.8.18-dist/summernote-lite.js"></script>
<script src="${contextPath}/resources/summernote-0.8.18-dist/lang/summernote-ko-KR.min.js"></script>
<link rel="stylesheet" href="${contextPath}/resources/summernote-0.8.18-dist/summernote-lite.css">
</head>

      <c:if test="${loginUser == null}">
      <div>
         <a href="${contextPath}/user/login/form">Login</a>
      </div>
      </c:if>
      <c:if test="${loginUser ne null}">
      <div class="userName_area" style="text-align: right;  margin-right:20px;" >   
         ${loginUser.name}님 환영합니다.  &nbsp;&nbsp; Point : ${loginUser.point}      
      </div>
      </c:if>
   
   <div class="page-title">
        <div class="container">
            <h1>1조 세미</h1>
        </div>
    </div>
   
         <ul>
            <li><a href="${contextPath}/board/list">자유게시판</a></li>
            <li><a href="${contextPath}/gallery/list">갤러리게시판</a></li>
            <li><a class="board_link" href="${contextPath}/upload/list">업로드게시판</a></li>
         </ul>

   

   

   