/* ------------------------------
 *  거리재기 기능
 * ------------------------------ */
let isMeasuring = false;
let tempPath = [];
let liveLine = null;
let drawnLines = [];
let vertexMarkers = [];
let liveDistanceMarker = null;
let distanceLabels = []; 
let wasEmptyMessageVisible = false;

function setupDistanceMeasuring(map) {
  const measureBtn = document.getElementById('measureBtn');
  const cancelBtn = document.getElementById('cancelMeasureBtn');
  const distanceGuide = document.getElementById('distance-guide');
  const emptyMessage = document.getElementById('empty-message');
  const propertyList = document.querySelector('.property-list');

  if (!measureBtn || !cancelBtn) return;

  // 거리재기 버튼 클릭 시
  measureBtn.addEventListener('click', () => {
    if (isMeasuring) return;
	measureBtn.classList.add('active');
    isMeasuring = true;
    tempPath = [];
    map.setCursor('crosshair');
    distanceGuide.style.display = 'inline-block';
	
	// 매물 리스트 숨기기
	if (propertyList) propertyList.style.display = 'none';
	
	// 빈 매물 메시지 숨기기
	if (emptyMessage) {
		const computedStyle = window.getComputedStyle(emptyMessage);
		wasEmptyMessageVisible = (computedStyle.display !== 'none');
		//console.log("거리재기 시작 전 empty-message display 상태:", computedStyle.display);
		//console.log("wasEmptyMessageVisible 저장된 값:", wasEmptyMessageVisible);
		emptyMessage.style.display = 'none';
	}
    //alert('거리 측정 시작! (ESC 키를 누르면 종료됩니다)');
  });

  // 거리재기 취소 버튼
  cancelBtn.addEventListener('click', () => {
	measureBtn.classList.remove('active');
    if (liveLine) {
      liveLine.setMap(null);
      liveLine = null;
    }
    
	// 새로 그리고 있던 선과 마커만 제거
	if (tempPath.length > 1) {
	    drawnLines.slice(-tempPath.length + 1).forEach(line => line.setMap(null));
	    vertexMarkers.slice(-tempPath.length).forEach(marker => marker.setMap(null));
	    distanceLabels.slice(-tempPath.length + 1).forEach(label => label.setMap(null));

	    drawnLines = drawnLines.slice(0, -tempPath.length + 1);
	    vertexMarkers = vertexMarkers.slice(0, -tempPath.length);
	    distanceLabels = distanceLabels.slice(0, -tempPath.length + 1);
		
		if (liveDistanceMarker) {
		      liveDistanceMarker.setMap(null);
		      liveDistanceMarker = null;
		}
	}
	
    tempPath = [];
    isMeasuring = false;
    map.setCursor(null);
    distanceGuide.style.display = 'none';
	
	if (propertyList) propertyList.style.display = 'block';
	
	// 원래 빈 매물 메시지가 보였던 경우만 다시 보이기
	if (wasEmptyMessageVisible && emptyMessage) {
		emptyMessage.style.display = 'block';
	}
  });

  // 지도 클릭 시
  naver.maps.Event.addListener(map, 'click', (e) => {
    if (!isMeasuring) return;

    if (tempPath.length === 0 && drawnLines.length > 0) {
      drawnLines.forEach(line => line.setMap(null));
	  vertexMarkers.forEach(marker => marker.setMap(null));
	  distanceLabels.forEach(label => label.setMap(null));
	  
      drawnLines = [];
	  vertexMarkers = [];
	  distanceLabels = [];
    }

    tempPath.push(e.coord);
	
	// 첫 클릭이면 시작점 표시
	if (tempPath.length === 1) {
	   addVertexMarker(e.coord, true); // 시작점
	} else {
	   addVertexMarker(e.coord, false); // 꼭짓점
	}

    if (tempPath.length >= 2) {
      const segment = new naver.maps.Polyline({
        map,
        path: tempPath.slice(-2),
        strokeColor: '#376ff9',
        strokeStyle: 'solid',
        strokeOpacity: 1,
        strokeWeight: 4
      });
      drawnLines.push(segment);
    }
	
	//누적 거리 계산 및 표시
	const totalDistance = calculateTotalDistance(tempPath);
	const formatTotalDistance = formatDistance(totalDistance);
	//시작점은 표시 제외
	if (tempPath.length > 1){
		addDistanceLabel(e.coord, formatTotalDistance);
	}
  });

  // 마우스 따라가는 점선
  naver.maps.Event.addListener(map, 'mousemove', (e) => {
    if (!isMeasuring || tempPath.length === 0) return;

	const lastCoord = tempPath[tempPath.length - 1];
	const currentCoord = e.coord;

    if (liveLine) liveLine.setMap(null);
    liveLine = new naver.maps.Polyline({
      map,
      path: [lastCoord,currentCoord],
      strokeColor: '#376ff9',
      strokeStyle: 'shortdash',
      strokeOpacity: 0.7,
      strokeWeight: 3
    });
	
	// 거리 계산
	const distance = calculateTotalDistance([lastCoord,currentCoord]);
	const distanceText = formatDistance(distance);
	
	//도보, 자전거 시간 계산
	const walkingTimeMin = estimateWalkingTime(distance);
	const bikeTimeMin = estimateBikingTime(distance);

	const walkingText = `${Math.round(walkingTimeMin)}분`;
	const bikeText = `${Math.round(bikeTimeMin)}분`;

	// 말풍선 제거 및 다시 생성
	if (liveDistanceMarker) liveDistanceMarker.setMap(null);
	liveDistanceMarker = new naver.maps.Marker({
	    position: currentCoord,
	    map: map,
	    icon: {
	      content: `
		  <div class="total-distance-bubble">
		    <div class="recent-distance">
		      <span class="label">최근 지점부터</span>
		      <span class="value"> ${distanceText}</span>
		    </div>
			<div class="divider"></div>
			<div class="total-distance-row">
			    <span class="label">도보</span>
			    <span class="value">${walkingText}</span>
			  </div>
			  <div class="total-distance-row">
			    <span class="label">자전거</span>
			    <span class="value">${bikeText}</span>
			  </div>
		    <div class="tip">esc키를 눌러 마침</div>
		  </div>
	      `,
	      anchor: new naver.maps.Point(-12, -20)
	    }
	});
  });

  // ESC 누를시 측정 종료 (선은 남기고, 다음 클릭 시 새로 시작)
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && isMeasuring) {
      if (liveLine) {
        liveLine.setMap(null);
        liveLine = null;
      }
	  measureBtn.classList.remove('active');
	  // 총거리 계산
	  const total = calculateTotalDistance(tempPath);
	  const totalText = formatDistance(total);
	  const walkingTime = estimateWalkingTime(total);
	  const bikingTime = estimateBikingTime(total); 

	  const walkingText = `${Math.ceil(walkingTime)}분`;
	  const bikeText = `${Math.ceil(bikingTime)}분`;

	  //내용만 총거리 정보로 업데이트
	  if (liveDistanceMarker) {
		  liveDistanceMarker.setMap(null);
	   }
	   
	  const lastCoord = tempPath[tempPath.length - 1];
	  liveDistanceMarker = new naver.maps.Marker({
		position: lastCoord,
		map: map,
		icon: {
	  	  content: `
	              <div class="total-distance-bubble">
				  <div class="recent-distance">
					 <span class="label">총 거리</span>
					 <span class="value"> ${totalText}</span>
				  </div>
				  <div class="divider"></div>
	              <div class="total-distance-row">
	                <span class="label">도보</span>
	                <span class="value">${walkingText}</span>
	              </div>
	              <div class="total-distance-row">
	                <span class="label">자전거</span>
	                <span class="value">${bikeText}</span>
	              </div>
	            </div>
	          `,
	       anchor: new naver.maps.Point(-12, -10)
		 },
		 zIndex : 9000
	   });
	   isMeasuring = false;
	   map.setCursor(null);
	   tempPath = [];
	   distanceGuide.style.display = 'none';
	   
	   if (propertyList)  propertyList.style.display = 'block';
	   
	   // 원래 빈 매물 메시지가 보였던 경우에만 다시 보이게
	   if (wasEmptyMessageVisible && emptyMessage) {
	   	emptyMessage.style.display = 'block';
	   }
	   
	   const clearBtnWrapper = document.getElementById('custom-clear-line');
	   if (clearBtnWrapper) {
	   	clearBtnWrapper.style.display = 'block';
	   }
    }
  });
  
  //isStart로 시작점과 나머지점 구분
  function addVertexMarker(position, isStart = false) {
    const marker = new naver.maps.Marker({
      position,
      map,
      icon: {
        content: isStart?
		  `
		  <div style="
		        width: 12px;
		        height: 12px;
				background-color: white;
		        border-radius: 50%;
		        display: flex;
		        align-items: center;
		        justify-content: center;
				border: 4px solid #376ff9;
		   ">
		   <div style="
		        width: 8px;
		        height: 8px;
		        background-color: #376ff9;
		        border-radius: 50%;
		   "></div>
		   </div>
		  ` :
		  `	<div style="
		        width: 12px;
		        height: 12px;
		        border-radius: 50%;
		        border: 4px solid #376ff9;
				background-color: white;
		     "></div>`,
        anchor: new naver.maps.Point(8, 8)
      },
      clickable: false,
      zIndex: 100
    });

    vertexMarkers.push(marker);
  }
  
  //두 점 사이 거리 계산
  function calculateTotalDistance(latlngArray) {
    if (latlngArray.length < 2) return 0;
    const projection = naver.maps.EPSG3857;
    let total = 0;

    for (let i = 1; i < latlngArray.length; i++) {
      const coord1 = projection.fromLatLngToCoord(latlngArray[i - 1]);
      const coord2 = projection.fromLatLngToCoord(latlngArray[i]);
      total += projection.getDistance(coord1, coord2);
    }
    return total; // 미터 단위
  }
  
  // 거리표시 라벨
  function addDistanceLabel(latlng, distance) {
     const marker = new naver.maps.Marker({
      map,
      position: latlng,
      icon: {
        content: `
		<div 
		style="
		position: relative;
		padding:4px 6px;
		background: white;
		border:2px solid #333;
		border-radius:4px;
		font-size:12px;
		white-space: nowrap;
		box-shadow: 0 2px 6px rgba(0,0,0,0.2);
		">${distance}
		<div
		style="
		position: absolute;
		bottom: -6px;
		left: 50%;
		transform: translateX(-50%);
		width: 0;
		height: 0;
		border-left: 6px solid transparent;
		border-right: 6px solid transparent;
		border-top: 6px solid #333;
		"
		></div></div>
		`,
        anchor: new naver.maps.Point(28, 45)
      },
      zIndex: 100
    });
	
	distanceLabels.push(marker);
	return marker;
  }
  
  //거리 단위 변환
  function formatDistance(distance) {
    if (distance < 1000) {
      return distance.toFixed(0) + 'm';  
    } else {
      return (distance / 1000).toFixed(2) + 'km'; 
    }
  }
  
  //도보 시간 계산
  //(4.8km/h) 기준
  function estimateWalkingTime(distance) {
    const speed = 80; 
    return distance / speed;
  }

  //자전거 시간 계산
  //(15km/h) 기준
  function estimateBikingTime(distance) {
    const speed = 250; 
    return distance / speed;
  }

}

