package com.williamhsieh.fruitandessence.service;

import com.williamhsieh.fruitandessence.dto.*;
import com.williamhsieh.fruitandessence.model.Member;
import com.williamhsieh.fruitandessence.model.MemberSubscription;
import com.williamhsieh.fruitandessence.model.Role;

import java.util.List;

public interface MemberService {

    Integer register(MemberRegisterRequest memberRegisterRequest);

    Member login(MemberLoginRequest memberLoginRequest);

    MemberResponse getMemberById(Integer memberId);

    MemberResponse updateMemberProfile(Integer memberId, MemberProfileRequest memberProfileRequest);

    String sendResetPasswordLink(String email);

    String resetPassword(String token,ResetPasswordRequest resetPasswordRequest);

    String changePassword(Integer memberId, ChangePasswordRequest changePasswordRequest);

    List<MemberSubscriptionResponse> getSubscriptionsByMemberId(Integer memberId);

    List<MemberSubscriptionResponse> updateMemberSubscriptions(Integer memberId, List<MemberSubscriptionRequest> memberSubscriptionRequests);

    List<Role> getRolesByMemberId(Integer memberId);

}
