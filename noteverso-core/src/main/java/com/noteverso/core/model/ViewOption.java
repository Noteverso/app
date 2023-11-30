package com.noteverso.core.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@TableName(value = "noteverso_view_option")
@Data
public class ViewOption {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String objectId;
    private Integer viewMode;
    private Integer groupedBy;
    private Integer viewType;
    private Integer orderedBy;
    private Integer orderValue;
    private Integer showArchivedNotes;
    private Integer filteredBy;
    private Instant addedAt;
    private Instant updateAt;
    private String creator;
    private String updater;
    private String filterValue;
}