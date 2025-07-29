CREATE TABLE property_fav (
    property_id NUMBER NOT NULL,         -- 매물번호 (FK)
    user_num     NUMBER NOT NULL,         -- 회원번호 (FK)
    alarm_flag   NUMBER(1) DEFAULT 0 NOT NULL, -- 알림 설정 (0: 허용, 1: 거부)

    CONSTRAINT property_fav_pk PRIMARY KEY (property_id, user_num),
    CONSTRAINT property_fav_property_fk FOREIGN KEY (property_id) REFERENCES property(property_id),
    CONSTRAINT property_fav_user_fk FOREIGN KEY (user_num) REFERENCES users(user_num)
);