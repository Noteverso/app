package com.noteverso.core.controller;

import com.noteverso.common.api.ApiResult;
import com.noteverso.core.manager.AuthManager;
import com.noteverso.core.manager.impl.AuthManagerImpl;
import com.noteverso.core.request.LabelCreateRequest;
import com.noteverso.core.request.LabelUpdateRequest;
import com.noteverso.core.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.noteverso.common.constant.NumConstants.*;

@Tag(name = "Label", description = "Label management APIs")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/labels")
@Slf4j
public class LabelController {
    private final LabelService labelService;
    private static final AuthManager authManager = new AuthManagerImpl();

    @Operation(summary = "Create a Label", description = "Create a Label", tags = { "Post" })
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<Void> createLabel(Authentication authentication, @Valid @RequestBody LabelCreateRequest request) {
        labelService.createLabel(request, authManager.getPrincipal(authentication).getUserId());
        return ApiResult.success(null);
    }

    @Operation(summary = "Update a Label", description = "Update a Label", tags = { "Patch" })
    @PatchMapping("/{id}")
    public ApiResult<Void> updateLabel(Authentication authentication, @PathVariable("id") String labelId, @Valid @RequestBody LabelUpdateRequest request) {
        labelService.updateLabel(labelId, request, authManager.getPrincipal(authentication).getUserId());
        return ApiResult.success(null);
    }

    @Operation(summary = "Delete a Label", description = "Delete a Label", tags = { "Delete" })
    @DeleteMapping("/{id}")
    public ApiResult<Void> deleteLabel(Authentication authentication, @PathVariable("id") String labelId) {
        labelService.deleteLabel(labelId, authManager.getPrincipal(authentication).getUserId());
        return ApiResult.success(null);
    }

    @Operation(summary = "Favorite a Label", description = "Favorite a Label", tags = { "Patch" })
    @PatchMapping("{id}/favorite")
    public ApiResult<Void> favoriteLabel(@PathVariable("id") String labelId) {
        labelService.updateIsFavoriteStatus(labelId, NUM_1);
        return ApiResult.success(null);
    }

    @Operation(summary = "UnFavorite a Label", description = "UnFavorite a Label", tags = { "Patch" })
    @PatchMapping("{id}/unfavorite")
    public ApiResult<Void> unFavoriteLabel(@PathVariable("id") String labelId) {
        labelService.updateIsFavoriteStatus(labelId, NUM_O);
        return ApiResult.success(null);
    }
}
