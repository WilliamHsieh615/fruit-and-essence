package com.williamhsieh.fruitandessence.service;

import com.williamhsieh.fruitandessence.dto.MemberLoginRequest;
import com.williamhsieh.fruitandessence.dto.MemberRegisterRequest;
import com.williamhsieh.fruitandessence.model.Member;

public interface MemberService {

    Member getMemberById(Integer memberId);

    Integer register(MemberRegisterRequest memberRegisterRequest);

    Member login(MemberLoginRequest memberLoginRequest);

}
