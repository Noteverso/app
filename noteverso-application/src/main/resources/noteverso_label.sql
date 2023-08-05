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

 Date: 05/08/2023 23:51:53
*/


-- ----------------------------
-- Table structure for noteverso_label
-- ----------------------------
-- DROP TABLE IF EXISTS "public"."noteverso_label";
CREATE TABLE "public"."noteverso_label" (
  "id" int8 NOT NULL DEFAULT nextval('noteverso_label_id_seq'::regclass),
  "name" varchar(60) COLLATE "pg_catalog"."default" NOT NULL,
  "color" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
  "is_deleted" bool DEFAULT false,
  "added_at" timestamptz(6),
  "updated_at" timestamptz(6),
  "creator_id" int8 NOT NULL,
  "is_favorite" bool DEFAULT false,
  "order_value" int8 NOT NULL
)
;
ALTER TABLE "public"."noteverso_label" OWNER TO "postgres";
COMMENT ON COLUMN "public"."noteverso_label"."id" IS '标签id';
COMMENT ON COLUMN "public"."noteverso_label"."name" IS '标签名称';
COMMENT ON COLUMN "public"."noteverso_label"."color" IS '标签图标颜色，总共20种颜色';
COMMENT ON COLUMN "public"."noteverso_label"."is_deleted" IS '是否删除';
COMMENT ON COLUMN "public"."noteverso_label"."added_at" IS '添加时间';
COMMENT ON COLUMN "public"."noteverso_label"."updated_at" IS '更新时间';
COMMENT ON COLUMN "public"."noteverso_label"."creator_id" IS '创建人';
COMMENT ON COLUMN "public"."noteverso_label"."is_favorite" IS '是否将标签添加到收藏夹';
COMMENT ON COLUMN "public"."noteverso_label"."order_value" IS '标签排序';
COMMENT ON TABLE "public"."noteverso_label" IS '标签表';

-- ----------------------------
-- Records of noteverso_label
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Primary Key structure for table noteverso_label
-- ----------------------------
ALTER TABLE "public"."noteverso_label" ADD CONSTRAINT "noteverso_label_pkey" PRIMARY KEY ("id");
