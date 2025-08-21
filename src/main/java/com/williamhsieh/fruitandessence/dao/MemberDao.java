package com.williamhsieh.fruitandessence.dao;

import com.williamhsieh.fruitandessence.dto.MemberRegisterRequest;
import com.williamhsieh.fruitandessence.model.Member;

public interface MemberDao {

    Member getMemberById(Integer memberId);

    Member getMemberByEmail(String email);

    Integer createMember(MemberRegisterRequest memberRegisterRequest);

}
