/**
 * 사용자 현재 위치를 기반으로 지도 중심 이동
 * - 처음 사용자 위치 받아 지도 중심 설정
 * - 버튼 누르면 다시 사용자 위치로 부드럽게 이동
 */

let userLocation = null;

function setupUserLocation() {
	if (!navigator.geolocation) {
		console.error("이 브라우저는 위치 기능을 지원하지 않습니다.");
		return;
	}

	navigator.geolocation.getCurrentPosition(function (position) {
		const userLat = position.coords.latitude;
		const userLng = position.coords.longitude;
		userLocation = new naver.maps.LatLng(userLat, userLng);

		map.setCenter(userLocation); //사용자 위치로 중심 이동
	}, function (err) {
		console.error("위치 오류", err);
	});
}

