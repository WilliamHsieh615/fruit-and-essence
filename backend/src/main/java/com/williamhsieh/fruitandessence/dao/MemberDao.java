package com.williamhsieh.fruitandessence.dao;

import com.williamhsieh.fruitandessence.dto.LoginHistoryQueryParams;
import com.williamhsieh.fruitandessence.dto.MemberRegisterRequest;
import com.williamhsieh.fruitandessence.model.LoginHistory;
import com.williamhsieh.fruitandessence.model.Member;
import com.williamhsieh.fruitandessence.model.MemberSubscription;
import com.williamhsieh.fruitandessence.model.Role;

import java.util.List;

public interface MemberDao {

    Member getMemberById(Integer memberId);

    Member getMemberByEmail(String email);

    List<MemberSubscription> getSubscriptionsByMemberId(Integer memberId);

    Integer createMember(Member member);

    void updateMember(Member member);

    void updateMemberProfile(Member member);

    void updateMemberPassword(Member member);

    void addMemberSubscriptions(List<MemberSubscription> memberSubscriptionList);

    void updateMemberSubscriptions(List<MemberSubscription> memberSubscriptionList);

    List<Role> getRolesByMemberId(Integer memberId);

    Role getRoleByName(String roleName);

    void addRoleForMemberId(Integer memberId, Role role);

    void removeRoleForMemberId(Integer memberId, Role role);

    void insertLoginHistory(LoginHistory loginHistory);

    List<LoginHistory> getLoginHistoryList(LoginHistoryQueryParams loginHistoryQueryParams);

}
