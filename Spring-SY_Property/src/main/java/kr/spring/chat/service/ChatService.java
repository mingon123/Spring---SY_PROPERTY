package kr.spring.chat.service;

import java.util.List;

import kr.spring.chat.vo.ChatMessageVO;
import kr.spring.chat.vo.ChatRoomVO;

public interface ChatService {

   void insertChat(ChatMessageVO msg);
   List<ChatMessageVO> getMessagesByRoom(Long chatroom_num);
   ChatRoomVO createRoom(long user_num, long realtor_user_num, long property_num);
   List<ChatRoomVO> getChatroomsByUserNum(long user_num);
   
   public int getChatroomCountUser(long userNum);
   public List<ChatRoomVO> getChatroomsUserPage(long userNum, int start, int end);

   
   int getChatroomCountRealtor(long realtorNum);
   List<ChatRoomVO> getChatroomsRealtorPage(long realtorNum, int start, int end);

   
   ChatRoomVO findChatRoom(long user_num, long realtor_user_num, long property_num);

}
