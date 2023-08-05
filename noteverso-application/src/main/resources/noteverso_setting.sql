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

 Date: 05/08/2023 23:52:49
*/


-- ----------------------------
-- Table structure for noteverso_setting
-- ----------------------------
-- DROP TABLE IF EXISTS "public"."noteverso_setting";
CREATE TABLE "public"."noteverso_setting" (
  "id" int8 NOT NULL DEFAULT nextval('noteverso_setting_id_seq'::regclass),
  "user_id" int8 NOT NULL,
  "note" jsonb,
  "quick_add" jsonb,
  "navigation" jsonb
)
;
ALTER TABLE "public"."noteverso_setting" OWNER TO "postgres";
COMMENT ON COLUMN "public"."noteverso_setting"."id" IS 'id';
COMMENT ON COLUMN "public"."noteverso_setting"."user_id" IS '用户id';
COMMENT ON COLUMN "public"."noteverso_setting"."note" IS '笔记详情展示情况设置';
COMMENT ON COLUMN "public"."noteverso_setting"."quick_add" IS '编辑器设置';
COMMENT ON COLUMN "public"."noteverso_setting"."navigation" IS '导航设置';
COMMENT ON TABLE "public"."noteverso_setting" IS '用户设置模块';

-- ----------------------------
-- Records of noteverso_setting
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Primary Key structure for table noteverso_setting
-- ----------------------------
ALTER TABLE "public"."noteverso_setting" ADD CONSTRAINT "noteverso_setting_pkey" PRIMARY KEY ("id");
