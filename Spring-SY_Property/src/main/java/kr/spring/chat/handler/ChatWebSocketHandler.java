package kr.spring.chat.handler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.spring.chat.service.ChatService;
import kr.spring.chat.vo.ChatMessageVO;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private ChatService chatService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 채팅방별 세션 리스트
    private final Map<Long, List<WebSocketSession>> chatRoomSessions = new ConcurrentHashMap<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        Map<String, Object> map = objectMapper.readValue(payload, Map.class);

        Long chatroomNum = Long.valueOf(map.get("chatroom_num").toString());
        Long userNum = Long.valueOf(map.get("user_num").toString());
        String msg = (String) map.get("message");
        
        // 1. DB 저장
        ChatMessageVO chatMessage = new ChatMessageVO();
        chatMessage.setChatroom_num(chatroomNum);
        chatMessage.setUser_num(userNum);
        chatMessage.setMessage(msg);
       
        chatService.insertChat(chatMessage);
        System.out.println("chatroom_num: " + chatroomNum + ", user_num: " + userNum + ", msg: " + msg);

        // 2. 세션 리스트에 현재 세션 추가
        chatRoomSessions.computeIfAbsent(chatroomNum, k -> new CopyOnWriteArrayList<>());
        List<WebSocketSession> sessionList = chatRoomSessions.get(chatroomNum);

        if (!sessionList.contains(session)) {
            sessionList.add(session);
        }

        // 3. 브로드캐스트 메시지 생성
        String broadcastMessage = objectMapper.writeValueAsString(Map.of(
            "user_num", userNum,
            "message", msg
        ));

        // 4. 해당 채팅방의 모든 세션에 메시지 전송
        for (WebSocketSession s : sessionList) {
            if (s.isOpen() && !s.getId().equals(session.getId())) {
                s.sendMessage(new TextMessage(broadcastMessage));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        // 연결 종료 시 모든 채팅방에서 해당 세션 제거
        chatRoomSessions.values().forEach(list -> list.remove(session));
        System.out.println("연결 종료: " + session.getId());
    }
}
