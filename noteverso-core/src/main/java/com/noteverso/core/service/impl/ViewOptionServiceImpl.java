package com.noteverso.core.service.impl;

import com.noteverso.core.dao.ViewOptionMapper;
import com.noteverso.core.enums.ObjectGroupByEnum;
import com.noteverso.core.enums.ObjectOrderByEnum;
import com.noteverso.core.enums.ObjectOrderValueEnum;
import com.noteverso.core.enums.ObjectViewModeEnum;
import com.noteverso.core.model.ViewOption;
import com.noteverso.core.request.ViewOptionCreate;
import com.noteverso.core.service.ViewOptionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static com.noteverso.common.constant.NumConstants.NUM_O;

@Service
@AllArgsConstructor
public class ViewOptionServiceImpl implements ViewOptionService {
    private final ViewOptionMapper viewOptionMapper;

    @Override
    public void createViewOption(ViewOptionCreate request, String tenantId) {
        var viewOption = new ViewOption();
        viewOption.setObjectId(request.getObjectId());
        viewOption.setViewType(request.getViewType());
        viewOption.setViewMode(null != request.getViewMode() ? request.getViewMode() : ObjectViewModeEnum.LIST.getValue());
        viewOption.setOrderedBy(null != request.getOrderedBy() ? request.getOrderedBy() : ObjectOrderByEnum.ADDED_AT.getValue());
        viewOption.setOrderValue(null != request.getOrderValue() ? request.getOrderValue() : ObjectOrderValueEnum.DESC.getValue());
        viewOption.setGroupedBy(null != request.getGroupedBy() ? request.getGroupedBy() : ObjectGroupByEnum.ADDED_AT.getValue());
        viewOption.setShowArchivedNotes(null != request.getShowArchivedNotes() ? request.getShowArchivedNotes() : NUM_O);
        viewOption.setAddedAt(Instant.now());
        viewOption.setUpdateAt(Instant.now());
        viewOption.setCreator(tenantId);
        viewOption.setUpdater(tenantId);
        viewOptionMapper.insert(viewOption);
    }
}
