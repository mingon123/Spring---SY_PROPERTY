$(function(){
	/*--------------------------
	 회원가입 : ID/별명 중복체크
	---------------------------*/
	// 아이디 중복체크
	// 아이디 중복 여부 저장 변수 : 0->아이디 중복 또는 중복체크 미실행
	//							  1->아이디 미중복
	let checkId = 0;
	
	// 아이디 중복 체크
	$('#confirm_id').click(function(){
		if($('#id').val().trim()==''){
			$('#message_id').css('color','red').text('아이디를 입력하세요');
			$('#id').val('').focus();
			return;
		}
		
		// 서버와 통신
		$.ajax({
			url:'confirmId/' + $('#id').val(),
			type:'get',
			dataType:'json',
			success:function(param){
				if(param.result=='idNotFound'){
					checkId = 1;
					$('#message_id').css('color','#000').text('등록 가능 ID');
					checkFormValidity();
				}else if(param.result == 'idDuplicated'){
					checkId = 0;
					$('#message_id').css('color','red').text('중복된 ID');
					$('#id').val('').focus();					
				}else if(param.result == 'notMatchPattern'){
					checkId = 0;
					$('#message_id').css('color','red').text('영문,숫자 4~12자 입력');
					$('#id').val('').focus();
				}else{
					checkId = 0;
					alert('아이디 중복 체크 오류');
				}
			},
			error:function(){
				checkId = 0;
				alert('네트워크 오류 발생');
			}
			
		});
	}); // end of click
	
	// 아이디 안내 메시지 초기화 및 아이디 초기화
	$('#id, input[type="password"]').on('input', function(){
	    // 아이디 값이 바뀌면 중복체크 초기화
	    if(this.id == 'id') {
	        checkId = 0;
	        $('#message_id').text('');
	    }
	    checkFormValidity();
	});
	
	// 비밀번호 입력 시 에러 메시지 지우기
	$('input[name="passwd"], input[name="confirmPasswd"]').on('input', function(){
	    $('#passwdError').text('');
	    $('#passwdNotMatchError').text('');
	});
	
	// submit 이벤트 발생시 아이디 중복 체크 여부 확인
	$('#user_register').submit(function(){
		// 아이디 중복체크 필수
		if(checkId==0){
			$('#message_id').css('color','red').text('아이디 중복체크 필수');
			if($('#id').val().trim()==''){
				$('#id').val('').focus();
			}
			return false;
		}
	}); // end of submit
	
	// 입력 상태체크
	$('input').on('input change', function(){
		checkFormValidity();
	});
	
	// 입력값과 중복체크 상태에 따라 버튼 활성화
	function checkFormValidity() {
	    const idFilled = $('#id').val().trim() !== '';
	    const pw1 = $('input[name="passwd"]').val();
	    const pw2 = $('input[name="confirmPasswd"]').val();
	    const pwFilled = pw1 !== '' && pw2 !== '';
	    if (checkId === 1 && idFilled && pwFilled) {
	        $('.submit-btn').addClass('active').prop('disabled', false);
	    } else {
	        $('.submit-btn').removeClass('active').prop('disabled', true);
	    }
	}
	
	// 초기 실행
	checkFormValidity();

});