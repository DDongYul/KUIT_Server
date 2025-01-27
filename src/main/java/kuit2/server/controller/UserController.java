package kuit2.server.controller;

import kuit2.server.common.argument_resolver.PreAuthorize;
import kuit2.server.common.exception.UserException;
import kuit2.server.common.response.BaseResponse;
import kuit2.server.dto.user.*;
import kuit2.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static kuit2.server.common.response.status.BaseExceptionResponseStatus.INVALID_USER_STATUS;
import static kuit2.server.common.response.status.BaseExceptionResponseStatus.INVALID_USER_VALUE;
import static kuit2.server.util.BindingResultUtils.getErrorMessages;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    /**
     * 회원 가입
     */
    @PostMapping("")
    public BaseResponse<PostUserResponse> signUp(@Validated @RequestBody PostUserRequest postUserRequest, BindingResult bindingResult) {
        log.info("[UserController.signUp]");
        if (bindingResult.hasErrors()) {
            throw new UserException(INVALID_USER_VALUE, getErrorMessages(bindingResult));
        }
        return new BaseResponse<>(userService.signUp(postUserRequest));
    }

    /**
     * 회원 휴면
     */
    @PatchMapping("/{userId}/dormant")
    public BaseResponse<Object> modifyUserStatus_dormant(@PathVariable @PreAuthorize long userId) {
        log.info("[UserController.modifyUserStatus_dormant]");
        userService.modifyUserStatus_dormant(userId);
        return new BaseResponse<>(null);
    }

    /**
     * 회원 탈퇴
     */
    @PatchMapping("/{userId}/deleted")
    public BaseResponse<Object> modifyUserStatus_deleted(@PathVariable @PreAuthorize long userId) {
        log.info("[UserController.modifyUserStatus_delete]");
        userService.modifyUserStatus_deleted(userId);
        return new BaseResponse<>(null);
    }

    /**
     * 닉네임 변경
     */
    @PatchMapping("/{userId}/nickname")
    public BaseResponse<String> modifyNickname(@PathVariable @PreAuthorize long userId,
                                               @Validated @RequestBody PatchNicknameRequest patchNicknameRequest, BindingResult bindingResult) {
        log.info("[UserController.modifyNickname]");
        if (bindingResult.hasErrors()) {
            throw new UserException(INVALID_USER_VALUE, getErrorMessages(bindingResult));
        }
        userService.modifyNickname(userId, patchNicknameRequest.getNickname());
        return new BaseResponse<>(null);
    }

    /**
     * 이메일 변경
     */
    @PatchMapping("/{userId}/email")
    public BaseResponse<String> modifyEmail(@PathVariable @PreAuthorize long userId,
                                               @Validated @RequestBody PatchEmailRequest patchEmailRequest, BindingResult bindingResult){
        log.info("[UserController.modifyEmail]");
        if (bindingResult.hasErrors()) {
            throw new UserException(INVALID_USER_VALUE, getErrorMessages(bindingResult));
        }
        userService.modifyEmail(userId, patchEmailRequest.getEmail());
        return new BaseResponse<>(null);
    }

    /**
     * 회원 목록 조회
     */
    @GetMapping("")
    public BaseResponse<List<GetUserResponse>> getUsers(
            @RequestParam(required = false, defaultValue = "") String nickname,
            @RequestParam(required = false, defaultValue = "") String email,
            @RequestParam(required = false, defaultValue = "active") String status) {
        log.info("[UserController.getUsers]");
        if (!status.equals("active") && !status.equals("dormant") && !status.equals("deleted")) {
            throw new UserException(INVALID_USER_STATUS);
        }
        return new BaseResponse<>(userService.getUsers(nickname, email, status));
    }
}
