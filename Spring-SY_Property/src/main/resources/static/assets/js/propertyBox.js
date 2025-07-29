/**
 * 클릭에 따른 매물 출력
 */

/* 사이드 바 클릭 */
document.addEventListener("DOMContentLoaded", function(){
	const menuLinks = document.querySelectorAll(".menu-link");

	menuLinks.forEach(link => {
		link.addEventListener("click", function(e) {
			e.preventDefault();
			const selectedCategoryContent = this.textContent.trim();
				let selectedCategory = "";
				if (selectedCategoryContent==='원룸/투룸') {
					selectedCategory = "원룸투룸";
				}else if(selectedCategoryContent==='주택/빌라'){
					selectedCategory = "빌라";
				}else{
					selectedCategory = selectedCategoryContent
				}
				
			//모든 버튼에서 active 제거
			menuLinks.forEach(l => l.classList.remove("active"));
							
			//클릭한 버튼에 active 추가
			this.classList.add("active");
			
			// 기존 캐시 초기화
			allProperties = [];

			// 선택한 카테고리로 매물 다시 불러오기
			drawMarkersOnMap(selectedCategory);

		});
	});
});

// 왼쪽 메뉴박스에 매물 정보 출력하는 함수
function updateMenuBox(properties) {
	const container = document.querySelector('.property-list');
	container.innerHTML = '';

	//매물리스트가 비어있는 경우
	const emptyMessage = document.getElementById('empty-message');
	if (!properties || properties.length === 0) {
		emptyMessage.style.display = 'block';
		return;
	}
	// 조건 맞는 매물이 있을 때는 숨기기
	emptyMessage.style.display = 'none';
	
	properties.forEach(property => {
		//console.log(property,user_num);
		const price = property.price || "";
			let dealType = "";
			let deposit = "";
			let monthly = "";

			if (price.includes("/")) {
				dealType = "월세";
				[deposit, monthly] = price.split("/");
			} else {
				dealType = "전세";
				deposit = price;
			}
		
		const card = document.createElement('div');
		let summary = property.title || '';
		if (summary.length > 18) {
		  summary = summary.substring(0, 18) + '...';
		}
		let complexName = property.complex_name;
		if (complexName && complexName.length > 18) {
			complexName = complexName.substring(0, 18) + '...';
		}
	    card.className = 'property-card';
		card.dataset.propertyId = property.property_id;
		
		let favOutput = property.is_favorite == 1?
			 '/assets/images/heart02.png' : '/assets/images/heart01.png';
		
	    card.innerHTML = `
			<div class="property-img-wrapper">
	    		<img src="${property.imageUrl || '/assets/images/default.png'}" class="property-img" alt="매물 사진">
				<img src="${favOutput}" class="favorite-btn" alt="관심버튼">
			</div>
			<div class="property-info">
	        	<div class="property-price">${dealType} ${deposit}${monthly ? '/' + monthly : ''}</div>
	        	<div class="property-type">${property.room_type} ${complexName? '· ' + complexName:''}</div>
	        	<div class="property-desc">${property.floor_info}, ${property.area_m2}㎡, ${property.maintenance_cost==0.0?'관리비 확인불가' : '관리비 ' + property.maintenance_cost}</div>
				<div class="property-summary">${summary}</div>
			</div>
	    `;
	    container.appendChild(card);
		
		// 추가 구문
		card.addEventListener('click', function () {
		  renderPropertyDetailCard(property);
		  
		  // 좋아요 버튼 클릭 이벤트
		  const favBtn = card.querySelector('.favorite-btn');
		  favBtn.addEventListener('click', function (e) {
			//console.log('하트 누름');
		  	e.stopPropagation();

		  	const propertyId = property.property_id;

		  	$.ajax({
		  		url: '/property/writeFav',
		  		type: 'post',
		  		data: JSON.stringify({ property_id: propertyId }),
		  		contentType: 'application/json;charset=utf-8',
		  		dataType: 'json',
		  		beforeSend: function(xhr) {
		  			xhr.setRequestHeader(
		  				$('meta[name="_csrf_header"]').attr('content'),
		  				$('meta[name="_csrf"]').attr('content')
		  			);
		  		},
		  		success: function(param) {
		  			if (param.result === 'logout') {
		  				alert('로그인 후 사용하세요');
		  			} else if (param.result === 'success') {
		  				const newSrc = (param.status === 'yesFav')? 
						'/assets/images/heart02.png':'/assets/images/heart01.png';
		  				favBtn.src = newSrc;
		  			} else {
		  				alert('좋아요 등록/삭제 오류');
		  			}
		  		},
		  		error: function() {
		  			alert('네트워크 오류 발생');
		  		}
		  	});
		  });
		});

	});
}
