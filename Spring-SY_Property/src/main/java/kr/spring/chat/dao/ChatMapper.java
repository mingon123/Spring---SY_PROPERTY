package kr.spring.chat.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.spring.chat.vo.ChatMessageVO;
import kr.spring.chat.vo.ChatRoomVO;


@Mapper
public interface ChatMapper {

   void insertChat(ChatMessageVO msg); // 채팅 저장
   ChatRoomVO selectChatRoom(@Param("user_num") long user_num,
            @Param("realtor_num") long realtor_user_num,
            @Param("property_num") long property_num);
   List<ChatMessageVO> getChatByRoom(Long chatroom_num); // 채팅방 메세지 목록
   void insertChatRoom(ChatRoomVO chatRoom);
   List<ChatRoomVO> getChatroomsByUser(long user_num);
   
   // 전체 채팅방 개수
   public int getChatroomCountUser(long userNum);
   // 페이징된 채팅방 목록
   public List<ChatRoomVO> getChatroomsUserPage(Map<String, Object> map);

   
   int getChatroomCountRealtor(@Param("realtorNum") long realtorNum);
   List<ChatRoomVO> getChatroomsRealtorPage(Map<String, Object> map);

}
