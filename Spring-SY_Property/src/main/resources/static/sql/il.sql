--쉐어하우스
create table sharehouse (
    house_id            NUMBER PRIMARY KEY,
    name                VARCHAR2(100) not null,
    s_state				NUMBER default 0 not null, -- 0:거래중, 1:거래완료
    s_hit    			NUMBER default 0 not null,
    s_reg_date			date default sysdate not null,
    s_modi_date 		date,
    address             VARCHAR2(300) not null,
    sido                VARCHAR2(50) not null,
    sigungu             VARCHAR2(50) not null,
    dong                VARCHAR2(50) not null,
    latitude            VARCHAR2(30) not null,
    longitude           VARCHAR2(30) not null,
    floor_info          VARCHAR2(50),
    gender_type_cd      VARCHAR2(50), --GENDR00001:성별무관, GENDR00002:남성전용, GENDR00003:여성전용, GENDR00004:남녀분리
    deposit_min         NUMBER, -- 최소 보증금(room 테이블에서 가져올 수 있음)
    deposit_max         NUMBER, -- 최대 보증금(room 테이블에서 가져올 수 있음)
    price_min           NUMBER, -- 최소 월 이용료(room 테이블에서 가져올 수 있음)
    price_max           NUMBER, -- 최대 월 이용료(room 테이블에서 가져올 수 있음)
    maintenance_fees	VARCHAR2(200), -- 관리비 (room 테이블에서 가져올 수 있음; 7.0|7.0|7.0|7.0|6.0 형식)
    enter_age_min		NUMBER,	-- 입주 최소 나이
    enter_age_max		NUMBER,	-- 입주 최대 나이
    structure			VARCHAR2(20), --건물형태(단독건물,단독주택,빌라/연립,상가건물 등)    
    house_info			CLOB, --쉐어하우스 소개
    move_requirements	CLOB, --입주 조건
    operator			VARCHAR2(50), -- 운영자
    realtor_num			NUMBER --TODO: 나중에 NOT NULL, FK 조건 및 데이터 추가
    --TODO: 공용시설 facilities 추가(데이터 재가공 필요)
    --TODO: TAGS -> 엘리베이터, 주차공간 있으면 해당내용 반영할지 추후 검토요망
    --review_cnt        NUMBER, -- 리뷰(나중에 추가 여부 재검토)
);
	
--쉐어하우스 사진 (+평면도 사진)
create table sharehouse_img (
	house_id 		NUMBER not null,
	sh_img_name		VARCHAR2(200),
	sh_img			BLOB,
	sh_img_seq		NUMBER, -- SEQ
	sh_img_type 	VARCHAR2(20),  -- 'HOUSE','STRUCTURE'
	CONSTRAINT sh_img_fk FOREIGN KEY (house_id) REFERENCES sharehouse(house_id)
);	

--쉐어하우스 룸
create table sharehouse_room (
    room_id         NUMBER not null, --ID
    house_id        NUMBER not null,
    room_name       VARCHAR2(60) not null, --NAME
    room_state		NUMBER default 0 not null, -- 0:공실, 1:입주중
    capacity		VARCHAR2(30), -- N인실
    deposit         NUMBER not null,
    monthly_price   NUMBER not null, --PRICE
    maintenance_fee NUMBER, -- -1: 1/n
    duration_min	NUMBER,
    duration_max    NUMBER,
    options         VARCHAR2(300), --각 방 facilities(데이터 재가공 필요)
    room_info     	CLOB, --INFO (방 상세정보)
    CONSTRAINT sh_room_pk PRIMARY KEY (room_id),
    CONSTRAINT sh_room_fk FOREIGN KEY (house_id) REFERENCES sharehouse(house_id)
);

--쉐어하우스 룸 사진
CREATE TABLE sharehouse_room_img (
	room_id 		NUMBER not null,
	shr_img_name	VARCHAR2(200),
	shr_img			BLOB,
	shr_img_seq		NUMBER, -- SEQ
  	CONSTRAINT sh_room_img_fk FOREIGN KEY (room_id) REFERENCES sharehouse_room(room_id)
);

--쉐어하우스 근처 역
CREATE TABLE sharehouse_subways (   
	house_id 		NUMBER not null,
	station_name 	VARCHAR2(50),
	line_short		VARCHAR2(50),
	distance		NUMBER,
	CONSTRAINT sh_subways_fk FOREIGN KEY (house_id) REFERENCES sharehouse(house_id)
);

--쉐어하우스 근처 학교
CREATE TABLE sharehouse_schools (
	house_id 		NUMBER not null,
	school_name 	VARCHAR2(50),
	distance		NUMBER,
  	CONSTRAINT sh_schools_fk FOREIGN KEY (house_id) REFERENCES sharehouse(house_id)
);










