$(function () {
    // QnA 목록 불러오기
    $.ajax({
        url: '/qna/list-json',
        method: 'GET',
        success: function (response) {
            const list = response.list;
            if (!list || list.length === 0) {
                $('#qna-tbody').html('<tr><td colspan="5">표시할 QnA가 없습니다.</td></tr>');
                return;
            }

            let html = '';
            list.forEach((qna, index) => {
                html += `
                    <tr class="qna-row" data-id="${qna.qna_num}" style="cursor:pointer;">
                        <td>${list.length - index}</td>
                        <td>${qna.category}</td>
                        <td>${qna.title}</td>
                        <td>${qna.reg_date}</td>
                        <td>${qna.answer_status}</td>
                    </tr>
                    <tr class="detail-row" style="display:none;">
                        <td colspan="5">
                            <div class="detail-content">불러오는 중...</div>
                        </td>
                    </tr>
                `;
            });
            $('#qna-tbody').html(html);
        },
        error: function () {
            $('#qna-tbody').html('<tr><td colspan="5">QnA 목록을 불러오지 못했습니다.</td></tr>');
        }
    });

    // 상세보기
    $('#qna-tbody').on('click', '.qna-row', function () {
        const $clickedRow = $(this);
        const qnaId = $clickedRow.data('id');
        const $detailRow = $clickedRow.next('.detail-row');

        $('.detail-row').not($detailRow).hide();

        if ($detailRow.is(':visible')) {
            $detailRow.hide();
            return;
        }

        $detailRow.find('.detail-content').html('불러오는 중...');
        $detailRow.show();

        $.ajax({
            url: '/qna/detail/' + qnaId,
            method: 'GET',
            success: function (response) {
                const qna = response.qna;
                const imageTag = qna.photo_name
                    ? `<img src="/assets/upload/${qna.photo_name}" style="max-width:300px; display:block; margin:10px auto;">`
                    : '';

                let html = `
				<label style="text-align:left; display:block; font-weight: bold; padding: 0 30px;">문의 내용</label>
                    ${imageTag}
                    <p style="text-align: left; padding: 0 30px;">
                        ${qna.content}
                    </p>
                `;

                if (qna.answer) {
                    // 답변이 있으면 답변 내용만 보여주고 수정/삭제 버튼 없음
                    html += `
                        <div style="padding:20px; background:#f9f9f9; margin-top:20px;">
                            <strong style="text-align: left; display: block;">답변 내용</strong>
                            <p style="white-space:pre-wrap; text-align: left;">${qna.answer}</p>
                        </div>
                    `;
                } else {
                    // 답변이 없으면 수정/삭제 버튼 노출
                    html += `
                        <div style="display: flex; justify-content: center; gap: 10px; margin-top: 10px;">
                            <button class="btn-modify" data-id="${qna.qna_num}" style="margin-right: 10px;">수정</button>
                            <button class="btn-delete" data-id="${qna.qna_num}">삭제</button>
                        </div>
                    `;
                }

                $detailRow.find('.detail-content').html(html);
            },
            error: function () {
                $detailRow.find('.detail-content').html('상세 내용을 불러오지 못했습니다.');
            }
        });
    });

    // 수정 이동
    $('#qna-tbody').on('click', '.btn-modify', function (e) {
        e.stopPropagation();
        const qnaId = $(this).data('id');
        window.location.href = `/qna/update?qna_num=${qnaId}`;
    });

    // 삭제
    $('#qna-tbody').on('click', '.btn-delete', function (e) {
        e.stopPropagation();
        const qnaId = $(this).data('id');
        if (confirm('정말 삭제하시겠습니까?')) {
            $.ajax({
                url: '/qna/delete?qna_num=' + qnaId,
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
