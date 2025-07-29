--회원관리
create table users(
 user_num number not null,
 id varchar2(50) unique not null,
 nick_name varchar2(30) unique,
 authority varchar2(30) default 'ROLE_USER' not null,
 report_count number(1) default 0 not null,
 constraint users_pk primary key (user_num)
);

create table users_detail(
 user_num number not null,
 name varchar2(30) not null,
 passwd varchar2(12) not null,
 phone varchar2(15) not null,
 email varchar2(50) not null,
 zipcode varchar2(5) not null,
 address1 varchar2(120) not null,
 address2 varchar2(120) not null,
 photo blob,
 photo_name varchar2(400),
 reg_date date default sysdate not null,
 modi_date date,
 constraint users_detail_pk primary key (user_num),
 constraint users_detail_fk foreign key (user_num) references users (user_num)
);

create table realtor_detail(
 realtor_num number not null,
 name varchar2(30) not null,
 passwd varchar2(12) not null,
 phone varchar2(15) not null,
 email varchar2(50) not null,
 zipcode varchar2(5) not null,
 address1 varchar2(120) not null,
 address2 varchar2(120) not null,
 photo blob,
 photo_name varchar2(400),
 reg_date date default sysdate not null,
 modi_date date,
 certificate blob not null, -- 중개사 자격증 사진
 certificate_name varchar2(400) not null,
 valid_date date not null, --중개사 자격 유효기간
 constraint realtor_detail_pk primary key (realtor_num),
 constraint realtor_detail_fk foreign key (realtor_num) references users (user_num)
);
create sequence users_seq;

--자동 로그인(스프링 시큐리티 자동 로그인 기능 사용)
create table persistent_logins(
 series varchar2(64) primary key,
 username varchar2(64) not null,
 token varchar2(64) not null,
 last_used timestamp not null
);





-- 분양, 매물 테이블은 일단 보류

create table property(
	property_num number not null primary key

)
-- 분양 이미지
CREATE TABLE lots_img(
	lots_num number not null,
	l_photo blob,
	l_photo_name varchar2(400),
	 constraint lots_img_pk primary key (lots_num)
)
-- 채팅방 테이블
CREATE TABLE chatroom(
	chatroom_num number not null,
	user_num number not null,
	realtor_num number not null,
	room_date date default sysdate not null,
  constraint chatroom_user_fk foreign key (user_num) references users(user_num)
)
-- 채팅 테이블
CREATE TABLE chat(
	chatroom_num number not null,
	user_num number not null,
	chat_date date default sysdate not null,
	message varchar2(900) not null,
	read_check number(1) not null,
  constraint chat_chatroom_fk foreign key (chatroom_num) references chatroom(chatroom_num),
  constraint chat_user_fk foreign key (user_num) references users(user_num)
)


-- 커뮤니티 테이블
CREATE TABLE board (
    board_num NUMBER PRIMARY KEY,
    b_title VARCHAR2(150) NOT NULL,
    b_content CLOB NOT NULL,
    b_hit NUMBER(9) NOT NULL,
    b_reg_date DATE DEFAULT SYSDATE NOT NULL,
    b_modi_date DATE,
    b_file BLOB,
    b_filename VARCHAR2(400),
    b_ip VARCHAR2(40) NOT NULL,
    user_num NUMBER NOT NULL,
    property_num NUMBER NOT NULL,
    CONSTRAINT board_user_fk FOREIGN KEY (user_num) REFERENCES users(user_num),
    CONSTRAINT board_property_fk FOREIGN KEY (property_num) REFERENCES property(property_num)
);
CREATE SEQUENCE board_seq;

-- 커뮤니티 좋아요 테이블
CREATE TABLE board_fav (
    board_num NUMBER NOT NULL,
    user_num NUMBER NOT NULL,
    CONSTRAINT board_fav_fk_board FOREIGN KEY (board_num) REFERENCES board(board_num),
    CONSTRAINT board_fav_fk_user FOREIGN KEY (user_num) REFERENCES users(user_num)
);

-- 커뮤니티 댓글 테이블
CREATE TABLE board_reply (
    breply_num NUMBER PRIMARY KEY,
    breply_content VARCHAR2(900) NOT NULL,
    breply_date DATE NOT NULL,
    breply_modidate DATE,
    breply_ip VARCHAR2(40) NOT NULL,
    board_num NUMBER NOT NULL,
    user_num NUMBER NOT NULL,
    CONSTRAINT board_reply_fk_board FOREIGN KEY (board_num) REFERENCES board(board_num),
    CONSTRAINT board_reply_fk_user FOREIGN KEY (user_num) REFERENCES users(user_num)
);

CREATE SEQUENCE board_reply_seq;


-- 공지사항 테이블
CREATE TABLE notice (
    notice_num NUMBER PRIMARY KEY,
    title VARCHAR2(100) NOT NULL,
    content CLOB NOT NULL,
    reg_date DATE DEFAULT SYSDATE NOT NULL
);
CREATE SEQUENCE notice_seq;

-- 운영정책 테이블
CREATE TABLE policy (
    policy_num NUMBER PRIMARY KEY,
    title VARCHAR2(100) NOT NULL,
    content CLOB NOT NULL,
    reg_date DATE DEFAULT SYSDATE NOT NULL,
    modi_date DATE
);
CREATE SEQUENCE policy_seq;

-- 팁 테이블
CREATE TABLE tip (
    tip_num NUMBER PRIMARY KEY,
    title VARCHAR2(100) NOT NULL,
    content CLOB NOT NULL,
    reg_date DATE DEFAULT SYSDATE NOT NULL
);
CREATE SEQUENCE tip_seq;

-- 뉴스 테이블
CREATE TABLE news (
    news_num NUMBER PRIMARY KEY,
    title VARCHAR2(100) NOT NULL,
    content CLOB NOT NULL,
    reg_date DATE DEFAULT SYSDATE NOT NULL
);
CREATE SEQUENCE news_seq;
-- FAQ (자주 묻는 질문)
CREATE TABLE faq (
    faq_num NUMBER PRIMARY KEY,
    title VARCHAR2(100) NOT NULL,
    content CLOB NOT NULL,
    reg_date DATE DEFAULT SYSDATE NOT NULL
);
CREATE SEQUENCE faq_seq;

-- 1대1 문의 (QnA)
CREATE TABLE qna (
    qna_num NUMBER PRIMARY KEY,
    title VARCHAR2(100) NOT NULL,
    content CLOB NOT NULL,
    photo BLOB NOT NULL,
    photo_name VARCHAR2(255) NOT NULL,
    reg_date DATE DEFAULT SYSDATE NOT NULL,
    modi_date DATE
);
CREATE SEQUENCE qna_seq;


-- 신고 분류
CREATE TABLE report_type (
    report_type_num NUMBER PRIMARY KEY,
    report_type_name VARCHAR2(60) NOT NULL
);
CREATE SEQUENCE report_type_seq;


-- 신고
CREATE TABLE report (
    report_num NUMBER PRIMARY KEY,
    title VARCHAR2(100) NOT NULL,
    content CLOB NOT NULL,
    report_date DATE DEFAULT SYSDATE NOT NULL,
    reporter_num NUMBER NOT NULL,
    suspect_num NUMBER NOT NULL,
    report_type_num NUMBER NOT NULL,
    r_photo BLOB NOT NULL,
    r_photo_name VARCHAR2(400) NOT NULL,
    CONSTRAINT report_type_fk FOREIGN KEY (report_type_num) REFERENCES report_type(report_type_num)
);
CREATE SEQUENCE report_seq;


CREATE TABLE area (
    area_num NUMBER NOT NULL PRIMARY KEY,
    sido VARCHAR2(50) NOT NULL,
    sigungu VARCHAR2(50) NOT NULL,
    eupmyeondong VARCHAR2(50) NOT NULL,
    detail_address VARCHAR2(200) NOT NULL
);
CREATE SEQUENCE area_seq;


CREATE TABLE alarm (
    alarm_num NUMBER NOT NULL PRIMARY KEY,
    user_num NUMBER NOT NULL,
    message VARCHAR2(500) NOT NULL,
    is_read NUMBER(1) DEFAULT 0 NOT NULL,
    created_date DATE DEFAULT SYSDATE NOT NULL,
    alarm_type VARCHAR2(20) DEFAULT 'general' NOT NULL,
    product_num NUMBER,
    CONSTRAINT alarm_user_fk FOREIGN KEY (user_num) REFERENCES users(user_num)
);
CREATE SEQUENCE alarm_seq;


CREATE TABLE PROPERTY (
    ROOM_ID            VARCHAR2(64) PRIMARY KEY,         -- 매물 ID (고유값)
    TITLE              VARCHAR2(200) NOT NULL,           -- 매물 제목
    ADDRESS            VARCHAR2(300) NOT NULL,           -- 주소
    CATEGORY           VARCHAR2(20),                     -- 매물 유형 (빌라, 오피스텔 등)
    ROOM_TYPE          VARCHAR2(50),                     -- 방 종류 (원룸, 투룸 등)
    PRICE              VARCHAR2(50),                     -- 가격 정보
    AREA_M2            NUMBER,                           -- 면적(m²)
    BED_COUNT          NUMBER,                           -- 방 수
    BATHROOM_COUNT     NUMBER,                           -- 화장실 수
    FLOOR_INFO         VARCHAR2(50),                     -- 층 정보
    BUILDING_FLOORS    VARCHAR2(50),                     -- 건물 층수
    PARKING            VARCHAR2(50),                     -- 주차 가능 여부
    MAINTENANCE_COST   VARCHAR2(50),                     -- 관리비
    MOVE_IN_DATE       VARCHAR2(100),                    -- 입주 가능일
    APPROVAL_DATE      VARCHAR2(100),                    -- 건축 승인일
    COMPLEX_NAME       VARCHAR2(200),                    -- 단지명
    HOUSEHOLD_NUM      NUMBER,                           -- 총 세대수
    EXCLUSIVE_AREA_M2  NUMBER,                           -- 전용면적(m²)
    SUPPLY_AREA_M2     NUMBER,                           -- 공급면적(m²)
    HASHTAGS           VARCHAR2(500),                    -- 해시태그
    OPTIONS            CLOB,                             -- 옵션 목록
    LATITUDE           NUMBER(10, 6),                    -- 위도
    LONGITUDE          NUMBER(10, 6),                    -- 경도

    -- 🔽 참고 항목 추가
    KEYP_STATE         NUMBER(1) DEFAULT 0 NOT NULL,     -- 매물 상태 (0 거래중, 1 거래완료)
    P_HIT              NUMBER DEFAULT 0 NOT NULL,        -- 매물 조회수
    P_REG_DATE         DATE DEFAULT SYSDATE NOT NULL,    -- 매물 등록일
    P_MODI_DATE        DATE                              -- 매물 수정일
);





CREATE SEQUENCE property_request_seq
  START WITH 1
  INCREMENT BY 1
  NOCACHE;


ALTER TABLE property
ADD CONSTRAINT fk_property_user
FOREIGN KEY (realtor_num)
REFERENCES users(user_num);


ALTER TABLE property
ADD realtor_num NUMBER;


 -- 매물 수정일

CREATE TABLE PROPERTY_REQUEST (
  REQUEST_ID          NUMBER PRIMARY KEY,
  REALTOR_NUM         NUMBER NOT NULL,
  TITLE               VARCHAR2(200) NOT NULL,
  ADDRESS             VARCHAR2(300) NOT NULL,
  ROOM_TYPE           VARCHAR2(100) NOT NULL,
  PRICE               VARCHAR2(50) NOT NULL,
  AREA_M2             NUMBER(7,2) NOT NULL,
  BED_COUNT           NUMBER(2) NOT NULL,
  BATHROOM_COUNT      NUMBER(2) NOT NULL,
  FLOOR_INFO          VARCHAR2(50) NOT NULL,
  BUILDING_FLOORS     VARCHAR2(50),
  PARKING             VARCHAR2(20),
  MAINTENANCE_COST    VARCHAR2(50),
  MOVE_IN_DATE        VARCHAR2(50),
  APPROVAL_DATE       VARCHAR2(50),
  COMPLEX_NAME        VARCHAR2(100),
  HOUSEHOLD_NUM       NUMBER,
  EXCLUSIVE_AREA_M2   NUMBER(7,2),
  SUPPLY_AREA_M2      NUMBER(7,2),
  HASHTAGS            VARCHAR2(300),
  OPTIONS             VARCHAR2(500),
  LATITUDE            NUMBER(15,10),
  LONGITUDE           NUMBER(15,10),
  CATEGORY            VARCHAR2(50),
  STATUS              VARCHAR2(20) DEFAULT '대기',
  REG_DATE            DATE DEFAULT SYSDATE
);



CREATE TABLE property_edit_request (
    id              NUMBER PRIMARY KEY,
    property_id     NUMBER NOT NULL,  -- ✅ 컬럼명 통일
    user_num        NUMBER NOT NULL,
    requested_fields VARCHAR2(4000),
    reason          VARCHAR2(1000),
    request_date    DATE DEFAULT SYSDATE,
    status          VARCHAR2(20) DEFAULT '대기'
);


ALTER TABLE property_edit_request
ADD CONSTRAINT fk_edit_user FOREIGN KEY (user_num) REFERENCES users(user_num);

ALTER TABLE property_edit_request
ADD CONSTRAINT fk_edit_property FOREIGN KEY (property_id) REFERENCES property(property_id);

CREATE SEQUENCE property_edit_request_seq
START WITH 1
INCREMENT BY 1
NOCACHE;

-- 최근 본 방 만들기 위 테이블 --
CREATE TABLE recent_view (
  recent_id NUMBER PRIMARY KEY,
  user_num NUMBER NOT NULL,
  property_id NUMBER NOT NULL,
  view_date DATE DEFAULT SYSDATE,

  CONSTRAINT fk_recent_user FOREIGN KEY (user_num)
    REFERENCES users(user_num),

  CONSTRAINT fk_recent_property FOREIGN KEY (property_id)
    REFERENCES property(property_id),

  CONSTRAINT unq_user_property UNIQUE (user_num, property_id)
);


