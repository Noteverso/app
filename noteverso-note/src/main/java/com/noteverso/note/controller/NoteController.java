package com.noteverso.note.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.noteverso.common.api.ApiResult;
import com.noteverso.note.model.Note;
import com.noteverso.note.request.NoteCreateRequest;
import com.noteverso.note.service.NoteService;
import com.noteverso.user.dao.UserMapper;
import com.noteverso.user.model.User;
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
import java.util.Date;
import java.util.HashMap;

@Tag(name = "Note", description = "Note management APIs")
@RestController
@RequestMapping("/api/note")
@AllArgsConstructor
@Slf4j
public class NoteController {
    private NoteService noteService;
    private final UserMapper userMapper;

    @Operation(
        summary = "Create a Note",
        description = "Create a Note object by specifying its name and addedAt. The response is Note object with id, name and addedAt.",
        tags = { "Post" })
    @PostMapping("/create")
    public HashMap<String, Object> createNote(@Valid @RequestBody NoteCreateRequest request) {
        return noteService.createNote(request);
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
    public ApiResult<HashMap<?, ?>> getNote(@PathVariable("id") long id) {
        NoteCreateRequest request = new NoteCreateRequest();
        request.setName("example");
        request.setAddedAt(new Date());
        HashMap<String, Object> note = noteService.createNote(request);
        return ApiResult.success(note);
    }

    @GetMapping("/lambda/{username}")
    public ApiResult<User> sayHello(@PathVariable String username) {
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.eq(User::getUsername, username);
        User user = userMapper.selectOne(qw);
        return ApiResult.success(user);
    }

    @GetMapping("/mapper/{username}")
    public ApiResult<User> mapper(@PathVariable String username) {
        User user = userMapper.findUserByUsername(username);
        return ApiResult.success(user);
    }
}
