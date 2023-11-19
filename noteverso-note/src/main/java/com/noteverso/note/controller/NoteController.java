package com.noteverso.note.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.noteverso.common.api.ApiResult;
import com.noteverso.common.context.TenantContext;
import com.noteverso.note.model.Note;
import com.noteverso.note.request.NoteCreateRequest;
import com.noteverso.note.service.NoteService;
import com.noteverso.core.dao.UserMapper;
import com.noteverso.core.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Optional;

@Tag(name = "Note", description = "Note management APIs")
@RestController
@RequestMapping("/api/v1/notes")
@AllArgsConstructor
@Slf4j
public class NoteController {
    private NoteService noteService;
    private final UserMapper userMapper;

    @Operation(
        summary = "Create a Note",
        description = "Create a Note object",
        tags = { "Post" })
    @PostMapping("")
    public ApiResult<Void> createNote(@Valid @RequestBody NoteCreateRequest request) {
        noteService.createNote(request);
        return ApiResult.success(null);
    }

    @Operation(
        summary = "Retrieve a Note by Id",
        description = "Get a Note object by specifying its id. The response is Tutorial object with id, title, description and published status.",
        tags = { "Get" })
    @ApiResponses({
        @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = Note.class), mediaType = "application/json")}),
        @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }),
        @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) })})
    @GetMapping("/get/{id}")
    public ApiResult<Void> getNote(@PathVariable("id") long id) {
        NoteCreateRequest request = new NoteCreateRequest();
        return ApiResult.success(null);
    }

    @GetMapping("/lambda/{username}")
    public ApiResult<User> sayHello(@PathVariable String username) {
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.eq(User::getUsername, username);
        User user = userMapper.selectOne(qw);
        return ApiResult.success(user);
    }

    @GetMapping("/mapper/{username}")
    public ApiResult<Optional<User>> mapper(@PathVariable String username) {
        log.info("tenantId: {}", TenantContext.getTenantId());
        var user = userMapper.findUserByUsername(username);
        return ApiResult.success(user);
    }
}
