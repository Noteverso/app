package com.noteverso.core.service;

import com.noteverso.core.model.dto.LabelItem;
import com.noteverso.core.model.dto.NoteItem;
import com.noteverso.core.model.dto.SelectItem;
import com.noteverso.core.model.pagination.PageResult;
import com.noteverso.core.model.request.LabelCreateRequest;
import com.noteverso.core.model.request.LabelRequest;
import com.noteverso.core.model.request.LabelUpdateRequest;
import com.noteverso.core.model.request.NotePageRequest;

import java.util.List;

public interface LabelService {
    String createLabel(LabelCreateRequest request, String userId);

    void updateLabel(String labelId, LabelUpdateRequest request, String userId);

    void deleteLabel(String labelId, String userId);

    void updateIsFavoriteStatus(String labelId, Integer isFavorite);

    List<LabelItem> getLabels(String userId);

    List<SelectItem> getLabelSelectItems(LabelRequest request, String userId);

    PageResult<NoteItem> getNotePageByLabel(String labelId, NotePageRequest request, String userId);
}
