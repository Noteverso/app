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

 Date: 05/08/2023 23:52:23
*/


-- ----------------------------
-- Table structure for noteverso_note
-- ----------------------------
-- DROP TABLE IF EXISTS "public"."noteverso_note";
CREATE TABLE "public"."noteverso_note" (
  "id" int8 NOT NULL DEFAULT nextval('noteverso_note_id_seq'::regclass),
  "note_type" int2 NOT NULL,
  "content" text COLLATE "pg_catalog"."default" NOT NULL,
  "is_top" bool DEFAULT false,
  "is_deleted" bool DEFAULT false,
  "is_archived" bool DEFAULT false,
  "comment_count" int8,
  "linked_note_count" int8,
  "project_id" int8 NOT NULL,
  "labels" jsonb,
  "status" int2 NOT NULL,
  "creator_id" int8 NOT NULL,
  "attachment" jsonb,
  "url" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "added_at" timestamptz(6),
  "updated_at" timestamptz(6)
)
;
ALTER TABLE "public"."noteverso_note" OWNER TO "postgres";
COMMENT ON COLUMN "public"."noteverso_note"."id" IS '笔记id';
COMMENT ON COLUMN "public"."noteverso_note"."note_type" IS '笔记类型，0-普通笔记 1-账户密码 2-待办清单 3-图表 4-日程 5-工具清单 6-记账(订阅信息，可自动更新续费信息)';
COMMENT ON COLUMN "public"."noteverso_note"."content" IS '笔记内容';
COMMENT ON COLUMN "public"."noteverso_note"."is_top" IS '是否将项目添加到收藏夹';
COMMENT ON COLUMN "public"."noteverso_note"."is_deleted" IS '是否删除';
COMMENT ON COLUMN "public"."noteverso_note"."is_archived" IS '是否归档';
COMMENT ON COLUMN "public"."noteverso_note"."comment_count" IS '笔记评论数';
COMMENT ON COLUMN "public"."noteverso_note"."linked_note_count" IS '关联至此的笔记数量';
COMMENT ON COLUMN "public"."noteverso_note"."project_id" IS '笔记所属的项目id';
COMMENT ON COLUMN "public"."noteverso_note"."labels" IS '笔记标签，字符串数组';
COMMENT ON COLUMN "public"."noteverso_note"."status" IS '笔记状态 0 -  待处理，1 - 正在进行，2 - 已完成';
COMMENT ON COLUMN "public"."noteverso_note"."creator_id" IS '创建人';
COMMENT ON COLUMN "public"."noteverso_note"."attachment" IS '附件对象，内容包括附件名称、附件类型、附件链接和资源类型';
COMMENT ON COLUMN "public"."noteverso_note"."url" IS '笔记链接，从 web、移动端应用通过链接进入笔记';
COMMENT ON COLUMN "public"."noteverso_note"."added_at" IS '添加时间';
COMMENT ON COLUMN "public"."noteverso_note"."updated_at" IS '更新时间';
COMMENT ON TABLE "public"."noteverso_note" IS '笔记表';

-- ----------------------------
-- Records of noteverso_note
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Primary Key structure for table noteverso_note
-- ----------------------------
ALTER TABLE "public"."noteverso_note" ADD CONSTRAINT "noteverso_note_pkey" PRIMARY KEY ("id");
