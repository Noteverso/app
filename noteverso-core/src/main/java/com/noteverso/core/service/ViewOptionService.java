package com.noteverso.core.service;

import com.noteverso.core.request.ViewOptionCreate;
import com.noteverso.core.request.ViewOptionUpdate;

public interface ViewOptionService {
    void createViewOption(ViewOptionCreate request, String userId);

    void updateViewOption(ViewOptionUpdate request, String userId);

    void deleteViewOption(String objectId, String userId);
}
