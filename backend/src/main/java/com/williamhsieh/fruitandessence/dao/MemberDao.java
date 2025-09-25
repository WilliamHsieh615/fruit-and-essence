package com.williamhsieh.fruitandessence.dao;

import com.williamhsieh.fruitandessence.dto.MemberRegisterRequest;
import com.williamhsieh.fruitandessence.model.Member;
import com.williamhsieh.fruitandessence.model.Role;

import java.util.List;

public interface MemberDao {

    Member getMemberById(Integer memberId);

    Member getMemberByEmail(String email);

    Integer createMember(MemberRegisterRequest memberRegisterRequest);

    List<Role> getRolesByMemberId(Integer memberId);

    Role getRoleByName(String roleName);

    void addRoleForMemberId(Integer memberId, Role role);

    void removeRoleForMemberId(Integer memberId, Role role);

}
