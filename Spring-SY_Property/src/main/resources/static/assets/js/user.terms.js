$(function(){
    // 체크박스 및 전체동의, 버튼 활성화/비활성화 로직
    const $agreeAll = $('#agreeAll');
    const $agreeChecks = $('.agree');
    const $submitBtn = $('#submitBtn');

    // 전체동의 클릭 시 모든 체크박스 ON/OFF
    $agreeAll.on('change', function() {
        $agreeChecks.prop('checked', this.checked);
        toggleSubmit();
    });

    // 개별 체크박스 해제/체크 시 전체동의 상태 업데이트
    $agreeChecks.on('change', function() {
        $agreeAll.prop('checked', $agreeChecks.length === $agreeChecks.filter(':checked').length);
        toggleSubmit();
    });

    // 버튼 활성화/비활성화 함수 (필수 항목 모두 체크됐을 때만 활성화)
    function toggleSubmit() {
        const $requiredChecks = $('.agree[data-required="true"]');
        const allRequiredChecked = $requiredChecks.length === $requiredChecks.filter(':checked').length;
        $submitBtn.prop('disabled', !allRequiredChecked);
        $submitBtn.toggleClass('active', allRequiredChecked);
    }

    // 최초 진입 시 초기 버튼 상태 셋팅
    toggleSubmit();
});