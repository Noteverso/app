-- create noteverso database
CREATE DATABASE IF NOT EXISTS noteverso;
\c noteverso;

-- create noteverso project table
CREATE TYPE style AS ENUM ('list', 'board');
CREATE TABLE IF NOT EXISTS noteverso_note (
    id bigserial NOT NULL,
    note_type smallint NOT NULL,
    content text NOT NULL,
    is_top boolean DEFAULT false,
    is_deleted boolean DEFAULT false,
    is_archived boolean DEFAULT false,
    comment_count bigint DEFAULT NULL,
    linked_note_count bigint DEFAULT NULL,
    project_id bigint NOT NULL,
    labels jsonb,
    status smallint NOT NULL,
    creator_id bigint NOT NULL,
    attachment jsonb DEFAULT NULL,
    url varchar(100) NOT NULL,
    added_at timestamptz DEFAULT NULL,
    updated_at timestamptz DEFAULT NULL,
    PRIMARY KEY (id)
);

COMMENT ON TABLE noteverso_project IS '项目表，最多支持 500 个项目';

COMMENT ON COLUMN noteverso_project.id IS '项目id';
COMMENT ON COLUMN noteverso_project.name IS '项目名称';
COMMENT ON COLUMN noteverso_project.color IS '项目图标颜色，总共 20种颜色';
COMMENT ON COLUMN noteverso_project.is_favorite IS '是否将项目添加到收藏夹';
COMMENT ON COLUMN noteverso_project.is_deleted IS '是否删除';
COMMENT ON COLUMN noteverso_project.is_archived IS '是否归档';
COMMENT ON COLUMN noteverso_project.is_shared IS '是否分享项目';
COMMENT ON COLUMN noteverso_project.is_inbox_project IS '是否为收件箱项目，只读';
COMMENT ON COLUMN noteverso_project.child_order IS '在客户端侧边栏菜单同一父项目中的位置';
COMMENT ON COLUMN noteverso_project.parent_id IS '父项目id，null 表示此项目为根（父）项目';
COMMENT ON COLUMN noteverso_project.view_style IS '客户端笔记展示布局方式 list - 列表，board - 看板';
COMMENT ON COLUMN noteverso_project.url IS '项目链接，在web、移动端应用可通过链接进入项目';
COMMENT ON COLUMN noteverso_project.collapsed IS '项目菜单是否折叠';
COMMENT ON COLUMN noteverso_project.added_at IS '添加时间';
COMMENT ON COLUMN noteverso_project.updated_at IS '更新时间';

-- create noteverso note table
CREATE TABLE IF NOT EXISTS noteverso_label (
    id bigserial NOT NULL,
    name varchar(60) NOT NULL,
    color varchar(20) NOT NULL,
    is_deleted boolean DEFAULT false,
    added_at timestamptz DEFAULT NULL,
    updated_at timestamptz DEFAULT NULL,
    creator_id bigint NOT NULL,
    is_favorite boolean DEFAULT false,
    order_value bigint NOT NULL,
    PRIMARY KEY (id)
);

COMMENT ON TABLE noteverso_note IS '笔记表';

COMMENT ON COLUMN noteverso_note.id IS '笔记id';
COMMENT ON COLUMN noteverso_note.note_type IS '笔记类型，0-普通笔记 1-账户密码 2-待办清单 3-图表 4-日程 5-工具清单 6-记账(订阅信息，可自动更新续费信息)';
COMMENT ON COLUMN noteverso_note.content IS '笔记内容';
COMMENT ON COLUMN noteverso_note.is_top IS '是否将项目添加到收藏夹';
COMMENT ON COLUMN noteverso_note.is_deleted IS '是否删除';
COMMENT ON COLUMN noteverso_note.is_archived IS '是否归档';
COMMENT ON COLUMN noteverso_note.comment_count IS '笔记评论数';
COMMENT ON COLUMN noteverso_note.linked_note_count IS '关联至此的笔记数量';
COMMENT ON COLUMN noteverso_note.project_id IS '笔记所属的项目id';
COMMENT ON COLUMN noteverso_note.labels IS '笔记标签，字符串数组';
COMMENT ON COLUMN noteverso_note.status IS '笔记状态 0 -  待处理，1 - 正在进行，2 - 已完成';
COMMENT ON COLUMN noteverso_note.creator_id IS '创建人';
COMMENT ON COLUMN noteverso_note.attachment IS '附件对象，内容包括附件名称、附件类型、附件链接和资源类型';
COMMENT ON COLUMN noteverso_note.url IS '笔记链接，从 web、移动端应用通过链接进入笔记';
COMMENT ON COLUMN noteverso_note.added_at IS '添加时间';
COMMENT ON COLUMN noteverso_note.updated_at IS '更新时间';

-- creat noteverso label table
CREATE TABLE IF NOT EXISTS noteverso_project (
                                                 id bigserial NOT NULL,
                                                 name varchar(120) NOT NULL,
    color varchar(20) NOT NULL,
    is_favorite boolean NOT NULL,
    is_deleted boolean DEFAULT false,
    is_archived boolean DEFAULT false,
    is_shared boolean DEFAULT false,
    is_inbox_project boolean DEFAULT false,
    child_order bigint NOT NULL,
    parent_id bigint,
    view_style style NOT NULL,
    url varchar(100) NOT NULL,
    collapsed boolean NOT NULL DEFAULT false,
    added_at timestamp with time zone DEFAULT NULL,
    updated_at timestamp with time zone DEFAULT NULL,
                             PRIMARY KEY (id)
    );

COMMENT ON TABLE noteverso_label IS '标签表';

COMMENT ON COLUMN noteverso_label.id IS '标签id';
COMMENT ON COLUMN noteverso_label.name IS '标签名称';
COMMENT ON COLUMN noteverso_label.color IS '标签图标颜色，总共20种颜色';
COMMENT ON COLUMN noteverso_label.is_deleted IS '是否删除';
COMMENT ON COLUMN noteverso_label.added_at IS '添加时间';
COMMENT ON COLUMN noteverso_label.updated_at IS '更新时间';
COMMENT ON COLUMN noteverso_label.creator_id IS '创建人';
COMMENT ON COLUMN noteverso_label.is_favorite IS '是否将标签添加到收藏夹';
COMMENT ON COLUMN noteverso_label.order_value IS '标签排序';

-- create noteverso note_link table
-- column names are
-- id, note_id, linked_note_id, added_at, is_deleted, view_style
CREATE TABLE IF NOT EXISTS noteverso_note_link (
    id bigserial NOT NULL,
    note_id bigint NOT NULL,
    linked_note_id bigint NOT NULL,
    added_at timestamptz DEFAULT NULL,
    is_deleted boolean DEFAULT false,
    view_style style NOT NULL,
    PRIMARY KEY (id)
);

COMMENT ON TABLE noteverso_note_link IS '笔记关联表';

COMMENT ON COLUMN noteverso_note_link.id IS '笔记关联id';
COMMENT ON COLUMN noteverso_note_link.note_id IS '笔记id';
COMMENT ON COLUMN noteverso_note_link.linked_note_id IS '关联至此的笔记id';
COMMENT ON COLUMN noteverso_note_link.added_at IS '添加时间';
COMMENT ON COLUMN noteverso_note_link.is_deleted IS '是否删除';
COMMENT ON COLUMN noteverso_note_link.view_style IS '关联笔记UI布局方式 list - 列表，board - 看板';

-- create noteverso view option table
-- column names are
-- id, type, note_id, project_id, label_id, view_mode, grouped_by, sorted_by,
-- sort_order, show_archived_notes, is_deleted, filtered_by, added_at, update_at
CREATE TYPE order_type AS ENUM ('ASC', 'DESC');
CREATE TABLE IF NOT EXISTS noteverso_view_option (
    id bigserial NOT NULL,
    type smallint NOT NULL,
    note_id bigint,
    project_id bigint,
    label_id bigint,
    view_mode style NOT NULL,
    grouped_by smallint DEFAULT NULL,
    sorted_by smallint NOT NULL DEFAULT 0,
    sort_order order_type NOT NULL DEFAULT 'ASC',
    show_archived_notes boolean NOT NULL DEFAULT false,
    is_deleted boolean DEFAULT false,
    filtered_by smallint DEFAULT NULL,
    added_at timestamptz DEFAULT NULL,
    update_at timestamptz DEFAULT NULL,
    PRIMARY KEY (id)
);

COMMENT ON TABLE noteverso_view_option IS '视图选项表';

COMMENT ON COLUMN noteverso_view_option.id IS '视图选项id';
COMMENT ON COLUMN noteverso_view_option.type IS '视图选项类型 0 - PROJECT, 1 - LABEL, 2 - UNCOMING, 3 - PAST, 4 - TODAY, 5 - LINKED_NOTE';
COMMENT ON COLUMN noteverso_view_option.note_id IS '笔记id';
COMMENT ON COLUMN noteverso_view_option.project_id IS '项目id';
COMMENT ON COLUMN noteverso_view_option.label_id IS '标签id';
COMMENT ON COLUMN noteverso_view_option.view_mode IS '笔记布局 list - 列表，board - 看板';
COMMENT ON COLUMN noteverso_view_option.grouped_by IS '分组方式 0 - NOTE_STATUS,1 - ADDED_DATE,2 - NOTE_LABEL';
COMMENT ON COLUMN noteverso_view_option.sorted_by IS '排序方式 0 - ADDED_DATE,1 - COMMENT_COUNT,2 - LINKED_NOTE_COUNT';
COMMENT ON COLUMN noteverso_view_option.sort_order IS '排序规则 ASC,DESC';
COMMENT ON COLUMN noteverso_view_option.show_archived_notes IS '是否显示已归档笔记';
COMMENT ON COLUMN noteverso_view_option.is_deleted IS '是否删除';
COMMENT ON COLUMN noteverso_view_option.filtered_by IS '过滤方式';
COMMENT ON COLUMN noteverso_view_option.added_at IS '添加时间';
COMMENT ON COLUMN noteverso_view_option.update_at IS '更新时间';

-- create noteverso user
-- column names are
-- id, inbox_project_id, avatar, email, full_name, has_password, password,
-- token, lang, is_premium, premium_status, premium_until, joined_at,
-- start_page, theme_id, tz_info, daily_goal, time_format, date_format, is_deleted
CREATE TABLE IF NOT EXISTS noteverso_user (
    id bigserial NOT NULL,
    inbox_project_id bigint NOT NULL,
    avatar jsonb DEFAULT NULL,
    email varchar(50) NOT NULL,
    full_name varchar(20) DEFAULT NULL,
    has_password boolean NOT NULL DEFAULT false,
    password varchar(80) DEFAULT NULL,
    token varchar(50) DEFAULT NULL,
    lang smallint NULL,
    is_premium boolean NOT NULL DEFAULT false,
    premium_status smallint NOT NULL,
    premium_until timestamptz DEFAULT NULL,
    joined_at timestamptz NOT NULL,
    start_page varchar(30) NOT NULL,
    theme_id smallint DEFAULT NULL,
    tz_info jsonb DEFAULT NULL,
    daily_goal bigint DEFAULT NULL,
    time_format smallint NOT NULL,
    date_format smallint NOT NULL,
    is_deleted boolean DEFAULT false,
    PRIMARY KEY (id)
);

COMMENT ON TABLE noteverso_user IS '用户表';

COMMENT ON COLUMN noteverso_user.id IS '用户id';
COMMENT ON COLUMN noteverso_user.inbox_project_id IS '收件箱项目id';
COMMENT ON COLUMN noteverso_user.avatar IS '头像';
COMMENT ON COLUMN noteverso_user.email IS '邮箱';
COMMENT ON COLUMN noteverso_user.full_name IS '昵称';
COMMENT ON COLUMN noteverso_user.has_password IS '是否有密码';
COMMENT ON COLUMN noteverso_user.password IS '密码';
COMMENT ON COLUMN noteverso_user.token IS 'token';
COMMENT ON COLUMN noteverso_user.lang IS '语言, 0 - zh-cn,1 - en-us';
COMMENT ON COLUMN noteverso_user.is_premium IS '是否是付费用户';
COMMENT ON COLUMN noteverso_user.premium_status IS '付费用户状态 0 - not_premium, 1 - premium';
COMMENT ON COLUMN noteverso_user.premium_until IS '付费用户到期时间';
COMMENT ON COLUMN noteverso_user.joined_at IS '加入时间';
COMMENT ON COLUMN noteverso_user.start_page IS '用户首次登陆应用后的定位页面，project?id=${project_id}, upcoming, label?name=${label_name}';
COMMENT ON COLUMN noteverso_user.theme_id IS '主题id';
COMMENT ON COLUMN noteverso_user.tz_info IS '时区信息 gmt_string, hours, is_dst, minutes, timezone';
COMMENT ON COLUMN noteverso_user.daily_goal IS '日常目标';
COMMENT ON COLUMN noteverso_user.time_format IS '时间格式 0 - 13:00，1 - 1:00pm，默认 0';
COMMENT ON COLUMN noteverso_user.date_format IS '日期格式 0 - YYYY-MM-DD，1 - DD-MM-YYYY，默认 0';
COMMENT ON COLUMN noteverso_user.is_deleted IS '是否删除';

-- create noteverso setting
-- column names are
-- id, user_id, note, quick_add, navigation
CREATE TABLE IF NOT EXISTS noteverso_setting (
  id bigserial NOT NULL,
  user_id bigint NOT NULL,
  note jsonb DEFAULT NULL,
  quick_add jsonb DEFAULT NULL,
  navigation jsonb DEFAULT NULL,
  PRIMARY KEY (id)
);

COMMENT ON TABLE noteverso_setting IS '用户设置模块';

COMMENT ON COLUMN noteverso_setting.id IS 'id';
COMMENT ON COLUMN noteverso_setting.user_id IS '用户id';
COMMENT ON COLUMN noteverso_setting.note IS '笔记详情展示情况设置';
COMMENT ON COLUMN noteverso_setting.quick_add IS '编辑器设置';
COMMENT ON COLUMN noteverso_setting.navigation IS '导航设置';
