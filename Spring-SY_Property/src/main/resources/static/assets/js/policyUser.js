$(function() {
    // 1) 먼저 공지 목록을 Ajax로 불러와 tbody에 동적 생성
    $.ajax({
        url: '/policy/list-json',  // JSON 형식으로 공지 목록을 반환하는 API 필요
        method: 'GET',
        success: function(response) {
            const list = response.list;  // 공지 목록 배열
            if (!list || list.length === 0) {
                $('#policy-tbody').html('<tr><td colspan="3">표시할 공지사항이 없습니다.</td></tr>');
                return;
            }

            let html = '';
            list.forEach((policy, index) => {
                html += `
                    <tr class="policy-row" data-id="${policy.policy_num}" style="cursor:pointer;">
                        <td>${list.length - index}</td>
                        <td>${policy.title}</td>
                        <td>${policy.reg_date}</td>
                    </tr>
                    <tr class="detail-row" style="display:none;">
                        <td colspan="3">
                            <div class="detail-content">불러오는 중...</div>
                        </td>
                    </tr>
                `;
            });
            $('#policy-tbody').html(html);
        },
        error: function() {
            $('#policy-tbody').html('<tr><td colspan="3">공지사항 목록을 불러오지 못했습니다.</td></tr>');
        }
    });

    // 2) 공지사항 행 클릭 이벤트 위임
    $('#policy-tbody').on('click', '.policy-row', function() {
        const $clickedRow = $(this);
        const policyId = $clickedRow.data('id');
        const $detailRow = $clickedRow.next('.detail-row');

        $('.detail-row').not($detailRow).hide();

        if ($detailRow.is(':visible')) {
            $detailRow.hide();
            return;
        }

        $detailRow.find('.detail-content').html('불러오는 중...');
        $detailRow.show();

        $.ajax({
            url: '/policy/detail/' + policyId,
            method: 'GET',
            success: function(response) {
                const policy = response.policy;
                const html = `
				<p style="text-align: left; padding: 0 30px;">
				            ${policy.content}
				</p>
                `;
                $detailRow.find('.detail-content').html(html);
            },
            error: function() {
                $detailRow.find('.detail-content').html('상세 내용을 불러오지 못했습니다.');
            }
        });
    });
});
