document.addEventListener("DOMContentLoaded", function () {
  // 카테고리 버튼 클릭 시 이동
  document.querySelectorAll(".category-button").forEach(btn => {
    btn.addEventListener("click", function () {
      const category = this.dataset.type;
      location.href = `/property/category?type=${encodeURIComponent(category)}`;
    });
  });

  const swiper = new Swiper(".swiper-container", {
    loop: true,
    speed: 1000,
    autoplay: {
      delay: 5000,
      disableOnInteraction: false
    },
    pagination: {
      el: ".swiper-pagination",
      clickable: true
    },
    navigation: {
      nextEl: ".swiper-button-next",
      prevEl: ".swiper-button-prev"
    }
  });

  // 배경 이미지 세팅
  document.querySelectorAll(".slide-bg-image").forEach((el) => {
    const bg = el.getAttribute("data-background");
    if (bg) {
      el.style.backgroundImage = `url(${bg})`;
      el.style.backgroundSize = "cover";
      el.style.backgroundPosition = "center";
      el.style.backgroundRepeat = "no-repeat";
    }
  });
});
