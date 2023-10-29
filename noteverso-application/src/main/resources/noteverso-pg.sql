-- create noteverso database
-- CREATE DATABASE noteverso;
-- \c noteverso;

-- create noteverso project table
CREATE TYPE style AS ENUM ('list', 'board');
CREATE TABLE IF NOT EXISTS noteverso_note (
    id bigserial NOT NULL,
    note_id varchar NOT NULL CONSTRAINT noteverso_note_pk UNIQUE,
    note_type smallint NOT NULL,
    content text NOT NULL,
    is_top boolean DEFAULT false,
    is_deleted boolean DEFAULT false,
    is_archived boolean DEFAULT false,
    project_id varchar(50) NOT NULL,
    status smallint NOT NULL,
    creator bigint NOT NULL,
    updater bigint NOT NULL,
    url varchar(100) NOT NULL,
    added_at timestamptz DEFAULT NULL,
    updated_at timestamptz DEFAULT NULL,
    PRIMARY KEY (id)
);
COMMENT ON TABLE noteverso_note IS '笔记表';
COMMENT ON COLUMN noteverso_note.id IS '笔记id';
COMMENT ON COLUMN noteverso_note.note_id IS '笔记唯一标识，snowFlake id';
COMMENT ON CONSTRAINT noteverso_note_pk ON noteverso_note IS 'UNIQUE (note_id)';
COMMENT ON COLUMN noteverso_note.note_type IS '笔记类型，0-普通笔记 1-账户密码 2-待办清单 3-图表 4-日程 5-工具清单 6-记账(订阅信息，可自动更新续费信息)';
COMMENT ON COLUMN noteverso_note.content IS '笔记内容';
COMMENT ON COLUMN noteverso_note.is_top IS '是否将项目添加到收藏夹';
COMMENT ON COLUMN noteverso_note.is_deleted IS '是否删除';
COMMENT ON COLUMN noteverso_note.is_archived IS '是否归档';
COMMENT ON COLUMN noteverso_note.project_id IS '笔记所属的项目id';
COMMENT ON COLUMN noteverso_note.status IS '笔记状态 0 -  待处理，1 - 正在进行，2 - 已完成';
COMMENT ON COLUMN noteverso_note.creator IS '创建人';
COMMENT ON COLUMN noteverso_note.updater IS '更新人';
COMMENT ON COLUMN noteverso_note.url IS '笔记链接，从 web、移动端应用通过链接进入笔记';
COMMENT ON COLUMN noteverso_note.added_at IS '添加时间';
COMMENT ON COLUMN noteverso_note.updated_at IS '更新时间';

CREATE TABLE IF NOT EXISTS noteverso_note_map (
    id bigserial NOT NULL,
    note_id varchar(50) NOT NULL,
    linked_note_id varchar(50) NOT NULL,
    added_at timestamptz DEFAULT NULL,
    updated_at timestamptz DEFAULT NULL,
    view_style style NOT NULL,
    PRIMARY KEY (id)
);

COMMENT ON TABLE noteverso_note_map IS '笔记关联表';
COMMENT ON COLUMN noteverso_note_map.id IS '笔记关联id';
COMMENT ON COLUMN noteverso_note_map.note_id IS '笔记id';
COMMENT ON COLUMN noteverso_note_map.linked_note_id IS '关联至此的笔记id';
COMMENT ON COLUMN noteverso_note_map.added_at IS '添加时间';
COMMENT ON COLUMN noteverso_note_map.view_style IS '关联笔记UI布局方式 list - 列表，board - 看板';

-- create noteverso note table
CREATE TABLE IF NOT EXISTS noteverso_label (
    id bigserial NOT NULL,
    label_id varchar(50) NOT NULL CONSTRAINT noteverso_label_pk UNIQUE,
    name varchar(60) NOT NULL CONSTRAINT noteverso_label_name_pk UNIQUE,
    color varchar(20) NOT NULL,
    added_at timestamptz DEFAULT NULL,
    updated_at timestamptz DEFAULT NULL,
    creator bigint NOT NULL,
    updater bigint NOT NULL,
    is_favorite boolean DEFAULT false,
    order_value bigint NOT NULL,
    PRIMARY KEY (id)
);

COMMENT ON TABLE noteverso_label IS '标签表';
COMMENT ON COLUMN noteverso_label.label_id IS '标签唯一标识，snowFlake id';
COMMENT ON CONSTRAINT noteverso_label_pk ON noteverso_label IS 'UNIQUE (label_id)';
COMMENT ON CONSTRAINT noteverso_label_name_pk ON noteverso_label IS 'UNIQUE (name)';
COMMENT ON COLUMN noteverso_label.id IS '标签id';
COMMENT ON COLUMN noteverso_label.name IS '标签名称';
COMMENT ON COLUMN noteverso_label.color IS '标签图标颜色，总共20种颜色';
COMMENT ON COLUMN noteverso_label.added_at IS '添加时间';
COMMENT ON COLUMN noteverso_label.updated_at IS '更新时间';
COMMENT ON COLUMN noteverso_label.creator IS '创建人';
COMMENT ON COLUMN noteverso_label.is_favorite IS '是否将标签添加到收藏夹';
COMMENT ON COLUMN noteverso_label.order_value IS '标签排序';


CREATE TABLE IF NOT EXISTS noteverso_note_label_map (
   id bigserial NOT NULL,
   note_id varchar(50) NOT NULL,
   label_id varchar(50) NOT NULL,
   added_at timestamptz DEFAULT NULL,
   updated_at timestamptz DEFAULT NULL,
   PRIMARY KEY (id)
);

COMMENT ON TABLE noteverso_note_label_map IS '标签关联表';
COMMENT ON COLUMN noteverso_note_label_map.id IS '标签关联id';
COMMENT ON COLUMN noteverso_note_label_map.note_id IS '笔记id';
COMMENT ON COLUMN noteverso_note_label_map.label_id IS '标签id';

-- creat noteverso label table
CREATE TABLE IF NOT EXISTS noteverso_project (
    id bigserial NOT NULL,
    project_id varchar(50) NOT NULL CONSTRAINT noteverso_project_pk UNIQUE,
    name varchar(120) NOT NULL,
    color varchar(20) NOT NULL,
    is_favorite boolean NOT NULL,
    is_archived boolean DEFAULT false,
    is_shared boolean DEFAULT false,
    child_order bigint NOT NULL,
    parent_id bigint,
    view_style style NOT NULL,
    url varchar(100) NOT NULL,
    collapsed boolean NOT NULL DEFAULT false,
    added_at timestamp with time zone DEFAULT NULL,
    updated_at timestamp with time zone DEFAULT NULL,
    creator bigint NOT NULL,
    updater bigint NOT NULL,
    PRIMARY KEY (id)
);
COMMENT ON TABLE noteverso_project IS '项目表，最多支持 500 个项目';
COMMENT ON COLUMN noteverso_project.id IS '项目id';
COMMENT ON COLUMN noteverso_project.project_id IS '项目唯一标识，snowFlake id';
COMMENT ON CONSTRAINT noteverso_project_pk ON noteverso_project IS 'UNIQUE (project_id)';
COMMENT ON COLUMN noteverso_project.name IS '项目名称';
COMMENT ON COLUMN noteverso_project.color IS '项目图标颜色，总共 20种颜色';
COMMENT ON COLUMN noteverso_project.is_favorite IS '是否将项目添加到收藏夹';
COMMENT ON COLUMN noteverso_project.is_archived IS '是否归档';
COMMENT ON COLUMN noteverso_project.is_shared IS '是否分享项目';
COMMENT ON COLUMN noteverso_project.child_order IS '在客户端侧边栏菜单同一父项目中的位置';
COMMENT ON COLUMN noteverso_project.parent_id IS '父项目id，null 表示此项目为根（父）项目';
COMMENT ON COLUMN noteverso_project.view_style IS '客户端笔记展示布局方式 list - 列表，board - 看板';
COMMENT ON COLUMN noteverso_project.url IS '项目链接，在web、移动端应用可通过链接进入项目';
COMMENT ON COLUMN noteverso_project.collapsed IS '项目菜单是否折叠';
COMMENT ON COLUMN noteverso_project.added_at IS '添加时间';
COMMENT ON COLUMN noteverso_project.updated_at IS '更新时间';

CREATE TABLE IF NOT EXISTS noteverso_note_project_map (
    id bigserial NOT NULL,
    note_id varchar(50) NOT NULL,
    project_id varchar(50) NOT NULL,
    added_at timestamptz DEFAULT NULL,
    updated_at timestamptz DEFAULT NULL,
    PRIMARY KEY (id)
);
COMMENT ON TABLE noteverso_note_project_map IS '项目关联表';
COMMENT ON COLUMN noteverso_note_project_map.id IS '项目关联id';
COMMENT ON COLUMN noteverso_note_project_map.note_id IS '笔记id';
COMMENT ON COLUMN noteverso_note_project_map.project_id IS '项目id';

CREATE TABLE IF NOT EXISTS noteverso_comment (
    id bigserial NOT NULL,
    comment_id varchar(50) NOT NULL CONSTRAINT noteverso_comment_pk UNIQUE,
    content text DEFAULT NULL,
    note_id varchar(50) DEFAULT NULL,
    project_id varchar(50) DEFAULT NULL,
    creator bigint DEFAULT NULL,
    updater bigint DEFAULT NULL,
    added_at timestamptz DEFAULT NULL,
    updated_at timestamptz DEFAULT NULL,
    PRIMARY KEY (id)
);
COMMENT ON TABLE noteverso_comment IS '评论表';
COMMENT ON COLUMN noteverso_comment.id IS '评论id';
COMMENT ON COLUMN noteverso_comment.comment_id IS '评论唯一标识，snowFlake id';
COMMENT ON CONSTRAINT noteverso_comment_pk ON noteverso_comment IS 'UNIQUE (comment_id)';
COMMENT ON COLUMN noteverso_comment.content IS '评论内容，可以为空';
COMMENT ON COLUMN noteverso_comment.note_id IS '笔记id，如果评论属于项目，则为 null';
COMMENT ON COLUMN noteverso_comment.project_id IS '项目id，如果评论属于笔记，则它为 null';

CREATE TABLE IF NOT EXISTS noteverso_attachment (
    id bigserial NOT NULL,
    name varchar(128) NOT NULL,
    type varchar(25) NOT NULL,
    url varchar(255) NOT NULL,
    resource_type varchar(10) NOT NULL,
    note_id varchar(50) DEFAULT NULL,
    project_id varchar(50) DEFAULT NULL,
    comment_id varchar(50) DEFAULT NULL,
    creator bigint DEFAULT NULL,
    updater bigint DEFAULT NULL,
    added_at timestamptz DEFAULT NULL,
    updated_at timestamptz DEFAULT NULL,
    PRIMARY KEY (id)
);

COMMENT ON TABLE noteverso_attachment IS '附件表';
COMMENT ON COLUMN noteverso_attachment.id IS '附件id';
COMMENT ON COLUMN noteverso_attachment.name IS '附件名称';
COMMENT ON COLUMN noteverso_attachment.type IS '附件类型，MIME type，如 video/*, audio/*, image/*';
COMMENT ON COLUMN noteverso_attachment.url IS '附件链接';
COMMENT ON COLUMN noteverso_attachment.resource_type IS '附件资源类型，image - 图片，file - 文件';
COMMENT ON COLUMN noteverso_attachment.note_id IS '笔记id，如果附件属于项目或评论，则为 null';
COMMENT ON COLUMN noteverso_attachment.project_id IS '项目id，如果附件属于笔记或评论，则它为 null';
COMMENT ON COLUMN noteverso_attachment.comment_id IS '评论id，如果附件属于笔记或项目，则它为 null';
COMMENT ON COLUMN noteverso_attachment.creator IS '创建人';
COMMENT ON COLUMN noteverso_attachment.updater IS '更新人';
COMMENT ON COLUMN noteverso_attachment.added_at IS '添加时间';
COMMENT ON COLUMN noteverso_attachment.updated_at IS '更新时间';

-- create noteverso view option table
-- column names are
-- id, type, note_id, project_id, label_id, view_mode, grouped_by, sorted_by,
-- sort_order, show_archived_notes, filtered_by, added_at, update_at
CREATE TYPE order_type AS ENUM ('ASC', 'DESC');
CREATE TABLE IF NOT EXISTS noteverso_view_option (
    id bigserial NOT NULL,
    type smallint NOT NULL,
    note_id varchar(50) DEFAULT NULL,
    project_id varchar(50) DEFAULT NULL,
    label_id varchar(50) DEFAULT NULL,
    view_mode style NOT NULL,
    grouped_by smallint DEFAULT NULL,
    sorted_by smallint NOT NULL DEFAULT 0,
    sort_order order_type NOT NULL DEFAULT 'ASC',
    show_archived_notes boolean NOT NULL DEFAULT false,
    filtered_by smallint DEFAULT NULL,
    added_at timestamptz DEFAULT NULL,
    update_at timestamptz DEFAULT NULL,
    creator bigint NOT NULL,
    updater bigint NOT NULL,
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
COMMENT ON COLUMN noteverso_view_option.filtered_by IS '过滤方式';
COMMENT ON COLUMN noteverso_view_option.added_at IS '添加时间';
COMMENT ON COLUMN noteverso_view_option.update_at IS '更新时间';

-- create noteverso user
-- column names are
-- id, inbox_project_id, avatar, email, full_name, has_password, password,
-- token, lang, is_premium, premium_status, premium_until, joined_at,
-- start_page, theme_id, tz_info, daily_goal, time_format, date_format
CREATE TABLE IF NOT EXISTS noteverso_user (
    id bigserial NOT NULL,
    inbox_project_id varchar(50) DEFAULT NULL,
    avatar jsonb DEFAULT NULL,
    username varchar(50) NOT NULL constraint noteverso_username_pk unique,
    email varchar(50) NOT NULL,
    full_name varchar(20) DEFAULT NULL,
    has_password boolean DEFAULT false,
    password varchar(80) NOT NULL,
    lang smallint NULL,
    is_premium boolean DEFAULT false,
    premium_status smallint default 0,
    premium_until timestamptz DEFAULT NULL,
    authority varchar(20) DEFAULT 'normal',
    joined_at timestamptz NOT NULL,
    start_page varchar(30) DEFAULT NULL,
    theme_id smallint DEFAULT NULL,
    tz_info jsonb DEFAULT NULL,
    daily_goal bigint DEFAULT NULL,
    time_format smallint DEFAULT NULL,
    date_format smallint DEFAULT NULL,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    PRIMARY KEY (id)
);

COMMENT ON TABLE noteverso_user IS '用户表';
COMMENT ON COLUMN noteverso_user.id IS '用户id';
COMMENT ON COLUMN noteverso_user.inbox_project_id IS '收件箱项目id';
COMMENT ON COLUMN noteverso_user.avatar IS '头像';
COMMENT ON COLUMN noteverso_user.username IS '用户名称';

COMMENT ON CONSTRAINT noteverso_username_pk ON noteverso_user IS 'email 唯一';

COMMENT ON COLUMN noteverso_user.email IS '邮箱';
COMMENT ON COLUMN noteverso_user.full_name IS '昵称';
COMMENT ON COLUMN noteverso_user.has_password IS '是否有密码';
COMMENT ON COLUMN noteverso_user.password IS '密码';
COMMENT ON COLUMN noteverso_user.lang IS '语言, 0 - zh-cn,1 - en-us';
COMMENT ON COLUMN noteverso_user.is_premium IS '是否是付费用户';
COMMENT ON COLUMN noteverso_user.premium_status IS '付费用户状态 0 - not_premium, 1 - premium';
COMMENT ON COLUMN noteverso_user.premium_until IS '付费用户到期时间';
COMMENT ON COLUMN noteverso_user.authority IS '权限，premium - 会员，normal - 普通用户';
COMMENT ON COLUMN noteverso_user.joined_at IS '加入时间';
COMMENT ON COLUMN noteverso_user.start_page IS '用户首次登陆应用后的定位页面，project?id=${project_id}, upcoming, label?name=${label_name}';
COMMENT ON COLUMN noteverso_user.theme_id IS '主题id';
COMMENT ON COLUMN noteverso_user.tz_info IS '时区信息 gmt_string, hours, is_dst, minutes, timezone';
COMMENT ON COLUMN noteverso_user.daily_goal IS '日常目标';
COMMENT ON COLUMN noteverso_user.time_format IS '时间格式 0 - 13:00，1 - 1:00pm，默认 0';
COMMENT ON COLUMN noteverso_user.date_format IS '日期格式 0 - YYYY-MM-DD，1 - DD-MM-YYYY，默认 0';

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
