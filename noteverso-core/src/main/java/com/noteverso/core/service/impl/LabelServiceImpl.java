package com.noteverso.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.noteverso.common.exceptions.DaoException;
import com.noteverso.common.exceptions.DuplicateRecordException;
import com.noteverso.common.exceptions.NoSuchDataException;
import com.noteverso.common.util.IPUtils;
import com.noteverso.common.util.SnowFlakeUtils;
import com.noteverso.core.dao.LabelMapper;
import com.noteverso.core.dao.NoteLabelRelationMapper;
import com.noteverso.core.dto.LabelDTO;
import com.noteverso.core.model.Label;
import com.noteverso.core.model.NoteLabelRelation;
import com.noteverso.core.request.LabelCreateRequest;
import com.noteverso.core.request.LabelUpdateRequest;
import com.noteverso.core.service.LabelService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import static com.noteverso.common.constant.NumConstants.*;
import static com.noteverso.core.constant.ExceptionConstants.LABEL_NAME_DUPLICATE;
import static com.noteverso.core.constant.ExceptionConstants.LABEL_NOT_FOUND;

import java.time.Instant;

@Service
@AllArgsConstructor
public class LabelServiceImpl implements LabelService {
    private final LabelMapper labelMapper;
    private final NoteLabelRelationMapper noteLabelRelationMapper;
    private final static SnowFlakeUtils snowFlakeUtils = new SnowFlakeUtils(
        LABEL_DATACENTER_ID, IPUtils.getHostAddressWithLong() % NUM_31
    );

    @Override
    public void createLabel(LabelCreateRequest request, String userId) {
        String labelId = String.valueOf(snowFlakeUtils.nextId());
        LabelDTO labelDTO = new LabelDTO();
        labelDTO.setName(request.getName());
        labelDTO.setUserId(userId);
        labelDTO.setColor(request.getColor());
        labelDTO.setIsFavorite(request.getIsFavorite());
        labelDTO.setLabelId(labelId);
        Label label = constructLabel(labelDTO);

        try {
            labelMapper.insert(label);
        } catch (Exception e) {
            if (e instanceof DuplicateKeyException) {
                throw new DuplicateRecordException(LABEL_NAME_DUPLICATE);
            } else {
                throw new DaoException("Failed to create label");
            }
        }
    }

    @Override
    public void updateLabel(String labelId, LabelUpdateRequest request, String userId) {
        LambdaUpdateWrapper<Label> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Label::getLabelId, labelId);
        updateWrapper.eq(Label::getCreator, userId);

        Label updatedLabel = new Label();
        updatedLabel.setUpdatedAt(Instant.now());
        updatedLabel.setName(request.getName());
        updatedLabel.setColor(request.getColor());

        if (request.getIsFavorite() != null) {
            updatedLabel.setIsFavorite(request.getIsFavorite());
        }

        try {
            labelMapper.update(updatedLabel, updateWrapper);
        } catch (Exception e) {
            if (e instanceof DuplicateKeyException) {
                throw new DuplicateRecordException(LABEL_NAME_DUPLICATE);
            } else {
                throw new DaoException("Failed to update label");
            }
        }
    }

    @Override
    public void deleteLabel(String labelId, String userId) {
        LambdaUpdateWrapper<Label> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Label::getLabelId, labelId);
        updateWrapper.eq(Label::getCreator, userId);

        int result = labelMapper.delete(updateWrapper);
        if (result > 0) {
            LambdaUpdateWrapper<NoteLabelRelation> noteLabelRelationWrapper = new LambdaUpdateWrapper<>();
            noteLabelRelationWrapper.eq(NoteLabelRelation::getLabelId, labelId);
            noteLabelRelationWrapper.eq(NoteLabelRelation::getCreator, userId);
            noteLabelRelationMapper.delete(noteLabelRelationWrapper);
        } else {
            throw new NoSuchDataException(LABEL_NOT_FOUND);
        }
    }

    @Override
    public void updateIsFavoriteStatus(String labelId, Integer isFavorite) {
        LambdaUpdateWrapper<Label> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Label::getLabelId, labelId);
        Label label = new Label();
        label.setIsFavorite(isFavorite);
        label.setUpdatedAt(Instant.now());

        int result = labelMapper.update(label, updateWrapper);
        if (result == 0) {
            throw new NoSuchDataException(LABEL_NOT_FOUND);
        }
    }

    private Label constructLabel(LabelDTO labelDTO) {
        return Label
                .builder()
                .labelId(labelDTO.getLabelId())
                .name(labelDTO.getName())
                .color(labelDTO.getColor())
                .creator(labelDTO.getUserId())
                .updater(labelDTO.getUserId())
                .addedAt(Instant.now())
                .updatedAt(Instant.now())
                .isFavorite(null != labelDTO.getIsFavorite() ? labelDTO.getIsFavorite() : NUM_O)
                .build();
    }
}
