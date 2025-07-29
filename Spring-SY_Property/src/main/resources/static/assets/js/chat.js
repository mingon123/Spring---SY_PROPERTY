let socket = null;
let chatroom_num = 0;
let user_num = 0;
let user_type = ''; // USER or REALTOR

// 채팅방 정보 설정
function setChatInfo(info) {
  chatroom_num = info.chatroom_num;
  user_num = info.user_num;
  user_type = info.user_type;
}

// 메시지 DOM 추가
function appendMessage(msg, isSelf, senderUserNum) {
  const row = $('<div>')
    .addClass('chat-popup-message-row')
    .addClass(isSelf ? 'self' : 'other');

  if (!isSelf) {
    const profileUrl = user_type === 'REALTOR'
      ? `/user/viewProfile?user_num=${senderUserNum}`
      : `/realtor/viewProfile?user_num=${senderUserNum}`;

    const avatar = $('<div>')
      .addClass('chat-popup-avatar')
      .append(
        $('<img>')
          .attr('src', profileUrl)
          .attr('alt', 'profile')
          .css({
            width: '32px',
            height: '32px',
            borderRadius: '50%',
            marginRight: '8px'
          })
      );

    row.append(avatar);
  }

  const bubble = $('<div>')
    .addClass('chat-popup-message-bubble')
    .text(msg);
	row.append(bubble);

  $('#chat-messages').append(row);
  $('#chat-messages').scrollTop($('#chat-messages')[0].scrollHeight);
}

// 웹소켓 연결
function connectWebSocket() {
  socket = new WebSocket("ws://localhost:8080/ws/chat");

  socket.onmessage = e => {
    const data = JSON.parse(e.data);
    const isSelf = data.user_num == user_num;
    if (!isSelf) {
      appendMessage(data.message, false, data.user_num); // 상대방 번호 전달
    }
  };
}

// 기존 메시지 불러오기
function loadMessages() {
  $.get("/api/chat/messages", { chatroom_num }, function (data) {
    data.forEach(msg => {
      const isSelf = msg.user_num == user_num;
      appendMessage(msg.message, isSelf, msg.user_num);
    });
  });
}

// 메시지 전송
function sendMessage() {
  const msg = $('#message-input').val().trim();
  if (!msg) return;

  appendMessage(msg, true);
  $('#message-input').val('');

  if (socket && socket.readyState === WebSocket.OPEN) {
    socket.send(JSON.stringify({ chatroom_num, user_num, message: msg }));
  }
}

// 채팅 팝업 열기
function openChatPopup(roomNum, userNum, userType) {
  if ($('#chat-popup-overlay').length > 0) return;

  $.get("/chat/popup?chatroom_num=" + roomNum, function (html) {
    $('body').append(html);
    $('#chat-popup-overlay').css('display', 'flex');

    setChatInfo({ chatroom_num: roomNum, user_num: userNum, user_type: userType });

    connectWebSocket();
    loadMessages();

    $('.chat-popup-close').one('click', () => $('#chat-popup-overlay').remove());
    $('#send-btn').off('click').on('click', sendMessage);
    $('#message-input').focus();
  });
}

// 초기 이벤트 바인딩
$(function () {
  // 채팅 입장 버튼
  $(document).on('click', '.chat-btn[data-chatroom-num]', function () {
    const roomNum = $(this).data('chatroom-num');
    const userNum = $(this).data('user-num');
    const userType = $(this).data('user-type'); // USER or REALTOR
    openChatPopup(roomNum, userNum, userType);
  });

  // 엔터 키로 전송
  $(window).on('keydown', e => {
    if (e.key === 'Enter' && !e.shiftKey) {
      if ($('#chat-popup-overlay').is(':visible')) {
        e.preventDefault();
        $('#send-btn').click();
      }
    }
  });
});
