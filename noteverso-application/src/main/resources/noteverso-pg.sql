-- create noteverso database
-- CREATE DATABASE noteverso;

-- create noteverso project table
-- \c noteverso;
CREATE TABLE IF NOT EXISTS noteverso_note (
    id bigserial NOT NULL,
    note_id varchar NOT NULL CONSTRAINT noteverso_note_pk UNIQUE,
    note_type smallint DEFAULT 0,
    content varchar NOT NULL,
    is_pin smallint DEFAULT 0,
    is_deleted smallint DEFAULT 0,
    is_archived smallint DEFAULT 0,
    is_favorite smallint DEFAULT 0,
    project_id varchar(50) NOT NULL,
    status smallint DEFAULT 1,
    creator varchar(50) NOT NULL,
    updater varchar(50) NOT NULL,
    url varchar(100) DEFAULT NULL,
    added_at timestamptz DEFAULT NULL,
    updated_at timestamptz DEFAULT NULL,
    PRIMARY KEY (id)
);
COMMENT ON TABLE noteverso_note IS '笔记表';
COMMENT ON COLUMN noteverso_note.id IS 'id';
COMMENT ON COLUMN noteverso_note.note_id IS '笔记唯一标识，snowFlake id';
COMMENT ON CONSTRAINT noteverso_note_pk ON noteverso_note IS 'UNIQUE (note_id)';
COMMENT ON COLUMN noteverso_note.note_type IS '笔记类型，0-普通笔记 1-账户密码 2-待办清单 3-图表 4-日程 5-工具清单 6-记账(订阅信息，可自动更新续费信息)';
COMMENT ON COLUMN noteverso_note.content IS '笔记内容';
COMMENT ON COLUMN noteverso_note.is_pin IS '是否将项目置顶,0-否，1-是';
COMMENT ON COLUMN noteverso_note.is_favorite IS '是否收藏项目, 0-否，1-是';
COMMENT ON COLUMN noteverso_note.is_deleted IS '是否删除,0-否，1-是';
COMMENT ON COLUMN noteverso_note.is_archived IS '是否归档,0-否，1-是';
COMMENT ON COLUMN noteverso_note.project_id IS '笔记所属的项目id';
COMMENT ON COLUMN noteverso_note.status IS '笔记状态 0 - 待处理，1 - 正在进行，2 - 已完成';
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
    view_style smallint DEFAULT 0,
    creator varchar(50) NOT NULL,
    updater varchar(50) NOT NULL,
    is_deleted    smallint    default 0 not null,
    PRIMARY KEY (id)
);

COMMENT ON TABLE noteverso_note_map IS '笔记关联表';
COMMENT ON COLUMN noteverso_note_map.id IS 'id';
COMMENT ON COLUMN noteverso_note_map.note_id IS '笔记id';
COMMENT ON COLUMN noteverso_note_map.linked_note_id IS '关联至此的笔记id';
COMMENT ON COLUMN noteverso_note_map.added_at IS '添加时间';
COMMENT ON COLUMN noteverso_note_map.view_style IS '关联笔记UI布局方式 0 - list 列表，1 - board - 看板';

-- create noteverso note table
CREATE TABLE IF NOT EXISTS noteverso_label (
    id bigserial NOT NULL,
    label_id varchar(50) NOT NULL CONSTRAINT noteverso_label_pk UNIQUE,
    name varchar(60) NOT NULL CONSTRAINT noteverso_label_name_pk UNIQUE,
    color varchar(20) NOT NULL,
    added_at timestamptz DEFAULT NULL,
    updated_at timestamptz DEFAULT NULL,
    creator varchar(50) NOT NULL,
    updater varchar(50) NOT NULL,
    is_favorite smallint DEFAULT 0,
    PRIMARY KEY (id)
);

COMMENT ON TABLE noteverso_label IS '标签表';
COMMENT ON COLUMN noteverso_label.label_id IS '标签唯一标识，snowFlake id';
COMMENT ON CONSTRAINT noteverso_label_pk ON noteverso_label IS 'UNIQUE (label_id)';
COMMENT ON CONSTRAINT noteverso_label_name_pk ON noteverso_label IS 'UNIQUE (name)';
COMMENT ON COLUMN noteverso_label.id IS 'id';
COMMENT ON COLUMN noteverso_label.name IS '标签名称';
COMMENT ON COLUMN noteverso_label.color IS '标签图标颜色，总共20种颜色';
COMMENT ON COLUMN noteverso_label.added_at IS '添加时间';
COMMENT ON COLUMN noteverso_label.updated_at IS '更新时间';
COMMENT ON COLUMN noteverso_label.creator IS '创建人';
COMMENT ON COLUMN noteverso_label.is_favorite IS '是否将标签添加到收藏夹, 0-否，1-是';


CREATE TABLE IF NOT EXISTS noteverso_note_label_map (
   id bigserial NOT NULL,
   note_id varchar(50) NOT NULL,
   label_id varchar(50) NOT NULL,
   added_at timestamptz DEFAULT NULL,
   updated_at timestamptz DEFAULT NULL,
   creator varchar(50) NOT NULL,
   updater varchar(50) NOT NULL,
   is_deleted    smallint    default 0 not null,
   PRIMARY KEY (id)
);

COMMENT ON TABLE noteverso_note_label_map IS '标签关联表';
COMMENT ON COLUMN noteverso_note_label_map.id IS '标签关联id';
COMMENT ON COLUMN noteverso_note_label_map.note_id IS '笔记id';
COMMENT ON COLUMN noteverso_note_label_map.label_id IS '标签id';

-- creat noteverso label table
CREATE TABLE IF NOT EXISTS noteverso_project (
    id bigserial NOT NULL,
    project_id varchar(50) NOT NULL CONSTRAINT uq_project_id UNIQUE,
    name varchar(120) NOT NULL,
    color varchar(20) NOT NULL,
    is_favorite smallint DEFAULT 0,
    is_archived smallint DEFAULT 0,
    is_shared smallint DEFAULT 0,
    is_inbox_project smallint DEFAULT 0,
    child_order bigint NOT NULL,
    parent_id varchar(50) DEFAULT NULL,
    url varchar(100) DEFAULT NULL,
    is_collapsed smallint DEFAULT 0,
    added_at timestamp with time zone DEFAULT NULL,
    updated_at timestamp with time zone DEFAULT NULL,
    creator varchar(50) NOT NULL,
    updater varchar(50) NOT NULL,
    PRIMARY KEY (id)
);
COMMENT ON TABLE noteverso_project IS '项目表';
COMMENT ON COLUMN noteverso_project.id IS 'id';
COMMENT ON COLUMN noteverso_project.project_id IS '项目唯一标识，snowFlake id';
COMMENT ON CONSTRAINT uq_project_id ON noteverso_project IS 'UNIQUE (project_id)';
COMMENT ON COLUMN noteverso_project.name IS '项目名称';
COMMENT ON COLUMN noteverso_project.color IS '项目图标颜色，总共 20种颜色';
COMMENT ON COLUMN noteverso_project.is_favorite IS '是否将项目添加到收藏夹,0-否，1-是';
COMMENT ON COLUMN noteverso_project.is_archived IS '是否归档,0-否，1-是';
COMMENT ON COLUMN noteverso_project.is_shared IS '是否分享项目,0-否，1-是';
COMMENT ON COLUMN noteverso_project.is_inbox_project IS '是否为收件箱项目，0 - 否 1 - 是';
COMMENT ON COLUMN noteverso_project.child_order IS '在客户端侧边栏菜单同一父项目中的位置';
COMMENT ON COLUMN noteverso_project.parent_id IS '父项目id，null 表示此项目为根（父）项目';
COMMENT ON COLUMN noteverso_project.url IS '项目链接，在web、移动端应用可通过链接进入项目';
COMMENT ON COLUMN noteverso_project.is_collapsed IS '项目菜单是否折叠,0-否，1-是';
COMMENT ON COLUMN noteverso_project.added_at IS '添加时间';
COMMENT ON COLUMN noteverso_project.updated_at IS '更新时间';

CREATE TABLE IF NOT EXISTS noteverso_comment (
    id bigserial NOT NULL,
    comment_id varchar(50) NOT NULL CONSTRAINT uq_comment_id UNIQUE,
    content varchar NOT NULL,
    object_id varchar(50) NOT NULL,
    creator varchar(50) DEFAULT NULL,
    updater varchar(50) DEFAULT NULL,
    added_at timestamptz DEFAULT NULL,
    updated_at timestamptz DEFAULT NULL,
    PRIMARY KEY (id)
);
COMMENT ON TABLE noteverso_comment IS '评论表';
COMMENT ON COLUMN noteverso_comment.id IS 'id';
COMMENT ON COLUMN noteverso_comment.comment_id IS '评论唯一标识，snowFlake id';
COMMENT ON CONSTRAINT uq_comment_id ON noteverso_comment IS 'UNIQUE (comment_id)';
COMMENT ON COLUMN noteverso_comment.content IS '评论内容，可以为空';
COMMENT ON COLUMN noteverso_comment.object_id IS '对象id，包括项目和笔记';

CREATE TABLE IF NOT EXISTS noteverso_attachment (
    id bigserial NOT NULL,
    name varchar(128) NOT NULL,
    type varchar(25) NOT NULL,
    url varchar(255) NOT NULL,
    size bigint NOT NULL,
    resource_type smallint NOT NULL,
    attachment_id varchar(50) not null constraint uq_attachment_id unique,
    creator varchar(50) DEFAULT NULL,
    updater varchar(50) DEFAULT NULL,
    added_at timestamptz DEFAULT NULL,
    updated_at timestamptz DEFAULT NULL,
    PRIMARY KEY (id)
);

COMMENT ON TABLE noteverso_attachment IS '附件表';
COMMENT ON COLUMN noteverso_attachment.id IS '附件id';
COMMENT ON COLUMN noteverso_attachment.attachment_id IS '附件唯一标识，snowFlake id';
comment on constraint uq_attachment_id on noteverso_attachment is '附件唯一id';
COMMENT ON COLUMN noteverso_attachment.name IS '附件名称';
COMMENT ON COLUMN noteverso_attachment.type IS '附件类型，MIME type，如 video/*, audio/*, image/*';
COMMENT ON COLUMN noteverso_attachment.size IS '附件大小，单位bytes';
COMMENT ON COLUMN noteverso_attachment.url IS '附件链接';
COMMENT ON COLUMN noteverso_attachment.resource_type IS '附件资源类型，0 - image - 图片，1 - file - 文件';
COMMENT ON COLUMN noteverso_attachment.creator IS '创建人';
COMMENT ON COLUMN noteverso_attachment.updater IS '更新人';
COMMENT ON COLUMN noteverso_attachment.added_at IS '添加时间';
COMMENT ON COLUMN noteverso_attachment.updated_at IS '更新时间';

create table noteverso_attachment_map
(
    id bigserial primary key,
    attachment_id varchar(50) not null,
    object_id     varchar(50) default NULL::character varying,
    added_at      timestamp with time zone,
    updated_at    timestamp with time zone,
    creator       varchar(50)           not null,
    updater       varchar(50)           not null,
    is_deleted    smallint    default 0 not null
);

comment on table noteverso_attachment_map is '项目附件关联表';
comment on column noteverso_attachment_map.id is '项目关联id';
comment on column noteverso_attachment_map.attachment_id is '附件id';
comment on column noteverso_attachment_map.object_id is '对象id，包括项目、评论和笔记';
comment on column noteverso_attachment_map.is_deleted is '0 - 否，1 - 是';

-- create noteverso view option table
create table noteverso_view_option
(
    id bigserial primary key,
    view_type           smallint    default 0,
    object_id           varchar(50) default NULL::character varying,
    view_mode           smallint    default 0,
    grouped_by          smallint,
    ordered_by          smallint    default 0 not null,
    order_value         smallint    default 0 not null,
    show_archived_notes smallint    default 0,
    filtered_by         smallint,
    added_at            timestamp with time zone,
    update_at           timestamp with time zone,
    creator             varchar(50)           not null,
    updater             varchar(50)           not null,
    filter_value        varchar(50)
);

comment on table noteverso_view_option is '视图选项表';
comment on column noteverso_view_option.id is '视图选项id';
comment on column noteverso_view_option.view_type is '视图选项类型 0 - PROJECT, 1 - UPCOMING, 2 - TODAY, 3 - PAST, 4 - ATTACHMENT';
comment on column noteverso_view_option.object_id is '对象id，包括默认为空，如果对象是项目，则为项目id';
comment on column noteverso_view_option.view_mode is '布局 0 - list 列表，1 - board 看板';
comment on column noteverso_view_option.grouped_by is '分组方式 0 - ADDED_AT, 1 - NOTE_STATUS, 2 - NOTE_LABEL';
comment on column noteverso_view_option.ordered_by is '排序方式 0 - ADDED_AT,1 - COMMENT_COUNT,2 - LINKED_NOTE_COUNT';
comment on column noteverso_view_option.order_value is '排序规则 0 - ASC, 1 - DESC';
comment on column noteverso_view_option.show_archived_notes is '是否显示已归档笔记, 0 - 不显示, 1 - 显示';
comment on column noteverso_view_option.filtered_by is '过滤方式';
comment on column noteverso_view_option.added_at is '添加时间';
comment on column noteverso_view_option.update_at is '更新时间';
comment on column noteverso_view_option.filter_value is '过滤值';

-- create noteverso user
CREATE TABLE IF NOT EXISTS noteverso_user_info (
   id               bigserial NOT NULL,
   user_id          varchar(50) NOT NULL CONSTRAINT uq_user_id UNIQUE,
   avatar           jsonb,
   username         varchar(50) NOT NULL CONSTRAINT uq_username UNIQUE,
   email            varchar(50) NOT NULL,
   full_name        varchar(20) DEFAULT NULL::character varying,
   has_password     smallint DEFAULT 0,
   password         varchar(80) NOT NULL,
   is_premium       smallint DEFAULT 0,
   premium_status   smallint DEFAULT 0,
   premium_until    timestamp with time zone,
   authority        varchar(20) DEFAULT 'normal'::character varying,
   joined_at        timestamp with time zone NOT NULL,
   created_at       timestamp with time zone NOT NULL,
   updated_at       timestamp with time zone NOT NULL,
    PRIMARY KEY (id)
);

COMMENT ON TABLE noteverso_user_info IS '用户基本信息表';
COMMENT ON COLUMN noteverso_user_info.id IS 'id';
COMMENT ON COLUMN noteverso_user_info.user_id IS '用户id';
COMMENT ON CONSTRAINT uq_user_id ON noteverso_user_info IS 'user_id 唯一';
COMMENT ON COLUMN noteverso_user_info.avatar IS '头像';
COMMENT ON COLUMN noteverso_user_info.username IS '用户名称';
COMMENT ON CONSTRAINT uq_username ON noteverso_user_info IS 'email 唯一';
COMMENT ON COLUMN noteverso_user_info.email IS '邮箱';
COMMENT ON COLUMN noteverso_user_info.full_name IS '昵称';
COMMENT ON COLUMN noteverso_user_info.has_password IS '是否有密码, 0 - 否, 1 - 是';
COMMENT ON COLUMN noteverso_user_info.password IS '密码';
COMMENT ON COLUMN noteverso_user_info.is_premium IS '是否是付费用户, 0 - 否, 1 - 是';
COMMENT ON COLUMN noteverso_user_info.premium_status IS '付费用户状态 0 - not_premium, 1 - premium';
COMMENT ON COLUMN noteverso_user_info.premium_until IS '付费用户到期时间';
COMMENT ON COLUMN noteverso_user_info.authority IS '权限，premium - 会员，normal - 普通用户';
COMMENT ON COLUMN noteverso_user_info.joined_at IS '加入时间';

CREATE TABLE noteverso_user_config (
   id bigserial not null,
   user_id varchar(50) not null,
   max_file_size bigint NOT NULL,
   projects_quota smallint NOT NULL,
   files_size_quota bigint NOT NULL,
   linked_notes_quota smallint NOT NULL ,
   inbox_project_id varchar(50) DEFAULT NULL,
   lang smallint NULL,
   start_page varchar(30) DEFAULT NULL,
   theme_id smallint DEFAULT NULL,
   tz_info jsonb DEFAULT NULL,
   daily_goal bigint DEFAULT NULL,
   time_format smallint DEFAULT NULL,
   date_format smallint DEFAULT NULL,
   creator varchar(50) DEFAULT NULL,
   updater varchar(50) DEFAULT NULL,
   added_at timestamp with time zone DEFAULT NULL,
   updated_at timestamp with time zone DEFAULT NULL,
    PRIMARY KEY (id)
);

COMMENT ON TABLE noteverso_user_config IS '用户配置表';
COMMENT ON COLUMN noteverso_user_config.id IS 'id';
COMMENT ON COLUMN noteverso_user_config.user_id IS '用户id';
COMMENT ON COLUMN noteverso_user_config.max_file_size IS '单个文件最大限制，单位bytes';
COMMENT ON COLUMN noteverso_user_config.projects_quota IS '项目数量配额';
COMMENT ON COLUMN noteverso_user_config.files_size_quota IS '文件大小总和配额，单位bytes';
COMMENT ON COLUMN noteverso_user_config.linked_notes_quota IS '链接笔记数量配额';
COMMENT ON COLUMN noteverso_user_config.inbox_project_id IS '收件箱项目id';
COMMENT ON COLUMN noteverso_user_config.lang IS '语言, 0 - zh-cn,1 - en-us';
COMMENT ON COLUMN noteverso_user_config.start_page IS '用户首次登陆应用后的定位页面';
COMMENT ON COLUMN noteverso_user_config.theme_id IS '主题id';
COMMENT ON COLUMN noteverso_user_config.tz_info IS '时区信息 gmt_string, hours, is_dst, minutes, timezone';
COMMENT ON COLUMN noteverso_user_config.daily_goal IS '日常目标';
COMMENT ON COLUMN noteverso_user_config.time_format IS '时间格式 0 - 13:00，1 - 1:00pm，默认 0';
COMMENT ON COLUMN noteverso_user_config.date_format IS '日期格式 0 - YYYY-MM-DD，1 - DD-MM-YYYY，默认 0';

-- create noteverso setting
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
