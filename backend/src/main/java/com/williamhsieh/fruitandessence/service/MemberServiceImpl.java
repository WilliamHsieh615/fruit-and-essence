package com.williamhsieh.fruitandessence.service;

import com.williamhsieh.fruitandessence.dao.MemberDao;
import com.williamhsieh.fruitandessence.dto.*;
import com.williamhsieh.fruitandessence.model.Member;
import com.williamhsieh.fruitandessence.model.MemberSubscription;
import com.williamhsieh.fruitandessence.model.Role;
import com.williamhsieh.fruitandessence.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
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
        Member member = memberDao.getMemberByEmail(memberRegisterRequest.getEmail());

        if (member != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already registered");
        }

        // 密碼加密
        String hashedPassword = passwordEncoder.encode(memberRegisterRequest.getPassword());
        memberRegisterRequest.setPassword(hashedPassword);

        Integer memberId = memberDao.createMember(memberRegisterRequest);

        // 設定預設角色
        Role normalRole = memberDao.getRoleByName("ROLE_NORMAL_MEMBER");
        memberDao.addRoleForMemberId(memberId, normalRole);

        // 訂閱
        if (memberRegisterRequest.getMemberSubscriptionRequests() != null) {
            for (MemberSubscriptionRequest memberSubscriptionRequest : memberRegisterRequest.getMemberSubscriptionRequests()) {
                MemberSubscription memberSubscription = new MemberSubscription();
                memberSubscription.setMemberId(memberId);
                memberSubscription.setSubscriptionType(memberSubscriptionRequest.getSubscriptionType());
                memberSubscription.setSubscribed(memberSubscriptionRequest.getSubscribed());
                memberSubscription.setCreatedDate(LocalDateTime.now());
                memberSubscription.setLastModifiedDate(LocalDateTime.now());

                memberDao.addMemberSubscription(memberSubscription);
            }
        }

        // 創建帳號
        return memberId;
    }

    @Override
    public Member login(MemberLoginRequest memberLoginRequest) {

        Member member = memberDao.getMemberByEmail(memberLoginRequest.getEmail());

        // 檢查 member 是否存在
        if (member == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email does not exist!");
        }

        // 驗證密碼
        if (passwordEncoder.matches(memberLoginRequest.getPassword(), member.getPassword())) {
            return member;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong password!");
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

        memberDao.updateMember(member);

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

        String newPassword = resetPasswordRequest.getNewPassword();

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

            String hashedPassword = passwordEncoder.encode(newPassword);
            member.setPassword(hashedPassword);
            memberDao.updateMember(member);

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

        memberDao.updateMember(member);

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

        // 遍歷前端送過來的更新
        for (MemberSubscriptionRequest memberSubscriptionRequest : memberSubscriptionRequests) {
            MemberSubscription existing = memberSubscriptionMap.get(memberSubscriptionRequest.getSubscriptionType());
            if (existing != null) {
                existing.setSubscribed(memberSubscriptionRequest.getSubscribed());
                existing.setLastModifiedDate(LocalDateTime.now());

                memberDao.updateMemberSubscription(existing);
            } else {
                // 如果沒有這個 subscriptionType，可以選擇新增
                MemberSubscription memberSubscription = new MemberSubscription();
                memberSubscription.setMemberId(memberId);
                memberSubscription.setSubscriptionType(memberSubscriptionRequest.getSubscriptionType());
                memberSubscription.setSubscribed(memberSubscriptionRequest.getSubscribed());
                memberSubscription.setCreatedDate(LocalDateTime.now());
                memberSubscription.setLastModifiedDate(LocalDateTime.now());

                memberDao.addMemberSubscription(memberSubscription);
            }
        }

        return getSubscriptionsByMemberId(memberId);
    }

    @Override
    public List<Role> getRolesByMemberId(Integer memberId) {

        List<Role> roleList = memberDao.getRolesByMemberId(memberId);

        return roleList;

    }

}
