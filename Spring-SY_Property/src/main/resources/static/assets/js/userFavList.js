let favPropertyId = [];

function loadFavProperties() {
  $.ajax({
    url: '/property/favList',
    type: 'get',
    dataType: 'json',
    success: function(data) {
      favPropertyId = data;
	  console.log('받은 데이터:', data);
    },
    error: function() {
      console.error('좋아요 목록 불러오기 실패');
    }
  });
}

// 페이지 초기화 시 실행
document.addEventListener("DOMContentLoaded", function(){
  loadFavProperties();
});
