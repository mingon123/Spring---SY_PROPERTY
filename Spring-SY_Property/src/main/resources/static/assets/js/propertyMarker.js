/* ------------------------- *
 * 지도에 매물 위치 마커 찍기
 * ------------------------- */
// 마커 저장 배열
let markers = [];

function loadPropertiesInView() {
    const bounds = map.getBounds();
    const sw = bounds.getSW();
    const ne = bounds.getNE();

    $.ajax({
        url: '/property/list',
        type: 'get',
        data: {
            swLat: sw.lat(),
            swLng: sw.lng(),
            neLat: ne.lat(),
            neLng: ne.lng()
        },
        dataType: 'json',
        success: function (propertyList) {
            // 기존 마커 제거
            for (let marker of markers) {
                marker.setMap(null);
            }
            markers = [];

            if (propertyList && propertyList.length > 0) {
                for (let property of propertyList) {
                    let lat = property.latitude;
                    let lng = property.longitude;
                    let title = property.title;

                    let marker = new naver.maps.Marker({
                        position: new naver.maps.LatLng(lat, lng),
                        map: map,
                        title: title
                    });

                    let infoWindow = new naver.maps.InfoWindow({
                        content: `<div style="padding:10px;">${title}</div>`
                    });

                    naver.maps.Event.addListener(marker, "click", function () {
                        infoWindow.open(map, marker);
                    });

                    // 배열에 저장
                    markers.push(marker);
                }
            }
        },
        error: function () {
            alert('매물 정보 불러오기 실패');
        }
    });
}

// 지도 생성 후 이벤트 등록
window.onload = function () {
    // 지도가 준비되었을 때 호출
    naver.maps.Event.addListener(map, 'idle', function () {
        loadPropertiesInView();
    });
    loadPropertiesInView();
};
