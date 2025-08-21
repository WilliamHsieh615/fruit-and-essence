package com.williamhsieh.fruitandessence.service;

import com.williamhsieh.fruitandessence.dao.MemberDao;
import com.williamhsieh.fruitandessence.dto.MemberLoginRequest;
import com.williamhsieh.fruitandessence.dto.MemberRegisterRequest;
import com.williamhsieh.fruitandessence.model.Member;
import org.mindrot.jbcrypt.BCrypt;
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

        // 使用 jBCrypt 生成密碼的雜湊值
        String hashedPassword = BCrypt.hashpw(memberRegisterRequest.getPassword(), BCrypt.gensalt(12));
        memberRegisterRequest.setPassword(hashedPassword);

        // 創建帳號
        return memberDao.createMember(memberRegisterRequest);
    }

    @Override
    public Member login(MemberLoginRequest memberLoginRequest) {

        Member member = memberDao.getMemberByEmail(memberLoginRequest.getEmail());

        // 檢查 member 是否存在
        if (member == null) {
            log.warn("{}, Email does not exist!", memberLoginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // 比較密碼
        if(BCrypt.checkpw(memberLoginRequest.getPassword(), member.getPassword())) {
            return member;
        }else{
            log.warn("{}, Wrong password!", memberLoginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
