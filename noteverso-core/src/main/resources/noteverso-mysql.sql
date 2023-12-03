-- create noteverso database
CREATE DATABASE IF NOT EXISTS noteverso;
USE noteverso;

-- create noteverso project table
CREATE TABLE IF NOT EXISTS `noteverso_project` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '项目id',
  `name` varchar(120) NOT NULL COMMENT '项目名称',
  `color` varchar(20) NOT NULL COMMENT '项目图标颜色，总共 20种颜色',
  `is_favorite` tinyint(1) NOT NULL COMMENT '是否将项目添加到收藏夹',
	`is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
	`is_archived` tinyint(1) DEFAULT '0' COMMENT '是否归档',
	`is_shared` tinyint(1)  DEFAULT '0' COMMENT '是否分享项目',
	`is_inbox_project` tinyint(1) DEFAULT '0' COMMENT '是否为收件箱项目，只读',
	`child_order` smallint UNSIGNED NOT NULL COMMENT '在客户端侧边栏菜单同一父项目中的位置',
	`parent_id` bigint UNSIGNED COMMENT '父项目id，null 表示此项目为根（父）项目',
	`view_style` enum('list', 'board') NOT NULL COMMENT '客户端笔记展示布局方式 list - 列表，board - 看板',
	`url` varchar(50) NOT NULL COMMENT '项目链接，在web、移动端应用可通过链接进入项目',
	`collapsed` tinyint(1) NOT NULL DEFAULT '0' COMMENT '项目菜单是否折叠',
	`added_at` datetime DEFAULT NULL COMMENT '添加时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目表，最多支持 500 个项目';

-- create noteverso note table
CREATE TABLE IF NOT EXISTS `noteverso_note` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '笔记id',
  `note_type` tinyint UNSIGNED NOT NULL COMMENT '笔记类型，0-普通笔记 1-账户密码 2-待办清单 3-图表 4-日程 5-工具清单 6-记账(订阅信息，可自动更新续费信息)',
  `content` text NOT NULL COMMENT '笔记内容',
  `is_top` tinyint(1) DEFAULT '0' COMMENT '是否将项目添加到收藏夹',
	`is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
	`is_archived` tinyint(1) DEFAULT '0' COMMENT '是否归档',
	`comment_count` smallint DEFAULT null COMMENT '笔记评论数',
	`linked_note_count` tinyint DEFAULT null COMMENT '关联至此的笔记数量',
	`project_id` smallint UNSIGNED NOT NULL COMMENT '笔记所属的项目id',
	`labels` json COMMENT '笔记标签',
	`status` tinyint UNSIGNED NOT NULL COMMENT '笔记状态 0 -  待处理，1 - 正在进行，2 - 已完成',
	`creator_id` bigint UNSIGNED NOT NULL COMMENT '创建人',
	`attachment` json DEFAULT NULL COMMENT '附件对象，内容包括附件名称、附件类型、附件链接和资源类型',
	`url` varchar(50) NOT NULL COMMENT '笔记链接，从 web、移动端应用通过链接进入笔记',
	`added_at` datetime DEFAULT NULL COMMENT '添加时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='笔记表';

-- creat noteverso label table
CREATE TABLE IF NOT EXISTS `noteverso_label` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '标签id',
  `name` varchar(60) NOT NULL COMMENT '标签名称',
  `color` varchar(20) NOT NULL COMMENT '标签图标颜色，总共20种颜色',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  `added_at` datetime DEFAULT NULL COMMENT '添加时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  `creator_id` bigint UNSIGNED NOT NULL COMMENT '创建人',
  `is_favorite` tinyint(1) DEFAULT '0' COMMENT '是否将标签添加到收藏夹',
  `order` smallint UNSIGNED NOT NULL COMMENT '标签排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- create noteverso note_link table
-- column names are
-- id, note_id, linked_note_id, added_at, is_deleted, view_style
CREATE TABLE IF NOT EXISTS `noteverso_note_link` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '笔记关联id',
  `note_id` bigint UNSIGNED NOT NULL COMMENT '笔记id',
  `linked_note_id` bigint UNSIGNED NOT NULL COMMENT '关联至此的笔记id',
  `added_at` datetime DEFAULT NULL COMMENT '添加时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  `view_style` enum('list', 'board') NOT NULL COMMENT '关联笔记UI布局方式 list - 列表，board - 看板',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='笔记关联表';

-- create noteverso view option table
-- column names are
-- id, type, note_id, project_id, label_id, view_mode, grouped_by, sorted_by,
-- sort_order, show_archived_notes, is_deleted, filtered_by, added_at, update_at
CREATE TABLE IF NOT EXISTS `noteverso_view_option` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '视图选项id',
  `type` tinyint NOT NULL COMMENT '视图选项类型 0 - PROJECT, 1 - LABEL, 2 - UNCOMING, 3 - PAST, 4 - TODAY, 5 - LINKED_NOTE',
  `note_id` bigint UNSIGNED COMMENT '笔记id',
  `project_id` smallint UNSIGNED COMMENT '项目id',
  `label_id` smallint UNSIGNED COMMENT '标签id',
  `view_mode` enum('list', 'board') NOT NULL COMMENT '笔记布局 list - 列表，board - 看板',
  `grouped_by` tinyint DEFAULT NULL COMMENT '分组方式 0 - NOTE_STATUS,1 - ADDED_DATE,2 - NOTE_LABEL',
  `sorted_by` tinyint NOT NULL DEFAULT 0 COMMENT '排序方式 0 - ADDED_DATE,1 - COMMENT_COUNT,2 - LINKED_NOTE_COUNT',
  `sort_order` enum('ASC', 'DESC') NOT NULL DEFAULT 'ASC' COMMENT '排序规则 ASC,DESC',
  `show_archived_notes` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否显示已归档笔记',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  `filtered_by` tinyint DEFAULT NULL COMMENT '过滤方式',
  `added_at` datetime DEFAULT NULL COMMENT '添加时间',
  `update_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='视图选项表';

-- create noteverso user
-- column names are
-- id, inbox_project_id, avatar, email, full_name, has_password, password,
-- token, lang, is_premium, premium_status, premium_until, joined_at,
-- start_page, theme_id, tz_info, daily_goal, time_format, date_format, is_deleted
CREATE TABLE IF NOT EXISTS `noteverso_user` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `inbox_project_id` smallint UNSIGNED NOT NULL COMMENT '收件箱项目id',
  `avatar` json DEFAULT NULL COMMENT '头像',
  `email` varchar(50) NOT NULL COMMENT '邮箱',
  `full_name` varchar(20) DEFAULT NULL COMMENT '昵称',
  `has_password` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否有密码',
  `password` varchar(80) DEFAULT NULL COMMENT '密码',
  `token` varchar(50) DEFAULT NULL COMMENT 'token',
  `lang` tinyint NOT NULL COMMENT '语言, 0 - zh-cn,1 - en-us',
  `is_premium` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否是付费用户',
  `premium_status` tinyint UNSIGNED NOT NULL COMMENT '付费用户状态 0 - not_premium, 1 - premium',
  `premium_until` datetime DEFAULT NULL COMMENT '付费用户到期时间',
  `joined_at` datetime NOT NULL COMMENT '加入时间',
  `start_page` varchar(30) NOT NULL COMMENT '用户首次登陆应用后的定位页面，project?id=${project_id}, upcoming, label?name=${label_name}',
  `theme_id` tinyint UNSIGNED DEFAULT NULL COMMENT '主题id',
  `tz_info` json DEFAULT NULL COMMENT '时区信息 gmt_string, hours, is_dst, minutes, timezone',
  `daily_goal` bigint UNSIGNED DEFAULT NULL COMMENT '日常目标',
  `time_format` tinyint UNSIGNED NOT NULL COMMENT '时间格式 0 - 13:00，1 - 1:00pm，默认 0',
  `date_format` tinyint UNSIGNED NOT NULL COMMENT '日期格式 0 - YYYY-MM-DD，1 - DD-MM-YYYY，默认0',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- create noteverso setting
-- column names are
-- id, user_id, note, quick_add, navigation
CREATE TABLE IF NOT EXISTS `noteverso_setting` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户id',
  `note` json DEFAULT NULL COMMENT '笔记详情展示情况设置',
  `quick_add` json DEFAULT NULL COMMENT '编辑器设置',
  `navigation` json DEFAULT NULL COMMENT '导航设置',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户设置模块';

