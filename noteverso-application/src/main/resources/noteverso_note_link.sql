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

 Date: 05/08/2023 23:52:30
*/


-- ----------------------------
-- Table structure for noteverso_note_link
-- ----------------------------
-- DROP TABLE IF EXISTS "public"."noteverso_note_link";
CREATE TABLE "public"."noteverso_note_link" (
  "id" int8 NOT NULL DEFAULT nextval('noteverso_note_link_id_seq'::regclass),
  "note_id" int8 NOT NULL,
  "linked_note_id" int8 NOT NULL,
  "added_at" timestamptz(6),
  "is_deleted" bool DEFAULT false,
  "view_style" "public"."style" NOT NULL
)
;
ALTER TABLE "public"."noteverso_note_link" OWNER TO "postgres";
COMMENT ON COLUMN "public"."noteverso_note_link"."id" IS '笔记关联id';
COMMENT ON COLUMN "public"."noteverso_note_link"."note_id" IS '笔记id';
COMMENT ON COLUMN "public"."noteverso_note_link"."linked_note_id" IS '关联至此的笔记id';
COMMENT ON COLUMN "public"."noteverso_note_link"."added_at" IS '添加时间';
COMMENT ON COLUMN "public"."noteverso_note_link"."is_deleted" IS '是否删除';
COMMENT ON COLUMN "public"."noteverso_note_link"."view_style" IS '关联笔记UI布局方式 list - 列表，board - 看板';
COMMENT ON TABLE "public"."noteverso_note_link" IS '笔记关联表';

-- ----------------------------
-- Records of noteverso_note_link
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Primary Key structure for table noteverso_note_link
-- ----------------------------
ALTER TABLE "public"."noteverso_note_link" ADD CONSTRAINT "noteverso_note_link_pkey" PRIMARY KEY ("id");
