--회원관리
create table users(
 user_num number not null,
 id varchar2(50) unique not null,
 passwd varchar2(60) not null,
 nick_name varchar2(30) unique,
 authority varchar2(30) default 'ROLE_USER' not null,
 report_count number(1) default 0 not null,
 user_type varchar2(20) default 'USER' not null,
 oauth number(1) default 0 not null, --0:일반, 1:소셜로그인
 constraint users_pk primary key (user_num)
);

create table users_detail(
 user_num number not null,
 name varchar2(30) not null,
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
 valid_date date, --중개사 자격 유효기간
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



