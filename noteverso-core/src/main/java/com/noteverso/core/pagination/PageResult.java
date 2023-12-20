package com.noteverso.core.pagination;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private List<T> records = Collections.emptyList();

    private long total = 0;

    private long pageIndex;

    private long pageSize;

    public PageResult() {
    }
}
