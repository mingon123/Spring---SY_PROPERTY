/**
 * 지역 중심 좌표 데이터
 * 출처: https://github.com/southkorea/southkorea-maps
 * 라이선스: Apache 2.0
 */

/*-------------------------------*
 *  데이터에서 시/도 단위 좌표 추출
 *-------------------------------*/
fetch('/skorea-provinces-2018-geo.json')
 .then(res => res.json())
  .then(data => {
    const result = {};

    data.features.forEach(feature => {
      const name = feature.properties.name;

      const coordinates = [];
      // 좌표를 재귀적으로 수집하는 함수
      function collectCoords(arr) {
        if (!Array.isArray(arr)) return;

        if (
          typeof arr[0] === 'number' &&
          typeof arr[1] === 'number'
        ) {
          coordinates.push(arr);
        } else {
          arr.forEach(collectCoords);
        }
      }

      collectCoords(feature.geometry.coordinates);

      const lats = coordinates.map(c => c[1]);
      const lngs = coordinates.map(c => c[0]);

      const avgLat = Math.round((lats.reduce((a, b) => a + b, 0) / lats.length) * 10000) / 10000;
      const avgLng = Math.round((lngs.reduce((a, b) => a + b, 0) / lngs.length) * 10000) / 10000;


      result[name] = { lat: avgLat, lng: avgLng };
    });

    console.log('행정구역 중심 좌표:', result);
  });
  
  /*-------------------------------*
   *  데이터에서 시/군/구 단위 좌표 추출
   *-------------------------------*/
  
  fetch('/skorea-municipalities-2018-geo.json')
    .then(res => res.json())
    .then(data => {
      const result = {};

      data.features.forEach(feature => {
        const name = feature.properties.name; // ex: 강남구
        const parent = feature.properties.sido; // ex: 서울특별시

        const fullName = `${parent} ${name}`; // ex: 서울특별시 강남구

        const coordinates = [];

        function collectCoords(arr) {
          if (!Array.isArray(arr)) return;

          if (
            typeof arr[0] === 'number' &&
            typeof arr[1] === 'number'
          ) {
            coordinates.push(arr);
          } else {
            arr.forEach(collectCoords);
          }
        }

        collectCoords(feature.geometry.coordinates);

        const lats = coordinates.map(c => c[1]);
        const lngs = coordinates.map(c => c[0]);

        const avgLat = Math.round((lats.reduce((a, b) => a + b, 0) / lats.length) * 10000) / 10000;
        const avgLng = Math.round((lngs.reduce((a, b) => a + b, 0) / lngs.length) * 10000) / 10000;

        result[name] = { lat: avgLat, lng: avgLng };
      });

      Object.entries(result).forEach(([name, coord]) => {
        console.log(`'${name}': { lat: ${coord.lat}, lng: ${coord.lng} },`);
      });
    });