package com.noteverso.core.service;

import com.noteverso.core.request.ViewOptionCreate;

public interface ViewOptionService {
    void createViewOption(ViewOptionCreate request, String userId);
}
