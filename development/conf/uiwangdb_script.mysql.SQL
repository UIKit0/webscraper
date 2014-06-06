

CREATE TABLE WCM_BOARD_B0428 (
  BOARD_ID           int  PRIMARY KEY          NOT NULL, 
  COMMUNITY_ID       varchar(5)       NOT NULL, 
  GROUP_ID           int                NULL, 
  RE_LEVEL           int                NULL, 
  RE_DEPTH           int                NULL, 
  TITLE              varchar(255)         NULL, 
  KOR_NAME           varchar(32)          NULL, 
  REAL_NAME          varchar(32)          NULL, 
  PASSWD             varchar(20)          NULL, 
  JUMIN              varchar(100)         NULL, 
  TELEPHONE          varchar(20)          NULL, 
  ADDR1              varchar(100)         NULL, 
  ADDR2              varchar(100)         NULL, 
  ZIP_CODE1          varchar(3)           NULL, 
  ZIP_CODE2          varchar(3)           NULL, 
  OPEN_YN            varchar(1)           NULL, 
  RE_PROC_YN         varchar(1)           NULL, 
  CONTENTS           text                  NULL, 
  FILE_COUNT         int                NULL, 
  READ_CNT           int                NULL, 
  RECMD_CNT          int                NULL, 
  MEMO_CNT           int                NULL, 
  HTML_YN            varchar(1)           NULL, 
  SECRET_YN          varchar(1)           NULL,
  EMAIL              varchar(64)          NULL, 
  IP_ADDR            varchar(16)          NULL, 
  NOTIFY_YN          varchar(1)           NULL, 
  NOTIFY_SEQ         varchar(1)           NULL, 
  BRANCH_ID          varchar(5)           NULL, 
  DBSTS              varchar(1)           NULL, 
  IS_INDEX           varchar(1)      DEFAULT 'N'           NULL, 
  CREATE_DATE_MOD    varchar(14)          NULL, 
  START_DATE         varchar(14)          NULL, 
  END_DATE           varchar(14)          NULL, 
  ETC_1              varchar(255)         NULL, 
  ETC_2              varchar(255)         NULL, 
  ETC_3              varchar(255)         NULL, 
  ETC_4              varchar(255)         NULL, 
  ETC_5              varchar(255)         NULL, 
  ORGAN              varchar(50)          NULL, 
  ORGAN_CODE         varchar(20)          NULL, 
  COPY_YN            varchar(1)           NULL, 
  CREATE_DATE        varchar(14)          NULL, 
  CREATE_USER_ID     varchar(32)          NULL, 
  UPDATE_DATE        varchar(14)          NULL, 
  UPDATE_USER_ID     varchar(32)          NULL
);






CREATE TABLE UW.WCM_FILE_APPD (
  FILE_APPD_ID      int    PRIMARY KEY         NOT NULL, 
  APPD_FILE_NAME    varchar(255)          NULL, 
  SAVE_FILE_NAME    varchar(255)          NULL, 
  FILE_SIZE         int                 NULL, 
  MIME_TYPE         varchar(32)           NULL, 
  SERVER_PATH       varchar(255)         NULL, 
  WEB_PATH          varchar(255)         NULL, 
  DBSTS             varchar(1)            NULL, 
  CREATE_DATE       varchar(14)           NULL, 
  CREATE_USER_ID    varchar(32)           NULL, 
  UPDATE_DATE       varchar(14)           NULL, 
  UPDATE_USER_ID    varchar(32)           NULL, 
  COMMUNITY_ID      varchar(5)            NULL, 
  BOARD_ID          int                 NULL, 
  DOWN_CNT          int                 NULL, 
  ETC               varchar(10)           NULL
)


