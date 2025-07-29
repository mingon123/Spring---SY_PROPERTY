/* ------------------------- *
 * 지도에 매물 위치 마커 찍기
 * ------------------------- */

let markers = [];
let clusterData = {};
let allProperties = []; // 전체 매물 리스트 캐싱
let lastZoom = null; //중복 이벤트 방지용
let hoverRectangle = null; // 마우스호버 시 표시할 사각형
let currentCategory = null;
let zoomEventRegistered = false;

// 주소 단위로 묶어서 개수 정리
function groupByRegion(propertyList, zoom) {
	clusterData = {};
	let region;
	
	for (let property of propertyList) {
		if(zoom <= 10) region = extractFirstRegionFromAddress(property.address);
		else if(zoom <= 12) region = extractSecondRegionFromAddress(property.address);
		else if(zoom <= 14) region = extractLastRegionFromAddress(property.address);
		else continue;

		if (!region || region === "주소" || region === "미상"){
			//console.warn('잘못된 주소:', property.address, '-> region:', region,property);
			continue;
		} 
		
		if (!clusterData[region]) clusterData[region] = [];
		clusterData[region].push(property);
	}
}

// 특별시/광역시/도 추출
function extractFirstRegionFromAddress(address) {
	const parts = address.trim().split(/\s+/);
	for (let i = 0; i < parts.length; i++) {
		if (/(도|시)$/.test(parts[i])) return parts[i];
	}
	return parts[0];
}

// 시/시 구 추출
function extractSecondRegionFromAddress(address) {
	const parts = address.trim().split(/\s+/);
	for (let i = 1; i < parts.length; i++) {
		if(parts.length > 3){
			if (/(시)$/.test(parts[i]) && /(구)$/.test(parts[i+1])) return parts[i] + ' ' + parts[i + 1];
			else if (/(시)$/.test(parts[i])) return parts[i];
		}else{
			if (/(시)$/.test(parts[i])) return parts[i];
		}
	}
	return parts[1];
}

// 동/읍/면/가 추출
function extractLastRegionFromAddress(address) {
	const parts = address.trim().split(/\s+/);
	for (let i = parts.length - 1; i >= 0; i--) {
		if (/(동|읍|면|가)$/.test(parts[i])) return parts[i];
	}
	return parts[parts.length - 1];
}

// 마커 그리기 시작 함수 (initMap 이후에 실행되어야 함)
function drawMarkersOnMap(category = "원룸투룸") {
	// 이미 데이터 있으면 캐시된 걸로만 처리
	if (allProperties.length > 0 && currentCategory === category) {
		updateMarkersByZoom();
		return;
	}
	currentCategory = category; // 현재 카테고리 기억
	
	$.ajax({
		url: '/property/list/'+ encodeURIComponent(category),
		type: 'get',
		dataType: 'json',
		success: function (propertyList) {
			//console.log("받은 매물 목록:", propertyList);
			if (propertyList && propertyList.length > 0) {
				allProperties = propertyList; //캐싱
				groupByRegion(propertyList, map.getZoom());
				updateMarkersByZoom();
				updateMenuBox(propertyList);

				
				if (!zoomEventRegistered) { //중복이벤트 방지
					naver.maps.Event.addListener(map, 'idle', function () {
						const currentZoom = map.getZoom();
						const visibleProps = getVisibleProperties();
						
						//줌 변경 시
						if (currentZoom !== lastZoom) {
							lastZoom = currentZoom;
							updateMarkersByZoom();
						}else{
							updateMenuBox(visibleProps);
						}
					});
					zoomEventRegistered = true;
				}
			} else {
				//console.log('매물 정보가 없습니다.');
				updateMenuBox([]); // 빈 값 전달
			}
		},
		error: function () {
			alert('네트워크 오류 발생');
			//console.log('리스트 새로 불러오다 에러');
		}
	});
}


// 줌 기준에 따라 마커 갱신
function updateMarkersByZoom() {
	clearAllMarkers();
	const zoom = map.getZoom();
	const visibleProps = getVisibleProperties();
	if (zoom >= 15) {
		showIndividualMarkersWithClustering(visibleProps);
		updateMenuBox(visibleProps); //리스트 업데이트
	} else {
		groupByRegion(allProperties, zoom);
		showClusterMarkers();
		updateMenuBox(visibleProps);
	}
}

// 개별 마커 표시
/*
function showIndividualMarkers() {
	for (let property of allProperties) {
		const marker = new naver.maps.Marker({
			position: new naver.maps.LatLng(property.latitude, property.longitude),
			map: map,
			title: property.title
		});
		markers.push(marker);
	}
}
*/

//마커 클러스터링 - 개별일때 그 주변 매물끼리 클러스터링
let markerCluster = null;
function showIndividualMarkersWithClustering() {
	const newMarkers = [];
	
	for (let property of allProperties) {
		const marker = new naver.maps.Marker({
			position: new naver.maps.LatLng(property.latitude, property.longitude),
			icon: {
				content: `<div class="circle-marker">1</div>`,
				size: new naver.maps.Size(50, 50),
				anchor: new naver.maps.Point(25, 25)
			}
		});
		marker.property = property;
		naver.maps.Event.addListener(marker, 'click', function () {
		    updateMenuBox([marker.property]);
		  });
		
		newMarkers.push(marker);
	}

	markerCluster = new MarkerClustering({
		minClusterSize: 2,
		maxZoom: 18,
		map: map,
		markers: newMarkers,
		disableClickZoom: false,
		gridSize: 150,
		icons: [{
			content: `<div class="circle-marker">N</div>`,
			size: new naver.maps.Size(50, 50),
			anchor: new naver.maps.Point(25, 25),
			averageCenter: false
		}],
		
		stylingFunction: function (marker, count) {
			const el = marker.getElement();
			if (!el) return; 
			
			//el.innerHTML = `<div class="circle-marker">${count}</div>`;
			el.querySelector('.circle-marker').textContent = (`${count}`);
			
			el.onmouseover = function () {
				/* == 클러스터 마커 마우스오버 시 사각형 표시 ==
					1. 마커 중심 좌표 -> 픽셀 좌표로 변환
					2. 좌우 75px씩 떨어진 위치 계산
					3. 다시 위도/경도 좌표로 변환
					4. 그 범위를 기준으로 사각형(Rectangle) 표시
			 	
			    const center = marker.getPosition();
			    const proj = map.getProjection();
			    if (!center || !proj) return;

			    const scale = Math.pow(2, map.getZoom());
				//마커의 지도 좌표(LatLng)를 픽셀 좌표(Point)로 변환
			    const point = proj.fromCoordToPoint(center);
			    const gridSize = 150;
			    const halfGrid = gridSize / 2 / scale;

			    const sw = proj.fromPointToCoord(new naver.maps.Point(point.x - halfGrid, point.y + halfGrid));
			    const ne = proj.fromPointToCoord(new naver.maps.Point(point.x + halfGrid, point.y - halfGrid));
				*/
				
				const clusterBounds = markerCluster._clusters.find(c => c._clusterMarker === marker)?.getBounds();
				if (!clusterBounds) return;

			    if (hoverRectangle) hoverRectangle.setMap(null);
				
			    hoverRectangle = new naver.maps.Rectangle({
			      map: map,
			      bounds: clusterBounds,
			      strokeColor: '#6699FF',
			      strokeOpacity: 0.8,
			      strokeWeight: 2,
			      fillColor: '#6699FF',
			      fillOpacity: 0.2
			    });
			  };

			  el.onmouseout = function () {
			    if (hoverRectangle) {
			      hoverRectangle.setMap(null);
			      hoverRectangle = null;
			    }
			  };
		}
	});
	markers = newMarkers; // 삭제 시 접근할 수 있도록
}


// 클러스터 마커 - 주소별 + 개수
function showClusterMarkers() {
	for (let region in clusterData) {
		const properties = clusterData[region];
		const count = properties.length;
		const rep = properties[0];
		const zoom = map.getZoom();
		const coord = regionCoordinates[region];
		const marker = new naver.maps.Marker({
		    position: coord? 
			new naver.maps.LatLng(coord.lat, coord.lng) : new naver.maps.LatLng(rep.latitude, rep.longitude),
		    map: map,
		    icon: {
		      content: zoom <= 10?
			  `
			  	<div class="custom-marker" onmouseover="this.classList.add('hovered')" onmouseout="this.classList.remove('hovered')">
			  		<div class="marker-region"><b>${region}</b></div>
			  	</div>
			  `
			  :
			  `
			     <div class="custom-marker" onmouseover="this.classList.add('hovered')" onmouseout="this.classList.remove('hovered')">
			       <div class="marker-count">${count}건</div>
			       <div class="marker-region">${region}</div>
			     </div>
			   `,
		      anchor: new naver.maps.Point(10, 10)
		    }
		  });
		  
		  // 마커 클릭 시 해당 지역 중심으로 부드럽게 이동 + 줌 레벨 변경
		  naver.maps.Event.addListener(marker, 'click', function () {
			updateMenuBox(properties); // properties = clusterData[region]
			
		  	if (coord) {
		  		const bounds = new naver.maps.LatLngBounds(
		  			new naver.maps.LatLng(coord.lat - 0.02, coord.lng - 0.02),
		  			new naver.maps.LatLng(coord.lat + 0.02, coord.lng + 0.02)
		  		);
		  		
				map.panToBounds(bounds, {
		  			duration: 200,
		  			easing: 'linear'
		  		});
		  		
				if(9 <= zoom && zoom <= 13){
					let afterZoom = zoom%2==0? zoom+1 : zoom+2;
		  			map.setZoom(afterZoom);
				}		
		  	}
		  });
		  markers.push(marker);
	}
}

// 기존 마커 지우기
function clearAllMarkers() {
	for (let marker of markers) {
		marker.setMap(null);
	}
	markers = [];
	
	if (markerCluster) {
		markerCluster.setMap(null);
		markerCluster = null;
	}
}


/*현재 보이는 화면에 해당하는 매물만 가져오기*/
function getVisibleProperties() {
	const bounds = map.getBounds(); // 현재 화면의 좌표 범위 (남서, 북동)
	const sw = bounds.getSW();
	const ne = bounds.getNE();
	//console.log("현재 보이는 위치:", sw, ne);

  	return allProperties.filter(p => {
    	return (
      		p.latitude >= sw.lat() && p.latitude <= ne.lat() &&
      		p.longitude >= sw.lng() && p.longitude <= ne.lng()
    	);
  	});
}


