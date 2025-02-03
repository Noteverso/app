package com.noteverso.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.noteverso.core.dao.ViewOptionMapper;
import com.noteverso.core.enums.*;
import com.noteverso.core.model.Note;
import com.noteverso.core.model.ViewOption;
import com.noteverso.core.request.ViewOptionCreate;
import com.noteverso.core.request.ViewOptionUpdate;
import com.noteverso.core.service.ViewOptionService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;

import static com.noteverso.common.constant.NumConstants.NUM_1;
import static com.noteverso.common.constant.NumConstants.NUM_O;

@Service
@AllArgsConstructor
public class ViewOptionServiceImpl implements ViewOptionService {
    private final ViewOptionMapper viewOptionMapper;

    @Override
    public void createViewOption(ViewOptionCreate request, String userId) {
        var viewOption = new ViewOption();
        viewOption.setObjectId(request.getObjectId());
        viewOption.setViewType(request.getViewType());
        viewOption.setViewMode(null != request.getViewMode() ? request.getViewMode() : ObjectViewModeEnum.LIST.getValue());
        viewOption.setAddedAt(Instant.now());
        viewOption.setUpdateAt(Instant.now());
        viewOption.setCreator(userId);
        viewOption.setUpdater(userId);
        viewOptionMapper.insert(viewOption);
    }

    @Override
    public void updateViewOption(ViewOptionUpdate request, String userId) {
        LambdaUpdateWrapper<ViewOption> viewOptionUpdateWrapper = new LambdaUpdateWrapper<>();
        viewOptionUpdateWrapper.eq(ViewOption::getId, request.getViewOptionId());
        viewOptionUpdateWrapper.eq(ViewOption::getCreator, userId);

        if (request.getShowDeleted() != null) {
            viewOptionUpdateWrapper.set(ViewOption::getShowDeleted, request.getShowDeleted());
        }

        if (request.getShowArchived() != null) {
            viewOptionUpdateWrapper.set(ViewOption::getShowArchived, request.getShowArchived());
        }

        if (request.getShowPinned() != null) {
            viewOptionUpdateWrapper.set(ViewOption::getShowPinned, request.getShowPinned());
        }

        if (request.getOrderedBy() != null) {
            viewOptionUpdateWrapper.set(ViewOption::getOrderedBy, request.getOrderedBy());
        }

        if (request.getOrderValue() != null) {
            viewOptionUpdateWrapper.set(ViewOption::getOrderValue, request.getOrderValue());
        }

        if (request.getGroupedBy() != null) {
            viewOptionUpdateWrapper.set(ViewOption::getGroupedBy, request.getGroupedBy());
        }

        if (request.getShowAttachmentCount() != null) {
            viewOptionUpdateWrapper.set(ViewOption::getShowAttachmentCount, request.getShowAttachmentCount());
        }

        if (request.getShowLabelList() != null) {
            viewOptionUpdateWrapper.set(ViewOption::getShowLabelList, request.getShowLabelList());
        }

        if (request.getShowCommentCount() != null) {
            viewOptionUpdateWrapper.set(ViewOption::getShowCommentCount, request.getShowCommentCount());
        }

        if (request.getShowRelationNoteCount() != null) {
            viewOptionUpdateWrapper.set(ViewOption::getShowRelationNoteCount, request.getShowRelationNoteCount());
        }

        viewOptionMapper.update(null, viewOptionUpdateWrapper);
    }

    @Override
    public  void deleteViewOption(String objectId, String userId) {
        LambdaUpdateWrapper<ViewOption> viewOptionUpdateWrapper = new LambdaUpdateWrapper<>();
        viewOptionUpdateWrapper.eq(ViewOption::getObjectId, objectId);
        viewOptionUpdateWrapper.eq(ViewOption::getCreator, userId);
        viewOptionMapper.delete(viewOptionUpdateWrapper);
    }

    @Override
    public ViewOption getViewOption(ViewOption viewOption, String userId) {
        LambdaUpdateWrapper<ViewOption> viewOptionUpdateWrapper = new LambdaUpdateWrapper<>();
        if (viewOption.getObjectId() != null) {
            viewOptionUpdateWrapper.eq(ViewOption::getObjectId, viewOption.getObjectId());
        }

        if (viewOption.getViewType() != null) {
            viewOptionUpdateWrapper.eq(ViewOption::getViewType, viewOption.getViewType());
        }

        viewOptionUpdateWrapper.eq(ViewOption::getCreator, userId);
        return viewOptionMapper.selectOne(viewOptionUpdateWrapper);
    }

    @Override
    public HashMap<String, ViewOption> getViewOptionsMap(List<String> objectIds, String userId) {
        HashMap<String, ViewOption> viewOptionMap = new HashMap<>();
        if (null == objectIds || objectIds.isEmpty()) {
            return viewOptionMap;
        }

        LambdaUpdateWrapper<ViewOption> viewOptionUpdateWrapper = new LambdaUpdateWrapper<>();
        viewOptionUpdateWrapper.in(ViewOption::getObjectId, objectIds);
        viewOptionUpdateWrapper.eq(ViewOption::getCreator, userId);
        List<ViewOption> viewOptions = viewOptionMapper.selectList(viewOptionUpdateWrapper);

        for (ViewOption viewOption : viewOptions) {
            viewOptionMap.put(viewOption.getObjectId(), viewOption);
        }

        return viewOptionMap;
    }
}
