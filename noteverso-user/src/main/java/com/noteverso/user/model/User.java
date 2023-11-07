package com.noteverso.user.model;

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

@TableName(value = "noteverso_user", autoResultMap = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 收件箱项目id
     */
    private Long inboxProjectId;

    /**
     * 头像
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object avatar;

    /**
     * 用户名 - 实际为邮箱用于登录
     */
    private String username;

    /**
     * 名称
     */
    private String fullName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 是否有密码
     */
    private Integer hasPassword;

    /**
     * 密码
     */
    private String password;

    /**
     * 语言, 0 - zh-cn,1 - en-us
     */

    private Integer lang;
    /**
     * 是否是会员
     */
    private Integer isPremium;

    /**
     * 付费用户状态 0 - not_premium, 1 - premium
     */
    private Integer premiumStatus;

    /**
     * 用户角色 normal, premium
     */
    private String authority;

    /**
     * 会员到期时间
     */
    private Instant premiumUntil;

    /**
     * 加入时间
     */
    private Instant joinedAt;

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

    /**
     * 创建时间
     */
    private Instant createdAt;

    /**
     * 更新时间
     */
    private Instant updatedAt;
}