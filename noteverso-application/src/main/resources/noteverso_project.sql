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

 Date: 05/08/2023 23:52:39
*/


-- ----------------------------
-- Table structure for noteverso_project
-- ----------------------------
-- DROP TABLE IF EXISTS "public"."noteverso_project";
CREATE TABLE "public"."noteverso_project" (
  "id" int8 NOT NULL DEFAULT nextval('noteverso_project_id_seq'::regclass),
  "name" varchar(120) COLLATE "pg_catalog"."default" NOT NULL,
  "color" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
  "is_favorite" bool NOT NULL,
  "is_deleted" bool DEFAULT false,
  "is_archived" bool DEFAULT false,
  "is_shared" bool DEFAULT false,
  "is_inbox_project" bool DEFAULT false,
  "child_order" int4 NOT NULL,
  "parent_id" int8,
  "view_style" "public"."style" NOT NULL,
  "url" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "collapsed" bool NOT NULL DEFAULT false,
  "added_at" timestamptz(6),
  "updated_at" timestamptz(6)
)
;
ALTER TABLE "public"."noteverso_project" OWNER TO "postgres";
COMMENT ON COLUMN "public"."noteverso_project"."id" IS '项目id';
COMMENT ON COLUMN "public"."noteverso_project"."name" IS '项目名称';
COMMENT ON COLUMN "public"."noteverso_project"."color" IS '项目图标颜色，总共 20种颜色';
COMMENT ON COLUMN "public"."noteverso_project"."is_favorite" IS '是否将项目添加到收藏夹';
COMMENT ON COLUMN "public"."noteverso_project"."is_deleted" IS '是否删除';
COMMENT ON COLUMN "public"."noteverso_project"."is_archived" IS '是否归档';
COMMENT ON COLUMN "public"."noteverso_project"."is_shared" IS '是否分享项目';
COMMENT ON COLUMN "public"."noteverso_project"."is_inbox_project" IS '是否为收件箱项目，只读';
COMMENT ON COLUMN "public"."noteverso_project"."child_order" IS '在客户端侧边栏菜单同一父项目中的位置';
COMMENT ON COLUMN "public"."noteverso_project"."parent_id" IS '父项目id，null 表示此项目为根（父）项目';
COMMENT ON COLUMN "public"."noteverso_project"."view_style" IS '客户端笔记展示布局方式 list - 列表，board - 看板';
COMMENT ON COLUMN "public"."noteverso_project"."url" IS '项目链接，在web、移动端应用可通过链接进入项目';
COMMENT ON COLUMN "public"."noteverso_project"."collapsed" IS '项目菜单是否折叠';
COMMENT ON COLUMN "public"."noteverso_project"."added_at" IS '添加时间';
COMMENT ON COLUMN "public"."noteverso_project"."updated_at" IS '更新时间';
COMMENT ON TABLE "public"."noteverso_project" IS '项目表，最多支持 500 个项目';

-- ----------------------------
-- Records of noteverso_project
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Primary Key structure for table noteverso_project
-- ----------------------------
ALTER TABLE "public"."noteverso_project" ADD CONSTRAINT "noteverso_project_pkey" PRIMARY KEY ("id");
