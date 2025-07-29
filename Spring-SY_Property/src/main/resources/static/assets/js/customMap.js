window.onload = function () {
	/* ------------------------------
	 *  커스텀 버튼 줌인 / 줌아웃
	 * ------------------------------ */
    const zoomInBtn = document.getElementById('zoom-in');
    const zoomOutBtn = document.getElementById('zoom-out');

    if (!zoomInBtn || !zoomOutBtn || typeof map === 'undefined') return;

    zoomInBtn.addEventListener('click', function () {
        //map.setZoom(map.getZoom() + 1);
		map.morph(map.getCenter(), map.getZoom() + 1);
    });
    zoomOutBtn.addEventListener('click', function () {
        //map.setZoom(map.getZoom() - 1);
		map.morph(map.getCenter(), map.getZoom() - 1);
    });
	
	/* ------------------------------
     *  커스텀 버튼 맵 타입 변경
     * ------------------------------ */
	//일반
	const normalType = document.getElementById('map-type-normal');
	//위성
	const satelliteType = document.getElementById('map-type-satellite');
	//겹침(위성+도로,지명)
	const hybridType = document.getElementById('map-type-hybrid');
	//지형도
	const terrainType = document.getElementById('map-type-terrain');
	//고른 타입
	const mapSelectType = document.getElementById('map-type-select');
	const mapTypeControls = document.getElementById('custom-mapType-controls');
	
	if (!normalType || !satelliteType || !hybridType || !terrainType || typeof map === 'undefined') return;
	
	mapSelectType.addEventListener('click', function () {
	  const isVisible = mapTypeControls.style.display === 'flex';
	  mapSelectType.classList.add('map-type-select-active');
	  mapTypeControls.style.display = isVisible ? 'none' : 'flex';
	  
	  //현재 선택된 버튼에만 강조 적용
	  document.querySelectorAll('#custom-mapType-controls button')
	      .forEach(btn => btn.classList.remove('map-type-active'));

	  const buttonIdMap = {
	      '일반': 'map-type-normal',
	      '위성': 'map-type-satellite',
	      '혼합': 'map-type-hybrid',
	      '지형도': 'map-type-terrain'
	   };
	   const selectedBtn = document.getElementById(buttonIdMap[currentMapTypeLabel]);
	   if (selectedBtn) {
	      selectedBtn.classList.add('map-type-active');
	   }
	});
	
	let currentMapTypeLabel = '일반'; // 기본값
	// 공통 핸들러 함수
	function changeMapType(typeId, label) {
	  map.setMapTypeId(typeId);
	  mapSelectType.innerHTML = label;
	  mapSelectType.style.display = 'block';
	  mapSelectType.classList.remove('map-type-select-active');
	  mapTypeControls.style.display = 'none'; // 선택 후 숨기기
	  
	  currentMapTypeLabel = label;
	  
	  // 모든 버튼에서 강조 제거
	  document.querySelectorAll('#custom-mapType-controls button')
	     .forEach(btn => btn.classList.remove('map-type-active'));
		 
	  // 선택한 버튼에만 강조 추가
	  const buttonIdMap = {
		     '일반': 'map-type-normal',
		     '위성': 'map-type-satellite',
		     '혼합': 'map-type-hybrid',
		     '지형도': 'map-type-terrain'
	  };
	  const selectedBtn = document.getElementById(buttonIdMap[label]);
      if (selectedBtn) {
		     selectedBtn.classList.add('map-type-active');
	  }
	}
	
	// 타입별 이벤트 연결
	normalType.addEventListener('click', function () {
	  changeMapType(naver.maps.MapTypeId.NORMAL, '일반');
	});
	satelliteType.addEventListener('click', function () {
	  changeMapType(naver.maps.MapTypeId.SATELLITE, '위성');
	});
	hybridType.addEventListener('click', function () {
	  changeMapType(naver.maps.MapTypeId.HYBRID, '혼합');
	});
	terrainType.addEventListener('click', function () {
	  changeMapType(naver.maps.MapTypeId.TERRAIN, '지형도');
	});
	
	
	/* -----------------------------------
	 * 	사용자 현재 위치를 기반으로 지도 중심 이동
	 * - 버튼 누르면 다시 사용자 위치로 부드럽게 이동
	 * ----------------------------------- */
	const locationBtn = document.getElementById("go-to-myLocation");

	locationBtn.addEventListener("click", function () {
		if (userLocation && map) {
			map.panTo(userLocation); // 부드럽게 위치 이동
		} else {
			alert("위치 정보를 가져올 수 없습니다.");
		}
	});
	
	
	/* ------------------------------
	 *  거리재기 선 삭제
	 * ------------------------------ */
	const clearBtn = document.getElementById('clearLinesBtn');
	const clearBtnWrapper = document.getElementById('custom-clear-line');
	
	clearBtn.addEventListener('click', () => {
		// 선, 마커, 라벨, 실시간 말풍선 제거
		drawnLines.forEach(line => line.setMap(null));
	    drawnLines = [];

	    vertexMarkers.forEach(marker => marker.setMap(null));
	    vertexMarkers = [];

	    distanceLabels.forEach(label => label.setMap(null));
	    distanceLabels = [];

	    if (liveDistanceMarker) {
	    	liveDistanceMarker.setMap(null);
	    	liveDistanceMarker = null;
	    }

		if (clearBtnWrapper) {
			clearBtnWrapper.style.display = 'none';
		}
	    //console.log("선 및 거리정보 초기화 완료");
	  });

};
