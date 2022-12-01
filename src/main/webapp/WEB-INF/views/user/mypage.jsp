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
<script>
   
   $(function(){
   
      fn_showHide();
      fn_mobileCheck();
      fn_emailCheck();
      fn_init();
      fn_pwCheck();
      fn_pwCheckAgain();
      
   });
   
   var mobilePass = false;
   var emailPass = false;
   var pwPass = false;
   var rePwPass = false;
   
/*    function fn_showHide(){
      
      
      
      $('#btn_edit_info').click(function(){
         fn_init();
         $('#modify_info_area').show();
         
      });
      
      $('#btn_edit_info_cancel').click(function(){
         fn_init();
         $('#modify_info_area').hide();
         
      });
      

      
      $('#btn_edit_pw').click(function(){
         fn_init();
         $('#modify_pw_area').show();
         
      });
      
      $('#btn_edit_pw_cancel').click(function(){
         fn_init();
         $('#modify_pw_area').hide();
         
      });
      
   } */
   
   function fn_mobileCheck(){
      
      $('#mobile').keyup(function(){
         
         let mobileValue = $(this).val();
         
         let regMobile = /^010[0-9]{7,8}$/;
         
         if(regMobile.test(mobileValue) == false){
            $('#msg_mobile').text('휴대전화를 확인하세요.');
            mobilePass = false;
         } else {
            $('#msg_mobile').text('');
            mobilePass = true;
         }
         
      }); 
      
   }
   
   function fn_emailCheck(){   
      $('#btn_check').click(function(){
         $.ajax({
            type: 'get',
            url: '${contextPath}/user/checkReduceEmail',
            data: 'email=' + $('#email').val(),
            dataType: 'json',
            success: function(resData){
               if(resData.isUser){
                  $('#msg_email').text('이미 사용중인 이메일입니다.');
                  emailPass = false;
               } else {
                  $('#msg_email').text('사용 가능한 이메일입니다.');
                  emailPass = true;
               }
            }
         });
      });
   }
   
   function fn_init(){
      $('#pw').val('')
      $('#re_pw').val('')
      $('#msg_pw').text('');
      $('#msg_re_pw').text('');
   }
   
   function fn_pwCheck(){
      
      $('#pw').keyup(function(){
         
         let pwValue = $(this).val();
         
         let regPw = /^[0-9a-zA-Z!@#$%^&*]{8,20}$/;
         
         let validatePw = /[0-9]/.test(pwValue) 
                        + /[a-z]/.test(pwValue) 
                        + /[A-Z]/.test(pwValue) 
                        + /[!@#$%^&*]/.test(pwValue); 
         
         if(regPw.test(pwValue) == false || validatePw < 3){
            $('#msg_pw').text('8~20자의 소문자, 대문자, 숫자, 특수문자(!@#$%^&*)를 3개 이상 조합해야 합니다.');
            pwPass = false;
         } else {
            $('#msg_pw').text('사용 가능한 비밀번호입니다.');
            pwPass = true;
         }
                        
      });
      
   }
   
   function fn_pwCheckAgain(){
      
      $('#re_pw').keyup(function(){
         
         let rePwValue = $(this).val();
         
         if(rePwValue != '' && rePwValue != $('#pw').val()){
            $('#msg_re_pw').text('비밀번호를 확인하세요.');
            rePwPass = false;
         } else {
            $('#msg_re_pw').text('');
            rePwPass = true;
         }
         
      });
      
   }
   
	function fn_emailCheck(){
		
		$('#btn_getAuthCode').click(function(){
			
			// 인증코드를 입력할 수 있는 상태로 변경함
			$('#authCode').prop('readonly', false);
			
			// resolve : 성공하면 수행할 function
			// reject  : 실패하면 수행할 function
			new Promise(function(resolve, reject) {
		
				// 정규식 
				let regEmail = /^[a-zA-Z0-9-_]+@[a-zA-Z0-9]+(\.[a-zA-Z]{2,}){1,2}$/;
				
				// 입력한 이메일
				let emailValue = $('#email').val();
				
				// 정규식 검사
				if(regEmail.test(emailValue) == false){
					reject(1);  // catch의 function으로 넘기는 인수 : 1(이메일 형식이 잘못된 경우)
					authCodePass = false;
					return;     // 아래 ajax 코드 진행을 막음
				}
				
				// 이메일 중복 체크
				$.ajax({
					/* 요청 */
					type: 'get',
					url: '${contextPath}/user/checkReduceEmail',
					data: 'email=' + $('#email').val(),
					/* 응답 */
					dataType: 'json',
					success: function(resData){
						// 기존 회원 정보에 등록된 이메일이라면 실패 처리
						if(resData.isUser){
							reject(2);   // catch의 function으로 넘기는 인수 : 2(다른 회원이 사용중인 이메일이라서 등록이 불가능한 경우)
						} else {
							resolve();   // Promise 객체의 then 메소드에 바인딩되는 함수
						}
					}
				});  // ajax
				
			}).then(function(){
				
				// 인증번호 보내는 ajax
				$.ajax({
					/* 요청 */
					type: 'get',
					url: '${contextPath}/user/sendAuthCode',
					data: 'email=' + $('#email').val(),
					/* 응답 */
					dataType: 'json',
					success: function(resData){
						alert('인증코드를 발송했습니다. 이메일을 확인하세요.');
						// 발송한 인증코드와 사용자가 입력한 인증코드 비교
						$('#btn_verifyAuthCode').click(function(){
							if(resData.authCode == $('#authCode').val()){
								alert('인증되었습니다.');
								authCodePass = true;
							} else {
								alert('인증에 실패했습니다.');
								authCodePass = false;
							}
						});
					},
					error: function(jqXHR){
						alert('인증번호 발송이 실패했습니다.');
						authCodePass = false;
					}
				});  // ajax
				
			}).catch(function(code){  // 인수 1 또는 2를 전달받기 위한 파라미터 code 선언

				switch(code){
				case 1:
					$('#msg_email').text('이메일 형식이 올바르지 않습니다.');
					break;
				case 2:
					$('#msg_email').text('이미 사용중인 이메일입니다.');
					break;
				}
			
				authCodePass = false;
			
				// 입력된 이메일에 문제가 있는 경우 인증코드 입력을 막음
				$('#authCode').prop('readonly', true);
				
			});  // new Promise
			
		});  // click
		
	}  // fn_emailCheck
   
	$('#btn_edit_pw_cancel').click(function(){
		location.href='${contextPath}/user/login/form';
	});
	
</script>
</head>
<body>

   <div>
      
      <h1>마이페이지</h1>
      
      <div>
         <input type="button" value="개인정보 변경" id="btn_edit_info">
      </div>
      
      <div id="modify_info_area">
      
         <form id="frm_edit_info" action="${contextPath}/user/modify/info" method="post">
         
            <div>
               아이디 : ${loginUser.id}
            </div>
            
            <div>
               이름 : ${loginUser.name}
            </div>
            
            <div>
               성별 : ${loginUser.gender}
            </div>
         
            <div>
               <label for="mobile">휴대전화*</label>
               <input type="text" name="mobile" id="mobile" placeholder=" - 제외" value="${loginUser.mobile}">
               <span id="msg_mobile"></span>
            </div>
         
            <div>
               생년월일 : ${loginUser.birthyear}${loginUser.birthday}
            </div>
            
            <div>
               <input type="text" onclick="fn_execDaumPostcode()" name="postcode" id="postcode" placeholder="우편번호" readonly value="${loginUser.postcode}">
               <input type="button" onclick="fn_execDaumPostcode()" value="우편번호 찾기"><br>
               <input type="text" name="roadAddress" id="roadAddress" placeholder="도로명주소" readonly value="${loginUser.roadAddress}">
               <input type="text" name="jibunAddress" id="jibunAddress" placeholder="지번주소" readonly value="${loginUser.jibunAddress}"><br>
               <span id="guide" style="color:#999;display:none"></span>
               <input type="text" name="detailAddress" id="detailAddress" placeholder="상세주소" value="${loginUser.detailAddress}">
               <input type="text" name="extraAddress" id="extraAddress" placeholder="참고항목" readonly value="extraAddress">
               <script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
               <script>
				    //본 예제에서는 도로명 주소 표기 방식에 대한 법령에 따라, 내려오는 데이터를 조합하여 올바른 주소를 구성하는 방법을 설명합니다.
				    function fn_execDaumPostcode() {
				        new daum.Postcode({
				            oncomplete: function(data) {
				                // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.
				
				                // 도로명 주소의 노출 규칙에 따라 주소를 표시한다.
				                // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
				                var roadAddr = data.roadAddress; // 도로명 주소 변수
				                var extraRoadAddr = ''; // 참고 항목 변수
				
				                // 법정동명이 있을 경우 추가한다. (법정리는 제외)
				                // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
				                if(data.bname !== '' && /[동|로|가]$/g.test(data.bname)){
				                    extraRoadAddr += data.bname;
				                }
				                // 건물명이 있고, 공동주택일 경우 추가한다.
				                if(data.buildingName !== '' && data.apartment === 'Y'){
				                   extraRoadAddr += (extraRoadAddr !== '' ? ', ' + data.buildingName : data.buildingName);
				                }
				                // 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
				                if(extraRoadAddr !== ''){
				                    extraRoadAddr = ' (' + extraRoadAddr + ')';
				                }
				
				                // 우편번호와 주소 정보를 해당 필드에 넣는다.
				                document.getElementById('postcode').value = data.zonecode;
				                document.getElementById("roadAddress").value = roadAddr;
				                document.getElementById("jibunAddress").value = data.jibunAddress;
				                
				                // 참고항목 문자열이 있을 경우 해당 필드에 넣는다.
				                if(roadAddr !== ''){
				                    document.getElementById("extraAddress").value = extraRoadAddr;
				                } else {
				                    document.getElementById("extraAddress").value = '';
				                }
				
				                var guideTextBox = document.getElementById("guide");
				                // 사용자가 '선택 안함'을 클릭한 경우, 예상 주소라는 표시를 해준다.
				                if(data.autoRoadAddress) {
				                    var expRoadAddr = data.autoRoadAddress + extraRoadAddr;
				                    guideTextBox.innerHTML = '(예상 도로명 주소 : ' + expRoadAddr + ')';
				                    guideTextBox.style.display = 'block';
				
				                } else if(data.autoJibunAddress) {
				                    var expJibunAddr = data.autoJibunAddress;
				                    guideTextBox.innerHTML = '(예상 지번 주소 : ' + expJibunAddr + ')';
				                    guideTextBox.style.display = 'block';
				                } else {
				                    guideTextBox.innerHTML = '';
				                    guideTextBox.style.display = 'none';
				                }
				            }
				        }).open();
				    }
				</script>
            </div>
            
            <div>
               <label for="email">이메일*</label>
               <input type="text" name="email" id="email" value="${loginUser.email}">
               <input type="button" value="중복체크" id="btn_check">
               <span id="msg_email"></span>
            </div>
            
            <div>
               <button>개인정보 변경하기</button>
               <input type="button" value="취소하기" id="btn_edit_info_cancel">
            </div>
            
         </form>
      
      </div>
      
      <div>
         <input type="button" value="비밀번호 변경" id="btn_edit_pw">
      </div>
         
      <div id="modify_pw_area">
         
         <form id="frm_edit_pw" action="${contextPath}/user/modify/pw" method="post">
            
            
            <div>
               <label for="pw">비밀번호</label>
               <input type="password" name="pw" id="pw">
               <span id="msg_pw"></span>
            </div>
            
            <div>
               <label for="re_pw">비밀번호 확인</label>
               <input type="password" id="re_pw">
               <span id="msg_re_pw"></span>
            </div>
            
            <div>
               <button>비밀번호 변경하기</button>
               <input type="button" value="취소하기" id="btn_edit_pw_cancel">
            </div>
            
         </form>
         
      </div>
      
      
      <a href="javascript:fn_abc()">회원탈퇴</a>
      <form id="lnk_retire" action="${contextPath}/user/retire" method="post"></form>
      <script>
         function fn_abc(){
            if(confirm('탈퇴하시겠습니까?')){
               $('#lnk_retire').submit();
            }
         }
      </script>
      
   </div>
   

</body>
</html>