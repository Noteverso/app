package com.noteverso.core.service;

import com.noteverso.core.model.ViewOption;
import com.noteverso.core.request.ViewOptionCreate;
import com.noteverso.core.request.ViewOptionUpdate;

import java.util.HashMap;
import java.util.List;

public interface ViewOptionService {
    void createViewOption(ViewOptionCreate request, String userId);

    void updateViewOption(ViewOptionUpdate request, String userId);

    void deleteViewOption(String objectId, String userId);

    ViewOption getViewOption(ViewOption viewOption, String userId);

    HashMap<String, ViewOption> getViewOptionsMap(List<String> objectIds, String userId);
}
