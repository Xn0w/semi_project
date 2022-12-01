-- 갤러리 댓글 삭제
DROP TABLE GAL_CMT;
-- 좋아요 삭제
DROP TABLE LIKED;
-- 써머노트 삭제
DROP TABLE SUMMERNOTE_IMAGE;
-- 갤러리 게시판 삭제
DROP TABLE GALLERY;
-- 자유 게시판 삭제
DROP TABLE BOARD;
-- 첨부 삭제
DROP TABLE ATTACH;
-- 업로드 게시판 삭제
DROP TABLE UPLOAD;
-- 접속기록 삭제
DROP TABLE ACCESS_LOG;
-- 탈퇴 회원 삭제
DROP TABLE RETIRE_USERS;
-- 회원 삭제
DROP TABLE USERS;
-- 휴면 회원 삭제
DROP TABLE SLEEP_USERS;

-- 회원 생성
CREATE TABLE USERS (
   USER_NO               NUMBER              NOT NULL,           -- 회원 번호, PK
   ID                   VARCHAR2(50)          NOT NULL UNIQUE,    -- 회원 ID
   PW                   VARCHAR2(64)      NOT NULL,           -- 암호화된 비번 최대 64바이트
   NAME               VARCHAR2(50)      NOT NULL,            
   GENDER               VARCHAR2(2)          NOT NULL,           -- M, F, NO
   EMAIL               VARCHAR2(50)      NOT NULL UNIQUE,    -- 이메일, 이메일 인증 때문에 UNIQUE
   MOBILE               VARCHAR2(11)      NOT NULL,           -- 전화번호, 하이픈 제외 최대 11자리
   BIRTHYEAR           VARCHAR2(4)          NOT NULL,           -- 출생년도(YYYY)
   BIRTHDAY               VARCHAR2(4)          NOT NULL,           -- 생일(MMDD)
   POSTCODE               VARCHAR2(5)          NULL,               -- 우편번호
   ROAD_ADDRESS           VARCHAR2(100)      NULL,               -- 도로명 주소
   JIBUN_ADDRESS       VARCHAR2(100)      NULL,               -- 지번 주소
   DETAIL_ADDRESS       VARCHAR2(100)      NULL,               -- 상세 주소
   EXTRA_ADDRESS       VARCHAR2(100)      NULL,               -- 참고 주소
   POINT               NUMBER              NOT NULL,           -- 포인트
   AGREE_CODE           NUMBER              NOT NULL,           -- 동의여부(0:필수, 1:필수+위치, 2:필수+프로모션, 3:필수+위치+프로모션)
   SNS_TYPE               VARCHAR2(10)      NULL,               -- 간편가입종류(사이트가입:null, 네아로:naver)
   JOIN_DATE           DATE              NOT NULL,           
   PW_MODIFY_DATE       DATE              NULL,               
   INFO_MODIFY_DATE       DATE              NULL,
   SESSION_ID           VARCHAR2(50)          NULL,               -- 세션 아이디
   SESSION_LIMIT_DATE   DATE              NULL                -- 세션 만료일
);
-- 회원 포인트 지급 기준 
-- 댓글 : 5, 자유 게시판 작성 : 10, 갤러리 게시판 작성 : 20 , 업로드 게시판 작성 : 50, 
-- 다운로드 가능한 포인트 : 100, 
-- 다운로드 차감 포인트 : 30,
-- 휴면 회원으로 넘어가면 포인트 : 0, 복구를 해도 0

-- 회원 테이블 기본키
ALTER TABLE USERS ADD CONSTRAINT PK_USERS PRIMARY KEY (
   USER_NO
);
 
-- 회원 시퀀스
DROP SEQUENCE USERS_SEQ;
CREATE SEQUENCE USERS_SEQ NOCACHE;

-- 접속기록 생성
CREATE TABLE ACCESS_LOG (
   ACCESS_LOG_NO   NUMBER          NOT NULL,   -- 접속 기록 번호, PK
   ID               VARCHAR2(50)    NOT NULL UNIQUE,   -- 회원 ID, FK
   LAST_LOGIN_DATE   DATE          NOT NULL    -- 마지막 접속날짜가 현재 날짜를 기준으로 1년이 지나면 스케쥴러의 크론식을 이용하여 휴면회원테이블로 보내고 유저테이블에서 삭제
);

-- 접속기록 기본키
ALTER TABLE ACCESS_LOG ADD CONSTRAINT PK_ACCESS_LOG PRIMARY KEY (
   ACCESS_LOG_NO
);
-- 접속기록 외래키 (아이디)
ALTER TABLE ACCESS_LOG ADD CONSTRAINT FK_ACCESS_LOG_USERS FOREIGN KEY(ID) REFERENCES USERS(ID) ON DELETE CASCADE;

-- 접속기록 시퀀스
DROP SEQUENCE ACCESS_LOG_SEQ;
CREATE SEQUENCE ACCESS_LOG_SEQ NOCACHE;

-- 탈퇴 회원 생성
CREATE TABLE RETIRE_USERS (
   USER_NO       NUMBER          NOT NULL,   -- 회원 번호
   ID           VARCHAR2(50)       NOT NULL UNIQUE,  -- 회원 ID
   JOIN_DATE   DATE          NULL,       -- 가입 일자
   RETIRE_DATE   DATE          NULL        -- 탈퇴 일자
);

-- 휴면 회원 생성
CREATE TABLE SLEEP_USERS (
   USER_NO           NUMBER              NOT NULL,           -- PK, 본래의 회원번호를 가져옴
   ID               VARCHAR2(50)          NOT NULL UNIQUE,
   PW               VARCHAR2(64)      NOT NULL,           -- 암호화된 비번 최대 64바이트
   NAME           VARCHAR2(50)      NOT NULL,
   GENDER           VARCHAR2(2)          NOT NULL,           -- M, F, NO
   EMAIL           VARCHAR2(50)      NOT NULL UNIQUE,    -- 이메일 인증 때문에 UNIQUE
   MOBILE           VARCHAR2(11)      NOT NULL,           -- 하이픈 제외 최대 11자리
   BIRTHYEAR       VARCHAR2(4)          NOT NULL,           -- 출생년도(YYYY)
   BIRTHDAY           VARCHAR2(4)          NOT NULL,           -- 생일(MMDD)
   POSTCODE           VARCHAR2(5)          NULL,
   ROAD_ADDRESS       VARCHAR2(100)      NULL,
   JIBUN_ADDRESS   VARCHAR2(100)      NULL,
   DETAIL_ADDRESS   VARCHAR2(100)      NULL,
   EXTRA_ADDRESS   VARCHAR2(100)      NULL,
   AGREE_CODE       NUMBER              NOT NULL,           -- 동의여부(0:필수, 1:필수+위치, 2:필수+프로모션, 3:필수+위치+프로모션)
   SNS_TYPE           VARCHAR2(10)      NULL,               -- 간편가입종류(사이트가입:null, 네아로:naver)
   JOIN_DATE       DATE              NULL,               -- 가입 일자
   LAST_LOGIN_DATE   DATE              NULL,               -- 마지막 로그인 일자
   SLEEP_DATE       DATE              NULL                -- 휴면 회원으로 바뀐 일자
);
-- 휴면 회원 기본키
ALTER TABLE SLEEP_USERS ADD CONSTRAINT PK_SLEEP_USERS PRIMARY KEY (
   USER_NO
);

INSERT INTO USERS
    (USER_NO, ID, PW, NAME, GENDER, EMAIL, MOBILE, BIRTHYEAR, BIRTHDAY, POINT, AGREE_CODE, JOIN_DATE)
VALUES
    (USERS_SEQ.NEXTVAL, 'admin', '1234', '관리자', 'X', 'admin@web.com', '01000000000', '0', '0', 0, 0, SYSDATE);

-- 자유 게시판 생성
CREATE TABLE BOARD (
   BD_NO           NUMBER              NOT NULL,   -- 자유 게시판 번호
   ID               VARCHAR2(50)          NOT NULL,   -- 회원 ID
   BD_TITLE           VARCHAR2(200)      NOT NULL,
   BD_CONTENT       VARCHAR2(4000)      NULL,       -- 내용 최대 4000byte 까지 가능
   BD_CREATE_DATE   TIMESTAMP          NOT NULL,
   BD_MODIFY_DATE   TIMESTAMP          NOT NULL,
   BD_STATE           NUMBER(1)          NOT NULL,   -- 정상: 1, 삭제: 0
   BD_DEPTH           NUMBER(2)          NULL,       -- 원글: 0, 1차답글: 1, 2차댓글:2 , ...
   BD_GROUP_NO       NUMBER              NOT NULL,
   BD_GROUP_ORDER   NUMBER              NOT NULL,   -- 동일 그룹 내 표시 순서
   BD_IP           VARCHAR2(20)          NOT NULL    -- 화면에 IP 출력용
);
-- 자유 게시판 기본키
ALTER TABLE BOARD ADD CONSTRAINT PK_BOARD PRIMARY KEY (
   BD_NO
);
-- 자유 게시판 외래키 (아이디)
ALTER TABLE BOARD ADD CONSTRAINT FK_BOARD_USERS FOREIGN KEY(ID) REFERENCES USERS(ID) ON DELETE SET NULL;
-- 자유 시퀀스
DROP SEQUENCE BD_SEQ;
CREATE SEQUENCE BD_SEQ NOCACHE;

-- 업로드 게시판 생성
CREATE TABLE UPLOAD (
   UP_NO           NUMBER              NOT NULL,   -- 업로드 번호
   ID               VARCHAR2(50)           NOT NULL,   -- 회원 ID
   UP_TITLE           VARCHAR2(200)       NOT NULL,   
   UP_CONTENT       VARCHAR2(4000)       NULL,
   UP_CREATE_DATE   TIMESTAMP          NOT NULL,
   UP_MODIFY_DATE   TIMESTAMP          NOT NULL,
   UP_HIT           NUMBER              NOT NULL,    -- 업로드 조회수
    UP_IP           VARCHAR2(20)          NOT NULL    -- 화면에 IP 출력용
);
-- 업로드 게시판 기본키
ALTER TABLE UPLOAD ADD CONSTRAINT PK_UPLOAD PRIMARY KEY (
   UP_NO
);
-- 업로드 게시판 외래키 (아이디)
ALTER TABLE UPLOAD ADD CONSTRAINT FK_UPLOAD_USERS FOREIGN KEY(ID) REFERENCES USERS(ID) ON DELETE SET NULL;
-- 업로드 시퀀스
DROP SEQUENCE UPLOAD_SEQ;
CREATE SEQUENCE UPLOAD_SEQ NOCACHE;

-- 갤러리 게시판 생성
CREATE TABLE GALLERY (
   GAL_NO           NUMBER              NOT NULL,   -- 갤러리 번호
   ID               VARCHAR2(50)          NOT NULL,   -- 회원 ID
   GAL_TITLE       VARCHAR2(200)      NOT NULL,
   GAL_CONTENT       VARCHAR2(4000),
   GAL_CREATE_DATE   TIMESTAMP          NOT NULL,
   GAL_MODIFY_DATE   TIMESTAMP          NOT NULL,
   GAL_HIT           NUMBER              NOT NULL,   -- 갤러리 조회수
   GAL_IP           VARCHAR2(20)          NOT NULL    -- 화면에 IP 출력용
);
-- 갤러리 게시판 기본키
ALTER TABLE GALLERY ADD CONSTRAINT PK_GALLERY PRIMARY KEY (
   GAL_NO
);
-- 갤러리 게시판 외래키 (아이디)
ALTER TABLE GALLERY ADD CONSTRAINT FK_GALLERY_USERS FOREIGN KEY(ID) REFERENCES USERS(ID) ON DELETE SET NULL;
-- 갤러리 게시판 시퀀스
DROP SEQUENCE GALLERY_SEQ;
CREATE SEQUENCE GALLERY_SEQ NOCACHE;

-- 써머노트에서 사용된 이미지 목록(시퀀스는 사용하지 않음)
CREATE TABLE SUMMERNOTE_IMAGE(
    GAL_NO NUMBER,
    FILESYSTEM VARCHAR2(40 BYTE)
);

ALTER TABLE SUMMERNOTE_IMAGE
    ADD CONSTRAINT FK_SUMMERNOTE_GALLERY
        FOREIGN KEY(GAL_NO) REFERENCES GALLERY(GAL_NO)
            ON DELETE CASCADE;

-- 갤러리 댓글 생성
CREATE TABLE GAL_CMT (
   CMT_NO           NUMBER              NOT NULL,   -- 댓글 번호, PK
   GAL_NO           NUMBER,          -- 갤러리 번호, FK (ON DELETE SET NULL을 위해 NOT NULL 처리하면 안 됨)
    ID              VARCHAR2(50),
   CMT_CONTENT       VARCHAR2(4000)       NOT NULL,   -- 댓글 내용
    CMT_STATE       NUMBER(1) NOT NULL,    -- 댓글 상태(1: 정상, -1: 삭제)
    CMT_DEPTH       NUMBER(2) NOT NULL,     -- 댓글 들여쓰기 (댓글: 0, 댓글의 답글: 1)
    CMT_GROUP_NO    NUMBER NOT NULL,         -- 댓글과 해당 댓글에 달린 답글은 같은 그룹
   CMT_CREATE_DATE   DATE NOT NULL    -- 댓글 작성일자
);
-- 갤러리 댓글 기본키
ALTER TABLE GAL_CMT ADD CONSTRAINT PK_GAL_CMT PRIMARY KEY (
   CMT_NO
);
-- 갤러리 댓글 외래키 (아이디)
ALTER TABLE GAL_CMT ADD CONSTRAINT FK_GAL_CMT_USERS FOREIGN KEY(ID) REFERENCES USERS(ID) ON DELETE SET NULL;
-- 갤러리 댓글 외래키 (갤러리 번호)
ALTER TABLE GAL_CMT ADD CONSTRAINT FK_GAL_CMT_GALLERY FOREIGN KEY(GAL_NO) REFERENCES GALLERY(GAL_NO) ON DELETE SET NULL;
-- 갤러리 댓글 시퀀스
DROP SEQUENCE GAL_CMT_SEQ;
CREATE SEQUENCE GAL_CMT_SEQ NOCACHE;

-- 좋아요 생성
CREATE TABLE LIKED (
   ID       VARCHAR2(50)       NOT NULL,   -- 회원 ID
   GAL_NO   NUMBER          NOT NULL    -- 갤러리 번호
);
-- 좋아요 외래키 (아이디)
ALTER TABLE LIKED ADD CONSTRAINT FK_LIKED_USERS FOREIGN KEY(ID) REFERENCES USERS(ID) ON DELETE SET NULL;
-- 좋아요 외래키 (갤러리 번호)
ALTER TABLE LIKED ADD CONSTRAINT FK_LIKED_GALLERY FOREIGN KEY(GAL_NO) REFERENCES GALLERY(GAL_NO) ON DELETE CASCADE;

-- 첨부 생성
CREATE TABLE ATTACH (
   ATTACH_NO       NUMBER              NOT NULL,   -- 첨부 번호
   UP_NO           NUMBER              NOT NULL,   -- 업로드 번호
   PATH           VARCHAR2(300)      NULL,       -- 파일의 경로
   ORIGIN           VARCHAR2(300)      NULL,       -- 원본 이름
   FILESYSTEM       VARCHAR2(40)      NULL,       -- 파일의 저장된 이름
   DOWNLOAD_CNT       NUMBER              NULL        -- 다운로드 횟수
);
-- 첨부 기본키
ALTER TABLE ATTACH ADD CONSTRAINT PK_ATTACH PRIMARY KEY (
   ATTACH_NO
);
-- 첨부 외래키 (업로드 번호)
ALTER TABLE ATTACH ADD CONSTRAINT FK_ATTACH_UPLOAD FOREIGN KEY (UP_NO) REFERENCES UPLOAD (UP_NO) ON DELETE CASCADE;
-- 첨부 시퀀스
DROP SEQUENCE ATTACH_SEQ;
CREATE SEQUENCE ATTACH_SEQ NOCACHE;