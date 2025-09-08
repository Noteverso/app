package com.noteverso.core.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@TableName(value = "noteverso_user_config", autoResultMap = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserConfig {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String userId;

    /**
     * 收件箱项目id
     */
    private String inboxProjectId;

    /**
     * 语言, 0 - zh-cn,1 - en-us
     */
    private Integer lang;

    /**

     * 用户首次登陆应用后的定位页面，project?id=${project_id}, upcoming, label?name=${label_name}
     */
    private String startPage;

    /**
     * 主题id
     */
    private Integer themeId;

    /**
     * 时区信息 gmt_string, hours, is_dst, minutes, timezone
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object tzInfo;

    /**
     * 每天记录目标数
     */
    private Long dailyGoal;

    /**
     * 时间格式 0 - 13:00，1 - 1:00pm，默认 0
     */
    private Integer timeFormat;

    /**
     * 日期格式 0 - YYYY-MM-DD，1 - DD-MM-YYYY，默认 0
     */
    private Integer dateFormat;
    private Long maxFileSize;
    /**
     * 项目数量配额
     */
    private Long projectsQuota;
    /**
     * 文件大小总和配额, bytes
     */
    private Long filesSizeQuota;
    /**
     * 链接笔记数量配额
     */
    private Long linkedNotesQuota;
    private String creator;
    private String updater;
    private Instant addedAt;
    private Instant updatedAt;
}
