package com.noteverso.core.service;

import com.noteverso.common.exceptions.DuplicateRecordException;
import com.noteverso.common.exceptions.NoSuchDataException;
import com.noteverso.core.dao.LabelMapper;
import com.noteverso.core.dao.NoteLabelRelationMapper;
import com.noteverso.core.model.Label;
import com.noteverso.core.request.LabelCreateRequest;
import com.noteverso.core.request.LabelUpdateRequest;
import com.noteverso.core.service.impl.LabelServiceImpl;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LabelServiceTest {
    @Mock
    private LabelMapper labelMapper;

    @Mock
    private NoteLabelRelationMapper noteLabelRelationMapper;

    @InjectMocks
    private LabelServiceImpl labelService;

    @Test
    void should_createLabelSuccessfully() {
        // Arrange
        String userId = "1234567";
        LabelCreateRequest request = new LabelCreateRequest();
        request.setName("test");
        request.setIsFavorite(0);
        request.setColor("red");

        // Act
        labelService.createLabel(request, userId);

        // Assert
        ArgumentCaptor<Label> captor = ArgumentCaptor.forClass(Label.class);
        verify(labelMapper).insert(captor.capture());
        Label newedlabel = captor.getValue();
        assertEquals(newedlabel.getName(), request.getName());
    }

    @Test
    void createLabel_shouldThrowException_whenNameDuplicate() {
        // Arrange
        String userId = "1234567";
        LabelCreateRequest request = new LabelCreateRequest();
        request.setName("test");
        request.setIsFavorite(0);
        request.setColor("red");
        doThrow(new DuplicateKeyException("")).when(labelMapper).insert(any());

        // Act
        ThrowableAssert.ThrowingCallable callable = () -> labelService.createLabel(request, userId);

        // Assert
        assertThatThrownBy(callable)
                .isInstanceOf(DuplicateRecordException.class)
                .hasMessage("Label name has already been taken");
    }

    @Test
    void should_updateLabelSuccessfully() {
        // Arrange
        LabelUpdateRequest request = new LabelUpdateRequest();
        request.setName("test");
        request.setIsFavorite(1);
        request.setColor("blue");

        // Act
        labelService.updateLabel("1", request, "1234567");

        // Assert
        ArgumentCaptor<Label> captor = ArgumentCaptor.forClass(Label.class);
        verify(labelMapper).update(captor.capture(), any());
        Label updatedLabel = captor.getValue();
        assertEquals(updatedLabel.getName(), request.getName());
    }

    @Test
    void should_deleteLabelSuccessfully() {
        // Arrange
        String labelId = "1";
        String userId = "1234567";
        when(labelMapper.delete(any())).thenReturn(1);

        // Act
        labelService.deleteLabel(labelId, userId);

        // Assert
        verify(noteLabelRelationMapper, times(1)).delete(any());
    }

    @Test
    void deleteLabel_shouldThrowException_whenLabelNotFound() {
        // Arrange
        String labelId = "1";
        String userId = "1234567";

        when(labelMapper.delete(any())).thenReturn(0);

        // Act
        ThrowableAssert.ThrowingCallable callable = () -> labelService.deleteLabel(labelId, userId);

        // Assert
        assertThatThrownBy(callable).isInstanceOf(NoSuchDataException.class).hasMessage("Label not found");
    }

    @Test
    void updateIsFavoriteStatus_shouldThrowException_whenLabelNotFound() {
        // Arrange
        String labelId = "1";

        // Act
        ThrowableAssert.ThrowingCallable callable = () -> labelService.updateIsFavoriteStatus(labelId, 1);

        // Assert
        assertThatThrownBy(callable).isInstanceOf(NoSuchDataException.class).hasMessage("Label not found");
    }
}