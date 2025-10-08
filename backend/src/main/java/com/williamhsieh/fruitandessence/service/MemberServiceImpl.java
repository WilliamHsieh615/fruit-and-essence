package com.williamhsieh.fruitandessence.service;

import com.williamhsieh.fruitandessence.dao.MemberDao;
import com.williamhsieh.fruitandessence.dto.*;
import com.williamhsieh.fruitandessence.model.LoginHistory;
import com.williamhsieh.fruitandessence.model.Member;
import com.williamhsieh.fruitandessence.model.MemberSubscription;
import com.williamhsieh.fruitandessence.model.Role;
import com.williamhsieh.fruitandessence.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @Override
    public Integer register(MemberRegisterRequest memberRegisterRequest) {

        // 判斷 email 是否被註冊過
        if (memberDao.getMemberByEmail(memberRegisterRequest.getEmail()) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already registered");
        }

        // 資料轉移
        Member member = new Member();
        member.setEmail(memberRegisterRequest.getEmail());
        member.setPassword(passwordEncoder.encode(memberRegisterRequest.getPassword()));
        member.setName(memberRegisterRequest.getName());
        member.setPhone(memberRegisterRequest.getPhone());
        member.setBirthday(memberRegisterRequest.getBirthday());
        member.setCreatedDate(LocalDateTime.now());
        member.setLastModifiedDate(LocalDateTime.now());

        Integer memberId = memberDao.createMember(member);

        // 設定預設角色
        Role normalRole = memberDao.getRoleByName("ROLE_NORMAL_MEMBER");
        memberDao.addRoleForMemberId(memberId, normalRole);

        // 訂閱
        if (memberRegisterRequest.getMemberSubscriptionRequests() != null) {
            List<MemberSubscription> memberSubscriptionList = memberRegisterRequest.getMemberSubscriptionRequests().stream()
                    .map(req -> {
                        MemberSubscription memberSubscription = new MemberSubscription();
                        memberSubscription.setMemberId(memberId);
                        memberSubscription.setSubscriptionType(req.getSubscriptionType());
                        memberSubscription.setSubscribed(req.getSubscribed());
                        LocalDateTime now = LocalDateTime.now();
                        memberSubscription.setCreatedDate(now);
                        memberSubscription.setLastModifiedDate(now);
                        return memberSubscription;
                    })
                    .toList();

            memberDao.addMemberSubscriptions(memberSubscriptionList);
        }

        // 創建帳號
        return memberId;
    }

    @Override
    public Member login(MemberLoginRequest memberLoginRequest, HttpServletRequest httpServletRequest) {

        Member member = memberDao.getMemberByEmail(memberLoginRequest.getEmail());

        boolean success = false;
        Integer memberId = null;

        // 檢查 member 是否存在
        if (member != null) {
            memberId = member.getMemberId();
            // 密碼比對
            if (passwordEncoder.matches(memberLoginRequest.getPassword(), member.getPassword())) {
                success = true;
            }
        }

        String userAgent = (String) httpServletRequest.getAttribute("loginUserAgent");
        String ipAddress = (String) httpServletRequest.getAttribute("loginIpAddress");
        LocalDateTime loginTime = (LocalDateTime) httpServletRequest.getAttribute("loginAttemptTime");

        LoginHistory loginHistory = new LoginHistory();
        loginHistory.setMemberId(memberId);
        loginHistory.setEmail(memberLoginRequest.getEmail());
        loginHistory.setLoginTime(loginTime);
        loginHistory.setUserAgent(userAgent);
        loginHistory.setIpAddress(ipAddress);
        loginHistory.setSuccess(success);

        memberDao.insertLoginHistory(loginHistory);

        if (success) {
            return member;
        } else if(member != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong password!");
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email does not exist!");
        }

    }

    @Override
    public MemberResponse getMemberById(Integer memberId) {

        Member member = memberDao.getMemberById(memberId);
        MemberResponse memberResponse = new MemberResponse();
        memberResponse.setMemberId(memberId);
        memberResponse.setEmail(member.getEmail());
        memberResponse.setPassword(member.getPassword());
        memberResponse.setName(member.getName());
        memberResponse.setPhone(member.getPhone());
        memberResponse.setBirthday(member.getBirthday());

        List<MemberSubscriptionResponse> memberSubscriptionResponseList = member.getMemberSubscriptions().stream()
                .map(memberSubscription -> {
                    MemberSubscriptionResponse memberSubscriptionResponse = new MemberSubscriptionResponse();
                    memberSubscriptionResponse.setMemberSubscriptionId(memberSubscription.getMemberSubscriptionId());
                    memberSubscriptionResponse.setMemberId(memberSubscription.getMemberId());
                    memberSubscriptionResponse.setSubscriptionType(memberSubscription.getSubscriptionType());
                    memberSubscriptionResponse.setSubscribed(memberSubscription.getSubscribed());
                    memberSubscriptionResponse.setCreatedDate(memberSubscription.getCreatedDate());
                    memberSubscriptionResponse.setLastModifiedDate(memberSubscription.getLastModifiedDate());
                    return memberSubscriptionResponse;
                })
                .toList();
        memberResponse.setMemberSubscriptionResponses(memberSubscriptionResponseList);

        memberResponse.setCreatedDate(member.getCreatedDate());
        memberResponse.setLastModifiedDate(member.getLastModifiedDate());

        return memberResponse;
    }

    @Override
    public MemberResponse updateMemberProfile(Integer memberId, MemberProfileRequest memberProfileRequest) {

        Member member = memberDao.getMemberById(memberId);

        if (member == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found");
        }

        member.setName(memberProfileRequest.getName());
        member.setPhone(memberProfileRequest.getPhone());

        member.setLastModifiedDate(LocalDateTime.now());

        memberDao.updateMemberProfile(member);

        return getMemberById(memberId);
    }

    @Override
    public String sendResetPasswordLink(String email) {

        Member member = memberDao.getMemberByEmail(email);

        if (member == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email not registered");
        }

        // 產生重設密碼 token
        String resetToken = JwtUtil.generateResetPasswordToken(email);

        // 建立重設連結
        String resetLink = "http://localhost:3000/reset-password?token=" + resetToken;

        // 寄送 Gmail
        String subject = "Fruit & Essence Password Reset Notification";
        String text = String.format(
                "Dear %s,\\n\\nPlease click the link below to reset your password:\\n%s\\n\\nThis link will expire in 30 minutes.\\n\\nBest regards,\\nFruit & Essence Team",
                member.getName(), resetLink
        );

        mailService.sendMail(email, subject, text);

        return "Password reset link has been sent to your email. Please check your inbox.";
    }

    @Override
    public String resetPassword(String token, ResetPasswordRequest resetPasswordRequest) {

        try {
            Claims claims = JwtUtil.parseToken(token);
            if (JwtUtil.isExpired(claims)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token expired");
            }

            String email = claims.getSubject();
            Member member = memberDao.getMemberByEmail(email);

            if (member == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Member not found");
            }

            member.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
            member.setLastModifiedDate(LocalDateTime.now());

            memberDao.updateMemberPassword(member);

            return "Password has been reset successfully.";

        } catch (JwtException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token");
        }
    }

    @Override
    public String changePassword(Integer memberId, ChangePasswordRequest changePasswordRequest) {

        Member member = memberDao.getMemberById(memberId);
        if (member == null) {
            throw new RuntimeException("Member not found");
        }

        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), member.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old password does not match!");
        }

        member.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        member.setLastModifiedDate(LocalDateTime.now());

        memberDao.updateMemberPassword(member);

        return "Password updated successfully!";
    }

    @Override
    public List<MemberSubscriptionResponse> getSubscriptionsByMemberId(Integer memberId) {

        List<MemberSubscription> memberSubscriptionList = memberDao.getSubscriptionsByMemberId(memberId);

        List<MemberSubscriptionResponse> memberSubscriptionResponseList = new ArrayList<>();

        for (MemberSubscription memberSubscription : memberSubscriptionList) {
            MemberSubscriptionResponse memberSubscriptionResponse = new MemberSubscriptionResponse();
            memberSubscriptionResponse.setMemberSubscriptionId(memberSubscription.getMemberSubscriptionId());
            memberSubscriptionResponse.setMemberId(memberSubscription.getMemberId());
            memberSubscriptionResponse.setSubscriptionType(memberSubscription.getSubscriptionType());
            memberSubscriptionResponse.setSubscribed(memberSubscription.getSubscribed());
            memberSubscriptionResponse.setCreatedDate(memberSubscription.getCreatedDate());
            memberSubscriptionResponse.setLastModifiedDate(memberSubscription.getLastModifiedDate());

            memberSubscriptionResponseList.add(memberSubscriptionResponse);
        }

        return memberSubscriptionResponseList;
    }

    @Override
    public List<MemberSubscriptionResponse> updateMemberSubscriptions(Integer memberId, List<MemberSubscriptionRequest> memberSubscriptionRequests) {

        List<MemberSubscription> memberSubscriptionList = memberDao.getSubscriptionsByMemberId(memberId);


        Map<String, MemberSubscription> memberSubscriptionMap = memberSubscriptionList.stream()
                .collect(Collectors.toMap(MemberSubscription::getSubscriptionType, s -> s, (s1, s2) -> s1));

        List<MemberSubscription> toUpdate = new ArrayList<>();
        List<MemberSubscription> toInsert = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (MemberSubscriptionRequest memberSubscriptionRequest : memberSubscriptionRequests) {
            MemberSubscription existing = memberSubscriptionMap.get(memberSubscriptionRequest.getSubscriptionType());
            if (existing != null) {
                existing.setSubscribed(memberSubscriptionRequest.getSubscribed());
                existing.setLastModifiedDate(now);
                toUpdate.add(existing);
            } else {
                MemberSubscription sub = new MemberSubscription();
                sub.setMemberId(memberId);
                sub.setSubscriptionType(memberSubscriptionRequest.getSubscriptionType());
                sub.setSubscribed(memberSubscriptionRequest.getSubscribed());
                sub.setCreatedDate(now);
                sub.setLastModifiedDate(now);
                toInsert.add(sub);
            }
        }

        if (!toUpdate.isEmpty()) {
            memberDao.updateMemberSubscriptions(toUpdate); // 批次更新
        }
        if (!toInsert.isEmpty()) {
            memberDao.addMemberSubscriptions(toInsert); // 批次新增
        }

        return getSubscriptionsByMemberId(memberId);
    }

    @Override
    public List<Role> getRolesByMemberId(Integer memberId) {

        List<Role> roleList = memberDao.getRolesByMemberId(memberId);

        return roleList;

    }

    @Override
    public List<LoginHistory> getLoginHistoryList(LoginHistoryQueryParams loginHistoryQueryParams) {

        return memberDao.getLoginHistoryList(loginHistoryQueryParams);
    }
}
