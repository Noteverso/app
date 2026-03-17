package com.noteverso.core.validation;

import com.noteverso.core.model.request.NoteCreateRequest;
import com.noteverso.core.model.request.NoteUpdateRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ContentRequiredValidator implements ConstraintValidator<ContentRequired, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value instanceof NoteCreateRequest request) {
            return hasContent(request.getContentJson());
        }
        if (value instanceof NoteUpdateRequest request) {
            return hasContent(request.getContentJson());
        }
        return false;
    }

    private boolean hasContent(Object content) {
        return content != null;
    }
}
