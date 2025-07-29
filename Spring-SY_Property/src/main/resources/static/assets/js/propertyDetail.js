

// 왼쪽 메뉴박스에 매물 정보 출력하는 함수 (희동)

function renderPropertyDetailCard(property) {
	  //console.log("상세카드 호출됨", property); 
	  const panel = document.querySelector('.property-detail-panel');
	  const menuBox = document.querySelector('.menu-box');
	  if (!panel || !menuBox) return;
	  
	  panel.style.display = 'block';
	  panel.classList.add('active'); // 상세 카드 보이게
	  
	  //const imageUrl = property.image_url || '/assets/images/default.png';
	  const imageUrl = '/assets/images/default.png';
	  const favOutput = property.is_favorite == 1?
	  			 '/assets/images/heart02.png' : '/assets/images/heart01.png';
				 
	  // 해시태그/옵션 분리
	  const hashtags = (property.hashtags || '')
	      .split(',')
	      .filter(tag => tag.trim())
	      .map(tag => `<span class="tag">#${tag.trim()}</span>`)
	      .join('');
	  const options = (property.options || '')
	      .split(',')
	      .filter(opt => opt.trim())
	      .map(opt => `<span class="tag">${opt.trim()}</span>`)
	      .join('');
		  
	  //월세/전세
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

	  panel.innerHTML = `
	  	  <button id="close-detail-btn" class="detail-close-btn">×</button>
	      <div class="property-detail-card">
			<div class="image-placeholder">
	        	<img src="${imageUrl}" alt="매물 이미지" class="detail-image"/>
			</div>
			
			<div class="price-section">
				<span class="deal-type">${dealType}</span>
			    <span class="price-text">${deposit}${monthly ? '/' + monthly : ''}</span>
			</div>
			
	        <div class="property-detail-title">${property.title || '제목 없음'}</div>

			  <div class="icon-info">
					<div><img src='/assets/images/house_info.png' alt="방종류">${property.room_type || '-'}</div>
					<div><img src='/assets/images/house_area.png' alt="면적">${property.area_m2 || '-'}㎡</div>
					<div><img src='/assets/images/house_floor.png' alt="층수">${property.floor_info || '-'}${property.building_floors==0.0? '' :'/'+property.building_floors}</div>
					<div><img src='/assets/images/maintenance_cost.png' alt="관리비">${property.maintenance_cost==0.0?'관리비 확인불가' : '관리비 ' + property.maintenance_cost}</div>
			  </div>
			  
			  <hr class="detail-hr">
			  <h2>상세정보</h2>
	        <div class="detail-info">
	          <div><strong>주소</strong>: ${property.address || '-'}</div>
	          <div><strong>카테고리</strong>: ${property.category || '-'}</div>
	          <div><strong>방종류</strong>: ${property.room_type || '-'}</div>
	          <div><strong>면적</strong>: ${property.area_m2 || '-'}㎡ (전용 ${property.exclusive_area_m2 || '-'} / 공급 ${property.supply_area_m2 || '-'})</div>
	          <div><strong>방/욕실</strong>: ${property.bed_count || 0} / ${property.bathroom_count || 0}</div>
	          <div><strong>층수</strong>: ${property.floor_info || '-'} / 건물 ${property.building_floors==0.0? '-' :'/'+property.building_floors}</div>
	          <div><strong>주차</strong>: ${property.parking || '-'}</div>
	          <div><strong>관리비</strong>: ${property.maintenance_cost==0.0?'관리비 확인불가' : '관리비 ' + property.maintenance_cost}</div>
	          <div><strong>입주가능일</strong>: ${property.move_in_date || '-'}</div>
	          <div><strong>승인일</strong>: ${property.approval_date || '-'}</div>
	          <div><strong>단지명</strong>: ${property.complex_name || '-'}</div>
	          <div><strong>세대수</strong>: ${property.household_num || '-'}</div>
	          <div><strong>조회수</strong>: ${property.p_hit || 0}회</div>
	          <div><strong>상태</strong>: ${property.keyp_state == 1 ? '거래완료' : '거래중'}</div>
	          <div><strong>등록일</strong>: ${property.p_reg_date || '-'}</div>
	          <div><strong>수정일</strong>: ${property.p_modi_date || '-'}</div>
	        </div>
			
			<hr class="detail-hr">

	        <div class="detail-tags">
	          <div><strong>옵션</strong>: ${options || '-'}</div>
			  <br>
	          <div><strong>해시태그</strong>: ${hashtags || '-'}</div>
	        </div>
		
	        <div class="detail-actions">
	          <button class="chat-btn" data-realtor="${property.realtor_num}" data-property="${property.property_id}">
	            💬 중개사 연결
	          </button>
	        </div>
	      </div>
		   `;
		   
		   document.getElementById("close-detail-btn").addEventListener("click", () => {
		     panel.style.display = "none";
		     panel.classList.remove("active");
		   });
	}
	
	// 모달 방식 채팅방 열기 (아이프레임 방식에서 다시 모달 형식으로 수정)
	document.addEventListener("click", async function(e) {
	  if (e.target.classList.contains("chat-btn")) {
	    const user_num = document.querySelector('#user_num_hidden')?.value;
		const role = document.querySelector('#user_role_hidden')?.value;
	    const realtor_num = e.target.dataset.realtor;
	    const property_num = e.target.dataset.property;
		console.log("user_num:", user_num);
		console.log("realtor_num:", realtor_num);
		console.log("property_num:", property_num);

		if (!user_num) {
		     alert("로그인 후 이용 가능합니다");
		     return;
		   }

		   if (role !== 'ROLE_USER') {
		     if (role === 'ROLE_REALTOR') {
		       // 중개사면 채팅방 목록으로 이동
		        window.location.href = '/chat/list?propertyId=' + property_num;

		     } else {
		       alert("일반 회원만 채팅 기능을 사용할 수 있습니다.");
		     }
		     return;
		   }
	    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
	    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

	    try {
	      const response = await fetch("/chat/chatroom/checkOrCreate", {
	        method: "POST",
	        headers: {
	          "Content-Type": "application/x-www-form-urlencoded",
	          [header]: token
	        },
	        body: new URLSearchParams({
	          user_num,
	          realtor_num,
	          property_num
	        })
	      });

	      if (!response.ok) throw new Error("채팅방 생성 실패");
	      const room = await response.json();

	      openChatPopup(room.chatroom_num, user_num);

	    } catch (err) {
	      alert("로그인 후 이용하세요");
	      console.error(err);
	    }
	  }

	  if (e.target.classList.contains("chat-popup-close")) {
	    const modal = document.getElementById("chat-popup-overlay");
	    modal.style.display = "none";
	    document.getElementById("chat-messages").innerHTML = '';
	  }
	});
