package com.noteverso.core.service;

import com.noteverso.core.dto.LabelItem;
import com.noteverso.core.dto.SelectItem;
import com.noteverso.core.request.LabelCreateRequest;
import com.noteverso.core.request.LabelRequest;
import com.noteverso.core.request.LabelUpdateRequest;

import java.util.List;

public interface LabelService {
    String createLabel(LabelCreateRequest request, String userId);

    void updateLabel(String labelId, LabelUpdateRequest request, String userId);

    void deleteLabel(String labelId, String userId);

    void updateIsFavoriteStatus(String labelId, Integer isFavorite);

    List<LabelItem> getLabels(String userId);

    List<SelectItem> getLabelSelectItems(LabelRequest request, String userId);
}
