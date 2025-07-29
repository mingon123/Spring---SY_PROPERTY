$(function(){
	// MY페이지 프로필사진 등록 및 수정
	// 수정 버튼 이벤트 처리
	$('#photo_btn').click(function(){
		$('#upload').trigger('click');
	})
	// 업로드 이미지 미리보기
	// 파일 업로드 태그의 선택자, 업로드되는 이미지가 보여지는 태그 선택자, 업로드 허용 확장자, 업로드 허용 파일 사이즈
	customViewImage('#upload','.my-photo',['png','jpg','jpeg','gif'],1024*1024);
	$('#upload').on('change', function() {
		if (this.files.length > 0) {
			$('#photo_choice').show();
			$('#photo_btn').hide();
		}
	});
	// 파일 전송 이벤트 처리
	$('#photo_submit').click(function(){
		if($('#upload').val()==''){
			alert('파일을 선택하세요!');
			return;
		}
		const userType = $('#photo_form').data('user-type'); // HTML에서 사용자 타입 받아옴
		const uploadUrl = (userType === 'REALTOR') ? '../user/updateMyPhotoRealtor' : '../user/updateMyPhoto';

		const form_data = new FormData();
		form_data.append('upload', $('#upload')[0].files[0]);

		$.ajax({
			url: uploadUrl,
			type: 'put',
			data: form_data,
			dataType: 'json',
			contentType: false,
			processData: false,
			beforeSend: function (xhr) {
				xhr.setRequestHeader($('meta[name="csrf-header"]').attr('content'),
					$('meta[name="csrf-token"]').attr('content'));
			},
			success: function (param) {
				if (param.result == 'logout') {
					alert('로그인 후 사용하세요');
				} else if (param.result == 'success') {
					alert('프로필 사진이 수정되었습니다.');
					customInitImage('#upload', '.my-photo');
					$('#photo_choice').hide();
					$('#photo_btn').show();
				} else {
					alert('파일 전송 오류 발생');
				}
			},
			error: function () {
				alert('네트워크 오류 발생');
			}
		});
	});
	
	// 취소 버튼 처리
	$('#photo_reset').click(function(){
		// 파일 업로드 태그의 선택자, 업로드되는 이미지가 보여지는 태그 선택자
		customCancelImage('#upload','.my-photo'); // customjs.js에서 만든 메서드
		$('#photo_choice').hide();
		$('#photo_btn').show();
	});
	
});