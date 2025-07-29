// searchHandler.js

document.addEventListener("DOMContentLoaded", () => {
  const searchInput = document.querySelector("#search-input");
  const searchBtn = document.querySelector("#search-btn");
  console.log("JS 실행됨 (searchHandler.js)");

  searchBtn.addEventListener("click", () => {
    const keyword = searchInput.value.trim();
    if (keyword === "") {
      alert("검색어를 입력하세요.");
      return;
    }

    // mapMain으로 이동 (Spring Controller에서 keyword 처리)
    location.href = `/property/search?keyword=${encodeURIComponent(keyword)}`;
  });
});
