/*
 Navicat Premium Data Transfer

 Source Server         : localhost-postgres
 Source Server Type    : PostgreSQL
 Source Server Version : 140008 (140008)
 Source Host           : localhost:5432
 Source Catalog        : noteverso
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 140008 (140008)
 File Encoding         : 65001

 Date: 05/08/2023 23:52:58
*/


-- ----------------------------
-- Table structure for noteverso_user
-- ----------------------------
-- DROP TABLE IF EXISTS "public"."noteverso_user";
CREATE TABLE "public"."noteverso_user" (
  "id" int8 NOT NULL DEFAULT nextval('noteverso_user_id_seq'::regclass),
  "inbox_project_id" int8 NOT NULL,
  "avatar" jsonb,
  "email" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "full_name" varchar(20) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "has_password" bool NOT NULL DEFAULT false,
  "password" varchar(80) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "token" varchar(50) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "lang" int2,
  "is_premium" bool NOT NULL DEFAULT false,
  "premium_status" int2 NOT NULL,
  "premium_until" timestamptz(6),
  "joined_at" timestamptz(6) NOT NULL,
  "start_page" varchar(30) COLLATE "pg_catalog"."default" NOT NULL,
  "theme_id" int2,
  "tz_info" jsonb,
  "daily_goal" int8,
  "time_format" int2 NOT NULL,
  "date_format" int2 NOT NULL,
  "is_deleted" bool DEFAULT false
)
;
ALTER TABLE "public"."noteverso_user" OWNER TO "postgres";
COMMENT ON COLUMN "public"."noteverso_user"."id" IS '用户id';
COMMENT ON COLUMN "public"."noteverso_user"."inbox_project_id" IS '收件箱项目id';
COMMENT ON COLUMN "public"."noteverso_user"."avatar" IS '头像';
COMMENT ON COLUMN "public"."noteverso_user"."email" IS '邮箱';
COMMENT ON COLUMN "public"."noteverso_user"."full_name" IS '昵称';
COMMENT ON COLUMN "public"."noteverso_user"."has_password" IS '是否有密码';
COMMENT ON COLUMN "public"."noteverso_user"."password" IS '密码';
COMMENT ON COLUMN "public"."noteverso_user"."token" IS 'token';
COMMENT ON COLUMN "public"."noteverso_user"."lang" IS '语言, 0 - zh-cn,1 - en-us';
COMMENT ON COLUMN "public"."noteverso_user"."is_premium" IS '是否是付费用户';
COMMENT ON COLUMN "public"."noteverso_user"."premium_status" IS '付费用户状态 0 - not_premium, 1 - premium';
COMMENT ON COLUMN "public"."noteverso_user"."premium_until" IS '付费用户到期时间';
COMMENT ON COLUMN "public"."noteverso_user"."joined_at" IS '加入时间';
COMMENT ON COLUMN "public"."noteverso_user"."start_page" IS '用户首次登陆应用后的定位页面，project?id=${project_id}, upcoming, label?name=${label_name}';
COMMENT ON COLUMN "public"."noteverso_user"."theme_id" IS '主题id';
COMMENT ON COLUMN "public"."noteverso_user"."tz_info" IS '时区信息 gmt_string, hours, is_dst, minutes, timezone';
COMMENT ON COLUMN "public"."noteverso_user"."daily_goal" IS '日常目标';
COMMENT ON COLUMN "public"."noteverso_user"."time_format" IS '时间格式 0 - 13:00，1 - 1:00pm，默认 0';
COMMENT ON COLUMN "public"."noteverso_user"."date_format" IS '日期格式 0 - YYYY-MM-DD，1 - DD-MM-YYYY，默认 0';
COMMENT ON COLUMN "public"."noteverso_user"."is_deleted" IS '是否删除';
COMMENT ON TABLE "public"."noteverso_user" IS '用户表';

-- ----------------------------
-- Records of noteverso_user
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Primary Key structure for table noteverso_user
-- ----------------------------
ALTER TABLE "public"."noteverso_user" ADD CONSTRAINT "noteverso_user_pkey" PRIMARY KEY ("id");
