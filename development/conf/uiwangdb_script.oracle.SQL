/*------------------------------------------------------------------------------
-- 개체 이름 : UW.WCM_BOARD_B0428
-- 만든 날짜 : 2012-03-20 오후 4:15:02
-- 마지막으로 수정한 날짜 : 2012-03-31 오전 11:12:24
-- 상태 : VALID
------------------------------------------------------------------------------*/
DROP TABLE UW.WCM_BOARD_B0428 CASCADE CONSTRAINTS;

CREATE TABLE UW.WCM_BOARD_B0428 (
  BOARD_ID           NUMBER            NOT NULL, 
  COMMUNITY_ID       VARCHAR2(5)       NOT NULL, 
  GROUP_ID           NUMBER                NULL, 
  RE_LEVEL           NUMBER                NULL, 
  RE_DEPTH           NUMBER                NULL, 
  TITLE              VARCHAR2(255)         NULL, 
  KOR_NAME           VARCHAR2(32)          NULL, 
  REAL_NAME          VARCHAR2(32)          NULL, 
  PASSWD             VARCHAR2(20)          NULL, 
  JUMIN              VARCHAR2(100)         NULL, 
  TELEPHONE          VARCHAR2(20)          NULL, 
  ADDR1              VARCHAR2(100)         NULL, 
  ADDR2              VARCHAR2(100)         NULL, 
  ZIP_CODE1          VARCHAR2(3)           NULL, 
  ZIP_CODE2          VARCHAR2(3)           NULL, 
  OPEN_YN            VARCHAR2(1)           NULL, 
  RE_PROC_YN         VARCHAR2(1)           NULL, 
  CONTENTS           CLOB                  NULL, 
  FILE_COUNT         NUMBER                NULL, 
  READ_CNT           NUMBER                NULL, 
  RECMD_CNT          NUMBER                NULL, 
  MEMO_CNT           NUMBER                NULL, 
  HTML_YN            VARCHAR2(1)           NULL, 
  SECRET_YN          VARCHAR2(1)           NULL, 
  EMAIL              VARCHAR2(64)          NULL, 
  IP_ADDR            VARCHAR2(16)          NULL, 
  NOTIFY_YN          VARCHAR2(1)           NULL, 
  NOTIFY_SEQ         VARCHAR2(1)           NULL, 
  BRANCH_ID          VARCHAR2(5)           NULL, 
  DBSTS              VARCHAR2(1)           NULL, 
  IS_INDEX           VARCHAR2(1)      DEFAULT 'N'           NULL, 
  CREATE_DATE_MOD    VARCHAR2(14)          NULL, 
  START_DATE         VARCHAR2(14)          NULL, 
  END_DATE           VARCHAR2(14)          NULL, 
  ETC_1              VARCHAR2(300)         NULL, 
  ETC_2              VARCHAR2(300)         NULL, 
  ETC_3              VARCHAR2(300)         NULL, 
  ETC_4              VARCHAR2(300)         NULL, 
  ETC_5              VARCHAR2(300)         NULL, 
  ETC_6              VARCHAR2(300)         NULL, 
  ETC_7              VARCHAR2(300)         NULL, 
  ETC_8              VARCHAR2(300)         NULL, 
  ETC_9              VARCHAR2(300)         NULL, 
  ETC_10             VARCHAR2(300)         NULL, 
  ETC_11             VARCHAR2(300)         NULL, 
  ETC_12             VARCHAR2(300)         NULL, 
  ORGAN              VARCHAR2(50)          NULL, 
  ORGAN_CODE         VARCHAR2(20)          NULL, 
  POSITION           VARCHAR2(50)          NULL, 
  POSITION_CODE      VARCHAR2(20)          NULL, 
  COPY_YN            VARCHAR2(1)           NULL, 
  MOBILE             VARCHAR2(20)          NULL, 
  VG                 NUMBER                NULL, 
  G                  NUMBER                NULL, 
  N                  NUMBER                NULL, 
  B                  NUMBER                NULL, 
  VB                 NUMBER                NULL, 
  TAG_WORD           VARCHAR2(120)         NULL, 
  CREATE_DATE        VARCHAR2(14)          NULL, 
  CREATE_USER_ID     VARCHAR2(32)          NULL, 
  UPDATE_DATE        VARCHAR2(14)          NULL, 
  UPDATE_USER_ID     VARCHAR2(32)          NULL
)
TABLESPACE UWCMS
PCTFREE 10
PCTUSED 0
INITRANS 1
MAXTRANS 255
STORAGE (
    INITIAL 64 K
    MINEXTENTS 1
    MAXEXTENTS UNLIMITED
)
LOGGING
NOCACHE
MONITORING
NOPARALLEL
;

/*------------------------------------------------------------------------------
-- 개체 이름 : UW.WCM_BOARD_B0428_IDX02
-- 만든 날짜 : 2012-03-20 오후 4:15:02
-- 마지막으로 수정한 날짜 : 2012-03-31 오전 11:12:24
-- 상태 : VALID
------------------------------------------------------------------------------*/
DROP INDEX UW.WCM_BOARD_B0428_IDX02;

CREATE INDEX UW.WCM_BOARD_B0428_IDX02
ON UW.WCM_BOARD_B0428 ("GROUP_ID" DESC, RE_DEPTH)
PCTFREE 10
INITRANS 2
MAXTRANS 255
STORAGE (
    INITIAL 64 K
    NEXT 0 K
    MINEXTENTS 1
    MAXEXTENTS UNLIMITED
    PCTINCREASE 0
)
TABLESPACE UWCMS
LOGGING
NOPARALLEL;

/*------------------------------------------------------------------------------
-- 개체 이름 : UW.WCM_BOARD_B0428_IDX03
-- 만든 날짜 : 2012-03-20 오후 4:15:02
-- 마지막으로 수정한 날짜 : 2012-03-31 오전 11:12:24
-- 상태 : VALID
------------------------------------------------------------------------------*/
DROP INDEX UW.WCM_BOARD_B0428_IDX03;

CREATE INDEX UW.WCM_BOARD_B0428_IDX03
ON UW.WCM_BOARD_B0428 ("CREATE_DATE_MOD" DESC, "GROUP_ID" DESC, RE_DEPTH)
PCTFREE 10
INITRANS 2
MAXTRANS 255
STORAGE (
    INITIAL 64 K
    NEXT 0 K
    MINEXTENTS 1
    MAXEXTENTS UNLIMITED
    PCTINCREASE 0
)
TABLESPACE UWCMS
LOGGING
NOPARALLEL;

/*------------------------------------------------------------------------------
-- 개체 이름 : UW.WCM_BOARD_B0428_IDX01
-- 만든 날짜 : 2012-03-20 오후 4:15:02
-- 마지막으로 수정한 날짜 : 2012-03-31 오전 11:12:24
-- 상태 : VALID
------------------------------------------------------------------------------*/
DROP INDEX UW.WCM_BOARD_B0428_IDX01;

CREATE UNIQUE INDEX UW.WCM_BOARD_B0428_IDX01
ON UW.WCM_BOARD_B0428 (BOARD_ID)
PCTFREE 10
INITRANS 2
MAXTRANS 255
STORAGE (
    INITIAL 64 K
    NEXT 0 K
    MINEXTENTS 1
    MAXEXTENTS UNLIMITED
    PCTINCREASE 0
)
TABLESPACE UWCMS
LOGGING
NOPARALLEL;

ALTER TABLE UW.WCM_BOARD_B0428 ADD
(
    PRIMARY KEY ( BOARD_ID )
);








/*------------------------------------------------------------------------------
-- 개체 이름 : UW.WCM_FILE_APPD
-- 만든 날짜 : 2008-11-29 오후 3:11:47
-- 마지막으로 수정한 날짜 : 2008-11-29 오후 3:11:47
-- 상태 : VALID
------------------------------------------------------------------------------*/
DROP TABLE UW.WCM_FILE_APPD CASCADE CONSTRAINTS;

CREATE TABLE UW.WCM_FILE_APPD (
  FILE_APPD_ID      NUMBER             NOT NULL, 
  APPD_FILE_NAME    VARCHAR2(500)          NULL, 
  SAVE_FILE_NAME    VARCHAR2(500)          NULL, 
  FILE_SIZE         NUMBER                 NULL, 
  MIME_TYPE         VARCHAR2(32)           NULL, 
  SERVER_PATH       VARCHAR2(1000)         NULL, 
  WEB_PATH          VARCHAR2(1000)         NULL, 
  DBSTS             VARCHAR2(1)            NULL, 
  CREATE_DATE       VARCHAR2(14)           NULL, 
  CREATE_USER_ID    VARCHAR2(32)           NULL, 
  UPDATE_DATE       VARCHAR2(14)           NULL, 
  UPDATE_USER_ID    VARCHAR2(32)           NULL, 
  COMMUNITY_ID      VARCHAR2(5)            NULL, 
  BOARD_ID          NUMBER                 NULL, 
  DOWN_CNT          NUMBER                 NULL, 
  ETC               VARCHAR2(10)           NULL
)
TABLESPACE UWCMS
PCTFREE 10
PCTUSED 0
INITRANS 1
MAXTRANS 255
STORAGE (
    INITIAL 2048 K
    MINEXTENTS 1
    MAXEXTENTS UNLIMITED
)
LOGGING
NOCACHE
MONITORING
NOPARALLEL
;

COMMENT ON TABLE UW.WCM_FILE_APPD IS '게시판 첨부파일 관련 데이터 테이블';

COMMENT ON COLUMN UW.WCM_FILE_APPD.FILE_APPD_ID IS '첨부파일 아이디';

COMMENT ON COLUMN UW.WCM_FILE_APPD.APPD_FILE_NAME IS '첨부파일 명(실제 파일명)';

COMMENT ON COLUMN UW.WCM_FILE_APPD.SAVE_FILE_NAME IS '첨부파일 저장시 파일명';

COMMENT ON COLUMN UW.WCM_FILE_APPD.FILE_SIZE IS '파일 사이즈';

COMMENT ON COLUMN UW.WCM_FILE_APPD.MIME_TYPE IS '첨부파일 타입';

COMMENT ON COLUMN UW.WCM_FILE_APPD.SERVER_PATH IS '첨부파일의 절대경로';

COMMENT ON COLUMN UW.WCM_FILE_APPD.WEB_PATH IS '첨부파일의 웹경로';

COMMENT ON COLUMN UW.WCM_FILE_APPD.DBSTS IS '상태';

COMMENT ON COLUMN UW.WCM_FILE_APPD.CREATE_DATE IS '생성일';

COMMENT ON COLUMN UW.WCM_FILE_APPD.CREATE_USER_ID IS '생성자 아이디';

COMMENT ON COLUMN UW.WCM_FILE_APPD.UPDATE_DATE IS '수정일';

COMMENT ON COLUMN UW.WCM_FILE_APPD.UPDATE_USER_ID IS '수정자 아이디';

COMMENT ON COLUMN UW.WCM_FILE_APPD.COMMUNITY_ID IS '게시판 아이디';

COMMENT ON COLUMN UW.WCM_FILE_APPD.BOARD_ID IS '게시물 아이디';

COMMENT ON COLUMN UW.WCM_FILE_APPD.DOWN_CNT IS '다운로드 회수';

/*------------------------------------------------------------------------------
-- 개체 이름 : UW.WCM_FILE_APPD_IDX01
-- 만든 날짜 : 2008-11-29 오후 3:11:47
-- 마지막으로 수정한 날짜 : 2008-11-29 오후 3:11:47
-- 상태 : VALID
------------------------------------------------------------------------------*/
DROP INDEX UW.WCM_FILE_APPD_IDX01;

CREATE UNIQUE INDEX UW.WCM_FILE_APPD_IDX01
ON UW.WCM_FILE_APPD (FILE_APPD_ID)
PCTFREE 10
INITRANS 2
MAXTRANS 255
STORAGE (
    INITIAL 192 K
    NEXT 0 K
    MINEXTENTS 1
    MAXEXTENTS UNLIMITED
    PCTINCREASE 0
)
TABLESPACE UWCMS
LOGGING
NOPARALLEL;

ALTER TABLE UW.WCM_FILE_APPD ADD
(
    PRIMARY KEY ( FILE_APPD_ID )
);

