$(function () {
    const token = $('meta[name="_csrf"]').attr('content');
    const header = $('meta[name="_csrf_header"]').attr('content');

    // QnA 목록 불러오기
    $.ajax({
        url: '/admin/qna/list-json',
        method: 'GET',
        success: function (response) {
            const list = response.list;
            if (!list || list.length === 0) {
                $('#qna-tbody').html('<tr><td colspan="5">표시할 문의내역이 없습니다.</td></tr>');
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
                        <td class="status-cell">${qna.answer_status}</td>
                    </tr>
                    <tr class="detail-row" style="display:none;">
                        <td colspan="5"><div class="detail-content">불러오는 중...</div></td>
                    </tr>
                `;
            });
            $('#qna-tbody').html(html);
        },
        error: function () {
            $('#qna-tbody').html('<tr><td colspan="5">문의내역을 불러오지 못했습니다.</td></tr>');
        }
    });

    // 상세보기
    $('#qna-tbody').on('click', '.qna-row', function () {
        const $row = $(this);
        const qnaId = $row.data('id');
        const $detailRow = $row.next('.detail-row');

        $('.detail-row').not($detailRow).hide();

        if ($detailRow.is(':visible')) {
            $detailRow.hide();
            return;
        }

        $detailRow.find('.detail-content').html('불러오는 중...');
        $detailRow.show();

        $.ajax({
            url: '/admin/qna/detail-json/' + qnaId,
            method: 'GET',
            beforeSend: function (xhr) {
                if (token && header) {
                    xhr.setRequestHeader(header, token);
                }
            },
            success: function (res) {
                const qna = res.qna;
                const imageTag = qna.photo_name
                    ? `<img src="/assets/upload/${qna.photo_name}" style="max-width:300px; display:block; margin:10px auto;">`
                    : '';

                let html = `
				<label style="text-align:left; display:block; font-weight: bold; padding: 0 30px;">문의 내용</label>
                    ${imageTag}
                    <p style="text-align: left; padding: 0 30px;">${qna.content}</p>
                `;

                if (!qna.answer || qna.answer_status === '답변예정') {
                    html += `
                        <div class="answer-box" style="padding:20px; border-top:1px solid #ddd; margin-top:20px;">
                            <label style="text-align:left; display:block; font-weight: bold;">답변 내용</label>
                            <textarea class="admin-answer" data-id="${qna.qna_num}" rows="4" style="width:100%;" placeholder="답변을 입력하세요">${qna.answer || ''}</textarea><br>
                            <div style="text-align:left; margin-top:10px;">
                                <label style="display:block; font-weight: bold; margin-bottom:5px;">답변 상태</label>
                                <select class="answer-status" data-id="${qna.qna_num}"
                                        style="display:block; width:150px; text-align:left; text-align-last:left;">
                                    <option value="답변예정" ${qna.answer_status === '답변예정' ? 'selected' : ''}>답변예정</option>
                                    <option value="답변완료" ${qna.answer_status === '답변완료' ? 'selected' : ''}>답변완료</option>
                                </select>
                            </div>
                            <button class="btn-answer-submit" data-id="${qna.qna_num}" style="margin-top:10px;">답변 등록</button>
                        </div>
                    `;
                } else {
                    html += `
                        <div class="answer-box" style="padding:20px; background:#f9f9f9; margin-top:20px;">
                            <label style="text-align:left; display:block; font-weight: bold;">답변 내용</label>
                            <p class="answer-text" style="white-space:pre-wrap; text-align:left;">${qna.answer}</p>
							<div style="display: flex; justify-content: center; gap: 10px; margin-top: 10px;">
							  <button class="btn-edit-answer" data-id="${qnaId}">답변 수정</button>
							  <button class="btn-delete-answer" data-id="${qnaId}">답변 삭제</button>
							</div>

                        </div>
                    `;
                }

                html += `
                    <div style="text-align: center; margin-top: 15px;">
                        <button class="btn-delete" data-id="${qna.qna_num}">문의 삭제</button>
                    </div>
                `;

                $detailRow.find('.detail-content').html(html);
            },
            error: function () {
                $detailRow.find('.detail-content').html('상세 내용을 불러오지 못했습니다.');
            }
        });
    });

    // 답변 등록 및 수정
    $('#qna-tbody').on('click', '.btn-answer-submit', function (e) {
        e.stopPropagation();
        const qnaId = $(this).data('id');
        const answer = $(`.admin-answer[data-id="${qnaId}"]`).val();
        const answerStatus = $(`.answer-status[data-id="${qnaId}"]`).val();

        $.ajax({
            url: '/admin/qna/answer-json',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                qna_num: qnaId,
                answer: answer,
                answer_status: answerStatus
            }),
            beforeSend: function (xhr) {
                if (token && header) {
                    xhr.setRequestHeader(header, token);
                }
            },
            success: function () {
                alert('답변이 등록되었습니다.');

                const answerHtml = `
                    <div class="answer-box" style="padding:20px; background:#f9f9f9; margin-top:20px;">
                        <label style="text-align:left; display:block; font-weight: bold;">답변 내용</label>
                        <p class="answer-text" style="white-space:pre-wrap; text-align:left;">${answer}</p>
						<div style="display: flex; justify-content: center; gap: 10px; margin-top: 10px;">
						  <button class="btn-edit-answer" data-id="${qnaId}">답변 수정</button>
						  <button class="btn-delete-answer" data-id="${qnaId}">답변 삭제</button>
						</div>

                    </div>
                `;

                const $btn = $(`.btn-answer-submit[data-id="${qnaId}"]`);
                $btn.closest('.answer-box').replaceWith(answerHtml);

                $(`.qna-row[data-id="${qnaId}"]`).find('.status-cell').text(answerStatus);
            },
            error: function () {
                alert('답변 등록 중 오류가 발생했습니다.');
            }
        });
    });

    // 답변 수정 (폼으로 전환)
    $('#qna-tbody').on('click', '.btn-edit-answer', function (e) {
        e.stopPropagation();
        const qnaId = $(this).data('id');
        const $answerBox = $(this).closest('.answer-box');
        const answerText = $answerBox.find('.answer-text').text();

        const editHtml = `
            <label style="text-align:left; display:block; font-weight: bold;">답변 내용</label><br>
            <textarea class="admin-answer" data-id="${qnaId}" rows="4" style="width:100%;">${answerText}</textarea><br><br>
            <div style="text-align:left; margin-top:10px;">
                <label style="display:block; font-weight: bold; margin-bottom:5px;">답변 상태</label>
                <select class="answer-status" data-id="${qnaId}" style="display:block; width:150px; text-align:left; text-align-last:left;">
                    <option value="답변예정">답변예정</option>
                    <option value="답변완료" selected>답변완료</option>
                </select>
            </div>
            <button class="btn-answer-submit" data-id="${qnaId}" style="margin-top:10px;">답변 등록</button>
        `;

        $answerBox.html(editHtml);
    });

    // 답변 삭제
    $('#qna-tbody').on('click', '.btn-delete-answer', function (e) {
        e.stopPropagation();
        const qnaId = $(this).data('id');
        if (confirm('답변을 삭제하시겠습니까?')) {
            $.ajax({
                url: '/admin/qna/answer-delete-json',
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ qna_num: qnaId }),
                beforeSend: function (xhr) {
                    if (token && header) {
                        xhr.setRequestHeader(header, token);
                    }
                },
                success: function () {
                    alert('답변이 삭제되었습니다.');
                    const answerFormHtml = `
                        <div class="answer-box" style="padding:20px; border-top:1px solid #ddd; margin-top:20px;">
                            <label style="text-align:left; display:block; font-weight: bold;">답변 내용</label><br>
                            <textarea class="admin-answer" data-id="${qnaId}" rows="4" style="width:100%;" placeholder="답변을 입력하세요"></textarea><br>
                            <div style="text-align:left; margin-top:10px;">
                                <label style="display:block; font-weight: bold; margin-bottom:5px;">답변 상태</label>
                                <select class="answer-status" data-id="${qnaId}" style="display:block; width:150px; text-align:left; text-align-last:left;">
                                    <option value="답변예정" selected>답변예정</option>
                                    <option value="답변완료">답변완료</option>
                                </select>
                            </div>
                            <button class="btn-answer-submit" data-id="${qnaId}" style="margin-top:10px;">답변 등록</button>
                        </div>
                    `;

                    const $answerBox = $(`.qna-row[data-id="${qnaId}"]`).next('.detail-row').find('.answer-box');
                    $(`.btn-delete-answer[data-id="${qnaId}"]`).closest('.answer-box').replaceWith(answerFormHtml);
                    $(`.qna-row[data-id="${qnaId}"]`).find('.status-cell').text('답변예정');
                },
                error: function () {
                    alert('답변 삭제 중 오류가 발생했습니다.');
                }
            });
        }
    });

    // 문의 삭제
    $('#qna-tbody').on('click', '.btn-delete', function (e) {
        e.stopPropagation();
        const qnaId = $(this).data('id');
        if (confirm('정말 문의를 삭제하시겠습니까?')) {
            window.location.href = `/admin/qna/delete?qna_num=${qnaId}`;
        }
    });
});
