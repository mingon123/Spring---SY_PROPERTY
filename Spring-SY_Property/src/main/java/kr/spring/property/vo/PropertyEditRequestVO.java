package kr.spring.property.vo;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PropertyEditRequestVO {

    private Long id;
    private Long propertyId;
    private Long userNum;
    private String requestedFields; // 수정 요청 내용
    private String reason;          // 요청 사유
    private LocalDate requestDate;
    private String status;          // 대기, 승인, 반려 등

    // 매물 정보
    private String address;
    private String roomNumber;
}

