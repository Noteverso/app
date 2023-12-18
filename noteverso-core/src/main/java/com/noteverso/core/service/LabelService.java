package com.noteverso.core.service;

import com.noteverso.core.request.LabelCreateRequest;
import com.noteverso.core.request.LabelUpdateRequest;

public interface LabelService {
    void createLabel(LabelCreateRequest request, String userId);

    void updateLabel(String labelId, LabelUpdateRequest request, String userId);

    void deleteLabel(String labelId, String userId);

    void updateIsFavoriteStatus(String labelId, Integer isFavorite);
}
