package com.williamhsieh.fruitandessence.controller;

import com.williamhsieh.fruitandessence.dto.*;
import com.williamhsieh.fruitandessence.model.Member;
import com.williamhsieh.fruitandessence.model.MemberSubscription;
import com.williamhsieh.fruitandessence.model.Role;
import com.williamhsieh.fruitandessence.service.MemberService;
import com.williamhsieh.fruitandessence.util.CookieUtil;
import com.williamhsieh.fruitandessence.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MemberController {

    @Autowired
    private MemberService memberService;

    @PostMapping("/members/register")
    public ResponseEntity<MemberResponse> register(@RequestBody @Valid MemberRegisterRequest memberRegisterRequest) {
        Integer memberId = memberService.register(memberRegisterRequest);

        MemberResponse memberResponse = memberService.getMemberById(memberId);

        return ResponseEntity.status(HttpStatus.CREATED).body(memberResponse);
    }

    @PostMapping("/members/login")
    public ResponseEntity<MemberLoginResponse> login(
            @RequestBody @Valid MemberLoginRequest memberLoginRequest,
            HttpServletResponse response) {

        // 驗證帳號密碼
        Member member = memberService.login(memberLoginRequest);

        // 從資料庫取得角色
        List<Role> roles = memberService.getRolesByMemberId(member.getMemberId());
        List<String> roleList = roles.stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());

        // 產生 JWT（Access + Refresh）
        String accessToken = JwtUtil.generateAccessToken(member.getMemberId(), member.getEmail(), roleList);
        String refreshToken = JwtUtil.generateRefreshToken(member.getMemberId());

        // 寫入 HTTP-only cookie
        ResponseCookie accessCookie = CookieUtil.createTokenCookie("accessToken", accessToken, 30 * 60); // 30 分鐘
        ResponseCookie refreshCookie = CookieUtil.createTokenCookie("refreshToken", refreshToken, 7 * 24 * 60 * 60); // 7 天
        CookieUtil.addCookie(response, accessCookie);
        CookieUtil.addCookie(response, refreshCookie);

        // 組成回傳給前端的 DTO
        MemberLoginResponse memberLoginResponse = new MemberLoginResponse();
        memberLoginResponse.setMemberId(member.getMemberId());
        memberLoginResponse.setEmail(member.getEmail());
        memberLoginResponse.setName(member.getName());
        memberLoginResponse.setRoles(roleList);

        return ResponseEntity.status(HttpStatus.OK).body(memberLoginResponse);

    }

    @PostMapping("/members/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {

        // 建立刪除用 cookie（maxAge=0）
        ResponseCookie deleteAccess = CookieUtil.deleteCookie("accessToken");
        ResponseCookie deleteRefresh = CookieUtil.deleteCookie("refreshToken");

        // 加入到 response header
        CookieUtil.addCookie(response, deleteAccess);
        CookieUtil.addCookie(response, deleteRefresh);

        return ResponseEntity.status(HttpStatus.OK).body("Logout Successful!");

    }

    @PostMapping("/members/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody @Valid ForgotPasswordRequest forgotPasswordRequest) {

        String result = memberService.sendResetPasswordLink(forgotPasswordRequest.getEmail());

        return ResponseEntity.status(HttpStatus.OK).body(result);

    }

    @PostMapping("/members/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        String result = memberService.resetPassword(token, resetPasswordRequest);

        return ResponseEntity.status(HttpStatus.OK).body(result);

    }

    @PutMapping("/members/{memberId}/change-password")
    public ResponseEntity<String> changePassword(
            @PathVariable("memberId") Integer memberId,
            @RequestBody @Valid ChangePasswordRequest changePasswordRequest) {

        String result = memberService.changePassword(memberId, changePasswordRequest);

        return ResponseEntity.status(HttpStatus.OK).body(result);

    }

    @GetMapping("/members/{memberId}")
    public ResponseEntity<MemberResponse> getMemberProfile(@PathVariable("memberId") Integer memberId) {

        MemberResponse memberResponse = memberService.getMemberById(memberId);

        return ResponseEntity.status(HttpStatus.OK).body(memberResponse);
    }

    @GetMapping("/members/{memberId}/subscriptions")
    public ResponseEntity<List<MemberSubscriptionResponse>> getMemberSubscriptions(@PathVariable("memberId") Integer memberId) {

        List<MemberSubscriptionResponse> memberSubscriptionList = memberService.getSubscriptionsByMemberId(memberId);

        return ResponseEntity.status(HttpStatus.OK).body(memberSubscriptionList);

    }

    @PutMapping("/members/{memberId}")
    public ResponseEntity<MemberResponse> updateMemberProfile(
            @PathVariable("memberId") Integer memberId,
            @RequestBody @Valid MemberProfileRequest memberProfileRequest){

        MemberResponse memberResponse = memberService.updateMemberProfile(memberId, memberProfileRequest);

        return ResponseEntity.status(HttpStatus.OK).body(memberResponse);
    }

    @PutMapping("/members/{memberId}/subscriptions")
    public ResponseEntity<List<MemberSubscriptionResponse>> updateMemberSubscriptions(
            @PathVariable("memberId") Integer memberId,
            @RequestBody @Valid List<MemberSubscriptionRequest> memberSubscriptionRequests){

        List<MemberSubscriptionResponse> memberSubscriptionResponseList = memberService.updateMemberSubscriptions(memberId, memberSubscriptionRequests);

        return ResponseEntity.status(HttpStatus.OK).body(memberSubscriptionResponseList);

    }

}

