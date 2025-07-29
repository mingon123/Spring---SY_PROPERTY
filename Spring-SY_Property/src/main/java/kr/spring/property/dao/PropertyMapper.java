package kr.spring.property.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import kr.spring.property.vo.PropertyFavVO;
import kr.spring.property.vo.PropertyVO;


@Mapper
public interface PropertyMapper {
   public List<PropertyVO> getPropertiesList();
   public List<PropertyVO> getPropertiesListByCategory(Map<String, Object> map);
   
   //좋아요
   @Select("SELECT * FROM property_fav WHERE property_id=#{property_id} AND user_num=#{user_num}")
   public PropertyFavVO selectFav(PropertyFavVO fav);
   @Insert("INSERT INTO property_fav (property_id,user_num) VALUES (#{property_id},#{user_num})")
   public void insertFav(PropertyFavVO fav);
   @Delete("DELETE FROM property_fav WHERE property_id=#{property_id} AND user_num=#{user_num}")
   public void deleteFav(PropertyFavVO fav);
   
   List<Long> selectFavPropertyId(@Param("userNum") Long userNum);
   List<PropertyVO> getFavPropertyList(@Param("userNum") Long userNum);

   List<PropertyVO> getFavPropertyListPaging(Map<String, Object> map);
   int getFavCount(Long userNum);

   List<PropertyVO> selectFavPropertyList(@Param("userNum") Long userNum);

   void insertProperty(PropertyVO propertyVO);
   public int countPropertyByDate(String date);
   // PropertyService.java
   List<PropertyVO> getPropertyRealtor(long realtorNum);
   List<PropertyVO> getFilterProperty(Map<String, String> filters);
   PropertyVO selectPropertyID(Long propertyId);
   @Select("SELECT * FROM property WHERE property_id = #{propertyId}")
   PropertyVO getPropertyDetail(Long propertyId);

   // 관리자
   @Select("SELECT COUNT(*) FROM property")
   public int selectRowCount();
   public List<PropertyVO> selectList(Map<String, Object> map);
   public void updateProperty(PropertyVO propertyVO);
   @Delete("DELETE FROM property WHERE property_id=#{propertyId}")
	public void deleteProperty(Long propertyId);
   
   // 페이징 처리용
   int getPropertyCountRealtor(Long realtorNum);
   List<PropertyVO> getPropertyRealtorPage(@Param("realtorNum") Long realtorNum,
                                           @Param("start") int startRow,
                                           @Param("end") int endRow);

    List<PropertyVO> searchByCategory(@Param("category") String category);

   
}
