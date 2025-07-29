document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("changePasswordForm");
  const current = form.querySelector("input[name='currentPassword']");
  const newPass = form.querySelector("input[name='newPassword']");
  const confirm = form.querySelector("input[name='confirmPassword']");

  form.addEventListener("submit", function (e) {
    // 비어 있는지 체크
    if (!current.value || !newPass.value || !confirm.value) {
      alert("모든 필드를 입력해주세요.");
      e.preventDefault();
      return;
    }

    // 새 비밀번호 길이 체크
    if (newPass.value.length < 8) {
      alert("새 비밀번호는 최소 8자 이상이어야 합니다.");
      newPass.focus();
      e.preventDefault();
      return;
    }

    // 새 비밀번호 일치 확인
    if (newPass.value !== confirm.value) {
      alert("새 비밀번호가 일치하지 않습니다.");
      confirm.focus();
      e.preventDefault();
      return;
    }

    // 현재 비밀번호와 새 비밀번호 동일 여부
    if (current.value === newPass.value) {
      alert("현재 비밀번호와 새 비밀번호가 동일합니다.");
      newPass.focus();
      e.preventDefault();
      return;
    }
  });
});
