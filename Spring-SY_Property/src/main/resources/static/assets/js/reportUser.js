$(function () {
    // 신고내역 불러오기
    $.ajax({
        url: '/report/list-json',
        method: 'GET',
        success: function (response) {
            const list = response.list;
            if (!list || list.length === 0) {
                $('#report-tbody').html('<tr><td colspan="5">표시할 신고내역이 없습니다.</td></tr>');
                return;
            }

            let html = '';
            list.forEach((report, index) => {
                html += `
                <tr class="report-row" data-id="${report.report_num}" style="cursor:pointer;">
                    <td>${list.length - index}</td>
                    <td>${report.report_type_name || ''}</td>
                    <td>${report.title || ''}</td>
                    <td>${report.suspect_user_id || ''}</td>
                    <td>${report.report_date || ''}</td>
                </tr>
                <tr class="detail-row" style="display:none;">
                    <td colspan="5">
                        <div class="detail-content">불러오는 중...</div>
                    </td>
                </tr>
                `;
            });
            $('#report-tbody').html(html);
        },
        error: function () {
            $('#report-tbody').html('<tr><td colspan="5">신고 목록을 불러오지 못했습니다.</td></tr>');
        }
    });

    // 상세보기
    $('#report-tbody').on('click', '.report-row', function () {
        const $clickedRow = $(this);
        const reportId = $clickedRow.data('id');
        const $detailRow = $clickedRow.next('.detail-row');

        $('.detail-row').not($detailRow).hide();

        if ($detailRow.is(':visible')) {
            $detailRow.hide();
            return;
        }

        $detailRow.find('.detail-content').html('불러오는 중...');
        $detailRow.show();

        $.ajax({
            url: '/report/detail/' + reportId,
            method: 'GET',
            success: function (response) {
                const report = response.report;
                const imageTag = report.r_photo_name
                    ? `<img src="/assets/upload/${report.r_photo_name}" style="max-width:300px; display:block; margin:10px auto;">`
                    : '';

                let html = `
                    <label style="text-align:left; display:block; font-weight: bold; padding: 0 30px;">신고 내용</label>
                    ${imageTag}
                    <p style="text-align: left; padding: 0 30px;">
                        ${report.content || ''}
                    </p>
					<div style="display: flex; justify-content: center; gap: 10px; margin-top: 10px;">
						<button class="btn-modify" data-id="${report.report_num}" style="margin-right: 10px;">수정</button>
					  	<button class="btn-delete" data-id="${report.report_num}">삭제</button>
					</div>
                `;

                $detailRow.find('.detail-content').html(html);
            },
            error: function () {
                $detailRow.find('.detail-content').html('상세 내용을 불러오지 못했습니다.');
            }
        });
    });

    // 수정 이동
    $('#report-tbody').on('click', '.btn-modify', function (e) {
        e.stopPropagation();
        const reportId = $(this).data('id');
        window.location.href = `/report/update?report_num=${reportId}`;
    });

    // 삭제
    $('#report-tbody').on('click', '.btn-delete', function (e) {
        e.stopPropagation();
        const reportId = $(this).data('id');
        if (confirm('정말 삭제하시겠습니까?')) {
            $.ajax({
                url: '/report/delete?report_num=' + reportId,
                method: 'GET',
                success: function () {
                    alert('삭제되었습니다.');
                    location.reload();
                },
                error: function () {
                    alert('삭제 중 오류가 발생했습니다.');
                }
            });
        }
    });
});