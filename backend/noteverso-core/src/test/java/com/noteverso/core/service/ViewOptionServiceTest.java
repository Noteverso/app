package com.noteverso.core.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.noteverso.core.dao.ViewOptionMapper;
import com.noteverso.core.model.entity.ViewOption;
import com.noteverso.core.model.request.ViewOptionCreate;
import com.noteverso.core.model.request.ViewOptionUpdate;
import com.noteverso.core.service.impl.ViewOptionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ViewOptionServiceTest {

    @Mock
    private ViewOptionMapper viewOptionMapper;

    @InjectMocks
    private ViewOptionServiceImpl viewOptionService;

    @Test
    void should_createViewOption_successfully() {
        // Arrange
        ViewOptionCreate request = new ViewOptionCreate();
        request.setObjectId("obj1");
        request.setViewType(1);
        request.setViewMode(0);
        String userId = "user1";
        
        when(viewOptionMapper.insert(any(ViewOption.class))).thenReturn(1);

        // Act
        viewOptionService.createViewOption(request, userId);

        // Assert
        verify(viewOptionMapper).insert(any(ViewOption.class));
    }

    @Test
    void should_getViewOptionsMap_successfully() {
        // Arrange
        List<String> objectIds = List.of("obj1", "obj2");
        String userId = "user1";
        
        ViewOption vo1 = new ViewOption();
        vo1.setObjectId("obj1");
        vo1.setViewType(1);
        
        ViewOption vo2 = new ViewOption();
        vo2.setObjectId("obj2");
        vo2.setViewType(1);
        
        when(viewOptionMapper.selectList(any(LambdaUpdateWrapper.class)))
            .thenReturn(List.of(vo1, vo2));

        // Act
        HashMap<String, ViewOption> result = viewOptionService.getViewOptionsMap(objectIds, userId);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsKey("obj1");
        assertThat(result).containsKey("obj2");
    }

    @Test
    void should_getViewOptionsMap_returnEmpty_whenNullInput() {
        // Arrange
        List<String> objectIds = null;
        String userId = "user1";

        // Act
        HashMap<String, ViewOption> result = viewOptionService.getViewOptionsMap(objectIds, userId);

        // Assert
        assertThat(result).isEmpty();
    }
}