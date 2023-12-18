package com.noteverso.core.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@TableName(value = "noteverso_label", autoResultMap = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Label {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String labelId;
    private String name;
    private String color;
    private Integer isFavorite;
    private Instant addedAt;
    private Instant updatedAt;
    private String creator;
    private String updater;
}
