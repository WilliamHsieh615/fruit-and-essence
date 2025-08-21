package com.williamhsieh.fruitandessence.service;

import com.williamhsieh.fruitandessence.dao.MemberDao;
import com.williamhsieh.fruitandessence.dto.MemberRegisterRequest;
import com.williamhsieh.fruitandessence.model.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class MemberServiceImpl implements MemberService {

    // 創建 log 變數
    private final static Logger log =  LoggerFactory.getLogger(MemberServiceImpl.class);

    @Autowired
    private MemberDao memberDao;

    @Override
    public Member getMemberById(Integer memberId) {
        return memberDao.getMemberById(memberId);
    }

    @Override
    public Integer register(MemberRegisterRequest memberRegisterRequest) {

        // 判斷 email 是否被註冊過
        Member member = memberDao.getMemberByEmail(memberRegisterRequest.getEmail());

        if (member != null) {
            log.warn("{}, Email already exists!", memberRegisterRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // 創建帳號
        return memberDao.createMember(memberRegisterRequest);
    }
}
