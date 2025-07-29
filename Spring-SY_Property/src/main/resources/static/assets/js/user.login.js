$(function(){
	/*----------------------
	 회원로그인
	-----------------------*/
	$('#member_login').submit(function(){
		// 메시지 초기화
		$('.error-invalid').slideUp(500);
		$('#error_id').text('아이디를 입력하세요').slideUp(0);
		$('#error_passwd').text('비밀번호를 입력하세요').slideUp(0);
		
		if($('#id').val().trim()=='' && $('#passwd').val().trim()==''){ // 둘다 미입력
			$('#error_id').slideDown(500);
			$('#error_passwd').slideDown(500);
			$('#id').focus();
			return false;
		}
		if($('#id').val().trim()=='' && $('#passwd').val().trim()!=''){ // 비밀번호만 입력
			$('#error_id').slideDown(500);
			$('#id').focus();
			return false;
		}
		if($('#id').val().trim()!='' && $('#passwd').val().trim()==''){ // 아이디만 입력
			$('#error_passwd').slideDown(500);
			$('#passwd').focus();
			return false;
		}
	}); // end of submit
	
	$('#id').keydown(function(){
		$('#error_id, .error-invalid').slideUp(1000);
	});
	
	$('#passwd').keydown(function(){
		$('#error_passwd, .error-invalid').slideUp(1000);
	})
	
});