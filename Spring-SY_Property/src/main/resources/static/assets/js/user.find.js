$(function(){
	/*-----------------------
	 비밀번호 찾기
	------------------------*/
	$('#user_find').submit(function(event){
		// 기본 이벤트 제거
		event.preventDefault();
		
		const id = $('#id').val().trim();
		const email = $('#email').val().trim();
		const type = $('input[name="type"]:checked').val();
		
		if (id === '') {
		    alert('아이디를 입력하세요!');
		    $('#id').val('').focus();
		    return;
		}
		
		if(email === ''){
			alert('이메일을 입력하세요!');
			$('#email').val('').focus();
			return;
		}
		
		const form_data = { id: id };
		if (type === 'user') {
		    form_data.userDetail = { email: email };
		} else if (type === 'realtor') {
		    form_data.realtorDetail = { email: email };
		}
		
		// 서버와 통신
		$.ajax({
			url:'getPasswordInfo',
			type:'put',
			data:JSON.stringify(form_data),
			contentType:'application/json;charset=utf-8',
			dataType:'json',
			beforeSend:function(xhr){
				xhr.setRequestHeader(
					$('meta[name="csrf-header"]').attr('content'),
					$('meta[name="csrf-token"]').attr('content'));		
				$('#loading').show(); // 표시		
			},
			complete:function(){
				$('#loading').hide(); // 숨김
			},
			success:function(param){
				if(param.result == 'invalidInfo'){
					alert('아이디 또는 이메일 불일치');
				}else if(param.result == 'suspended'){
					alert('정지회원입니다.');
				}else if(param.result == 'success'){
					alert('이메일로 임시비밀번호가 발송되었습니다.');
					location.href='/user/login';
				}else{
					alert('비밀번호 찾기 오류 발생');
				}
			},
			error:function(xhr){
				try{
					const responseJson = JSON.parse(xhr.responseText);
					alert(responseJson.message);
				}catch(e){
					// JSON이 아닐 경우 대비
					alert('네트워크 오류 발생');
				}
				console.error('Error:',xhr.status,xhr.responseText);
			}
		})
	});
});