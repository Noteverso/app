package com.noteverso.core.dao;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.noteverso.core.model.Label;
import com.noteverso.core.request.LabelRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MybatisPlusTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LabelMapperTest {
    @Autowired
    LabelMapper labelMapper;

    @Test
    void getLabels() {
        // Arrange
        String userId = "1";
        Label label1 = constructLabel("1", "label1", "red", userId);
        Label label2 = constructLabel("2", "LABEL2", "green", userId);
        labelMapper.insert(label1);
        labelMapper.insert(label2);
        LabelRequest labelRequest = new LabelRequest();
        labelRequest.setName(" label ");

        // Act
        List<Label> labels = labelMapper.getLabels(labelRequest, userId);

        // Assert
        assertEquals(2, labels.size());
    }

    private Label constructLabel(String labelId, String name, String color, String userId) {
        Label label = new Label();
        label.setLabelId(labelId);
        label.setName(name);
        label.setColor(color);
        label.setCreator(userId);
        label.setUpdater(userId);
        label.setIsFavorite(0);
        return label;
    }
}