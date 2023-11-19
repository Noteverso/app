package com.noteverso.core.model;

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

@TableName(value = "noteverso_user_info", autoResultMap = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private String userId;

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
     * 创建时间
     */
    private Instant createdAt;

    /**
     * 更新时间
     */
    private Instant updatedAt;
}