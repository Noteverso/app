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

 Date: 05/08/2023 23:53:05
*/


-- ----------------------------
-- Table structure for noteverso_view_option
-- ----------------------------
-- DROP TABLE IF EXISTS "public"."noteverso_view_option";
CREATE TABLE "public"."noteverso_view_option" (
  "id" int8 NOT NULL DEFAULT nextval('noteverso_view_option_id_seq'::regclass),
  "type" int2 NOT NULL,
  "note_id" int8,
  "project_id" int8,
  "label_id" int8,
  "view_mode" "public"."style" NOT NULL,
  "grouped_by" int2,
  "sorted_by" int2 NOT NULL DEFAULT 0,
  "sort_order" "public"."order_type" NOT NULL DEFAULT 'ASC'::order_type,
  "show_archived_notes" bool NOT NULL DEFAULT false,
  "is_deleted" bool DEFAULT false,
  "filtered_by" int2,
  "added_at" timestamptz(6),
  "update_at" timestamptz(6)
)
;
ALTER TABLE "public"."noteverso_view_option" OWNER TO "postgres";
COMMENT ON COLUMN "public"."noteverso_view_option"."id" IS '视图选项id';
COMMENT ON COLUMN "public"."noteverso_view_option"."type" IS '视图选项类型 0 - PROJECT, 1 - LABEL, 2 - UNCOMING, 3 - PAST, 4 - TODAY, 5 - LINKED_NOTE';
COMMENT ON COLUMN "public"."noteverso_view_option"."note_id" IS '笔记id';
COMMENT ON COLUMN "public"."noteverso_view_option"."project_id" IS '项目id';
COMMENT ON COLUMN "public"."noteverso_view_option"."label_id" IS '标签id';
COMMENT ON COLUMN "public"."noteverso_view_option"."view_mode" IS '笔记布局 list - 列表，board - 看板';
COMMENT ON COLUMN "public"."noteverso_view_option"."grouped_by" IS '分组方式 0 - NOTE_STATUS,1 - ADDED_DATE,2 - NOTE_LABEL';
COMMENT ON COLUMN "public"."noteverso_view_option"."sorted_by" IS '排序方式 0 - ADDED_DATE,1 - COMMENT_COUNT,2 - LINKED_NOTE_COUNT';
COMMENT ON COLUMN "public"."noteverso_view_option"."sort_order" IS '排序规则 ASC,DESC';
COMMENT ON COLUMN "public"."noteverso_view_option"."show_archived_notes" IS '是否显示已归档笔记';
COMMENT ON COLUMN "public"."noteverso_view_option"."is_deleted" IS '是否删除';
COMMENT ON COLUMN "public"."noteverso_view_option"."filtered_by" IS '过滤方式';
COMMENT ON COLUMN "public"."noteverso_view_option"."added_at" IS '添加时间';
COMMENT ON COLUMN "public"."noteverso_view_option"."update_at" IS '更新时间';
COMMENT ON TABLE "public"."noteverso_view_option" IS '视图选项表';

-- ----------------------------
-- Records of noteverso_view_option
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Primary Key structure for table noteverso_view_option
-- ----------------------------
ALTER TABLE "public"."noteverso_view_option" ADD CONSTRAINT "noteverso_view_option_pkey" PRIMARY KEY ("id");
