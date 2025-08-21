package com.williamhsieh.fruitandessence.dao;

import com.williamhsieh.fruitandessence.dto.MemberRegisterRequest;
import com.williamhsieh.fruitandessence.model.Member;

public interface MemberDao {

    Member getMemberById(Integer memberId);

    Integer createMember(MemberRegisterRequest memberRegisterRequest);

}
