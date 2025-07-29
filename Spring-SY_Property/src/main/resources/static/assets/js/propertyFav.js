/*------------------
 * 좋아요 유무 확인
*------------------*/
function selectFav(property_id){
	//서버와 통신
	$.ajax({
		url:'/property/getFav/' + property_id,
		type:'get',
		dataType:'json',
		success:function(param){
			displayFav(param);
		},
		error:function(){
			alert('네트워크 오류 발생');
		}
	});
}

/*-----------------
 * 좋아요 등록/삭제
*----------------*/
$('.favorite-btn').click(function(){
	//서버와 통신
	$.ajax({
		url:'/property/writeFav',
		type:'post',
		data:JSON.stringify({property_id:$('.property-card').attr('propertyId')}),
		contentType:'application/json;charset=utf-8',
		dataType:'json',
		beforeSend:function(xhr){
			xhr.setRequestHeader(
				$('meta[name="csrf-header"]').attr('content'),
				$('meta[name="csrf-token"]').attr('content'));
		},
		success:function(param){
			if(param.result=='logout'){
				alert('로그인 후 사용하세요');
			}else if(param.result == 'success'){
				displayFav(param);
			}else{
				alert('좋아요 등록/삭제 오류');
			}
		},
		error:function(){
			alert('네트워크 오류 발생');
		}
	});
});
		
/*---------------------
 * 좋아요 표시 공통 함수
 *----------------------*/
function displayFav(param){
	let output;
	if(param.status == 'yesFav'){
		output = '../assets/images/heart02.png';
	}else if(param.status == 'noFav'){
		output = '../assets/images/heart01.png';
	}else{
		output = '../assets/images/heart01.png';
		alert('좋아요 표시 오류 발생');
	}
	return output;
}	










