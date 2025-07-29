package kr.spring.chat.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.chat.dao.ChatMapper;
import kr.spring.chat.vo.ChatMessageVO;
import kr.spring.chat.vo.ChatRoomVO;
import kr.spring.view.DownloadView;

@Service
public class ChatServiceImpl implements ChatService{

    private final DownloadView downloadView;

   @Autowired
   private ChatMapper chatMapper;

    ChatServiceImpl(DownloadView downloadView) {
        this.downloadView = downloadView;
    }
   
   @Override
   public List<ChatMessageVO> getMessagesByRoom(Long chatroom_num){
      return chatMapper.getChatByRoom(chatroom_num);
   }
   @Transactional   
   @Override
   public void insertChat(ChatMessageVO msg) {
      // TODO Auto-generated method stub
      System.out.println("insertChat 호출" + msg);
        try {
              chatMapper.insertChat(msg);
              System.out.println("Chat Insert 성공: " + msg);
          } catch (Exception e) {
              System.out.println("    Chat Insert 실패: " + e.getMessage());
              e.printStackTrace();
          }
      
   }

   @Override
   public ChatRoomVO createRoom(long user_num, long realtor_user_num,long property_num) {
      // 이미 존재하는 채팅방이면 존재하는 채팅방으로 리턴
      ChatRoomVO existingRoom = chatMapper.selectChatRoom(user_num, realtor_user_num, property_num);
      if(existingRoom != null) {
         return existingRoom;
      }
      
      
      // 없으면 새로 생성
      ChatRoomVO newRoom = new ChatRoomVO();
      newRoom.setUser_num(user_num);
      newRoom.setRealtor_user_num(realtor_user_num);
      newRoom.setProperty_num(property_num); 
      newRoom.setChatroom_title("사용자-" + user_num + " / 중개사-" + realtor_user_num + "의 채팅방");
      chatMapper.insertChatRoom(newRoom);  
      return newRoom;
   }

   @Override
   public List<ChatRoomVO> getChatroomsByUserNum(long user_num) {
      return chatMapper.getChatroomsByUser(user_num);
      
   }

   @Override
   public int getChatroomCountUser(long userNum) {
       return chatMapper.getChatroomCountUser(userNum);
   }

   @Override
   public List<ChatRoomVO> getChatroomsUserPage(long userNum, int start, int end) {
       Map<String, Object> map = new HashMap<>();
       map.put("userNum", userNum);
       map.put("start", start);
       map.put("end", end);
       return chatMapper.getChatroomsUserPage(map);
   }

   @Override
   public int getChatroomCountRealtor(long realtorNum) {
       return chatMapper.getChatroomCountRealtor(realtorNum);
   }

   @Override
   public List<ChatRoomVO> getChatroomsRealtorPage(long realtorNum, int start, int end) {
       Map<String, Object> map = new HashMap<>();
       map.put("realtorNum", realtorNum);
       map.put("start", start);
       map.put("end", end);
       return chatMapper.getChatroomsRealtorPage(map);
   }
   
   @Override
   public ChatRoomVO findChatRoom(long user_num, long realtor_user_num, long property_num) {
       return chatMapper.selectChatRoom(user_num, realtor_user_num, property_num);
   }

}
