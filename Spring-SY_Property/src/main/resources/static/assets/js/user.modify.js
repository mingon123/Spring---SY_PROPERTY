function openCertModal() {
  document.getElementById('certModal').style.display = 'block';
}
function closeCertModal() {
  document.getElementById('certModal').style.display = 'none';
}

$(function(){
	/*------------------
	 별명 중복체크
	------------------*/
	let checkNick = 0;
	let nickName;
	
	if($('#nick_name').val()!=''){
		nickName = $('#nick_name').val();
	}
			
	// 별명 입력 텍스트에 따라서 입력창 크기 및 중복 체크 버튼 표시 여부 제어
	$('#nick_name').keyup(function(){
		if($('#nick_name').val()==nickName){
			initNickName();
		}else if($('#nick_name').val().length > 0){
			$('#confirm_nick').show();
		}else{
			initNickName();
		}
	});
	
	// 별명창이 포커스를 잃어버렸을 경우
	$('#nick_name').blur(function(){
		if($('#nick_name').val().length == 0){
			initNickName();
		}
	});
	
	function initNickName(){
		$('#confirm_nick').hide();
		$('#message_nick').text('');
	}
	
	// 별명 중복 안내 메시지 초기화 및 아이디 중복 값 초기화
	$('#nick_name').keydown(function(){
		checkNick = 0;
		$('#message_nick').text('');
	}); // end of keydown
	
	// 별명 중복체크
	$('#confirm_nick').click(function(){
		if($('#nick_name').val().trim()==''){
			$('#message_nick').css('color','red').text('별명을 입력하세요');
			$('#nick_name').val('').focus();
			return;
		}
		
		// 서버와 통신
		$.ajax({
			url:'confirmNickName/' + $('#nick_name').val(),
			type:'get',
			dataType:'json',
			success:function(param){
				if(param.result == 'nickNotFound'){
					checkNick = 1;
					$('#message_nick').css('color','#000').text('등록 가능 별명');
				}else if(param.result == 'nickDuplicated'){
					checkNick = 0;
					$('#message_nick').css('color','red').text('중복된 별명')
					$('#nick_name').val('').focus();
				}else if(param.result == 'notMatchPattern'){
					checkNick = 0;
					$('#message_nick').css('color','red').text('한글,영문,숫자 2~10만 가능')
					$('#nick_name').val('').focus();
				}else{
					checkNick = 0;
					alert('별명 중복체크 오류');
				}
			},
			error:function(){
				checkNick = 0;
				alert('네트워크 오류 발생');
			}
		});
	}); // end of click
	
	// submit 이벤트 발생시 별명 중복체크 여부 확인
	$('#member_modify').submit(function(){
		if($('#nick_name').val()!='' && $('#nick_name').val()!=$('#nick_name').attr('value') && checkNick==0){ // 값은 있는데 중복체크 안함
			$('#message_nick').css('color','red')
							  .html('<div class="form-notice">별명을 변경할 경우 중복체크 필수</div>');
			return false;
		}
	});
	
	$('#admin_modify').submit(function(){
	    if($('#nick_name').val() != '' && $('#nick_name').val() != nickName && checkNick == 0){
	        $('#message_nick').css('color','red')
	                          .html('<div class="form-notice">별명을 변경할 경우 중복체크 필수</div>');
	        return false;
	    }
	});
	
	// 관리자 페이지 회원 삭제 버튼 처리
	$('#delete-btn').click(function(){
		let choice = confirm('삭제하시겠습니까?');
		if(choice){
			let userNum = $(this).data('user-num');
			let redirectUrl = $(this).data('redirect-url');
			
			// 서버와 통신
			$.ajax({
				url: 'admin_delete/' + userNum,
				type:'delete',
				dataType:'json',
				beforeSend:function(xhr){
					xhr.setRequestHeader($('meta[name="csrf-header"]').attr('content'),
										 $('meta[name="csrf-token"]').attr('content'));
				},
				success:function(param){
					if(param.result == 'logout'){
						alert('로그인 후 사용하세요');
					}else if(param.result == 'success'){
						alert('삭제되었습니다.');
						window.location.href = redirectUrl;
					}else{
						alert('회원 삭제 오류 발생');
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
			});
		}
	});
	
});