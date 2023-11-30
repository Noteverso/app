package com.noteverso.core.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class UserDTO {
    private String userId;
    private String username;
    private Integer hasPassword;
    private String password;
    private Integer isPremium;
    private Integer premiumStatus;
    private String authority;
    private Instant premiumUntil;
    private Instant joinedAt;
    private String inboxProjectId;
    private String startPage;
    private Long maxFileSize;
    private Integer projectsQuota;
    private Long filesSizeQuota;
    private Integer linkedNotesQuota;
}
