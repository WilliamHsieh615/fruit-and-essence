package com.williamhsieh.fruitandessence.dao;

import com.williamhsieh.fruitandessence.dto.MemberRegisterRequest;
import com.williamhsieh.fruitandessence.model.Member;
import com.williamhsieh.fruitandessence.model.MemberSubscription;
import com.williamhsieh.fruitandessence.model.Role;

import java.util.List;

public interface MemberDao {

    Member getMemberById(Integer memberId);

    Member getMemberByEmail(String email);

    List<MemberSubscription> getSubscriptionsByMemberId(Integer memberId);

    Integer createMember(MemberRegisterRequest memberRegisterRequest);

    void updateMember(Member member);

    void addMemberSubscription(MemberSubscription memberSubscription);

    void updateMemberSubscription(MemberSubscription memberSubscription);

    List<Role> getRolesByMemberId(Integer memberId);

    Role getRoleByName(String roleName);

    void addRoleForMemberId(Integer memberId, Role role);

    void removeRoleForMemberId(Integer memberId, Role role);

}
