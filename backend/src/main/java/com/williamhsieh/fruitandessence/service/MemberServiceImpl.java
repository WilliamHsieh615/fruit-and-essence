package com.williamhsieh.fruitandessence.service;

import com.williamhsieh.fruitandessence.dao.MemberDao;
import com.williamhsieh.fruitandessence.dto.MemberLoginRequest;
import com.williamhsieh.fruitandessence.dto.MemberRegisterRequest;
import com.williamhsieh.fruitandessence.model.Member;
import com.williamhsieh.fruitandessence.model.Role;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
public class MemberServiceImpl implements MemberService {

    // 創建 log 變數
    private final static Logger log =  LoggerFactory.getLogger(MemberServiceImpl.class);

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

        // 使用統一的 PasswordEncoder 進行加密
        String hashedPassword = passwordEncoder.encode(memberRegisterRequest.getPassword());
        memberRegisterRequest.setPassword(hashedPassword);

        Integer memberId = memberDao.createMember(memberRegisterRequest);

        // 為 Member 添加預設的 Role
        Role normalRole = memberDao.getRoleByName("ROLE_NORMAL_MEMBER");
        memberDao.addRoleForMemberId(memberId, normalRole);

        // 創建帳號
        return memberId;
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
        if(passwordEncoder.matches(memberLoginRequest.getPassword(), member.getPassword())) {
            return member;
        }else{
            log.warn("{}, Wrong password!", memberLoginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public String subscribe(Integer memberId) {

        List<Role> roleList = memberDao.getRolesByMemberId(memberId);

        // 檢查訂閱狀態
        boolean isSubscribed = checkSubscribeStatus(roleList);

        if (isSubscribed) {
            return "已訂閱過，不需重複訂閱";
        } else {
            Role vipRole = memberDao.getRoleByName("ROLE_VIP_MEMBER");
            memberDao.addRoleForMemberId(memberId, vipRole);
            return "訂閱成功！請刪除 Cookie 重新登入";
        }
    }

    @Override
    public String unsubscribe(Integer memberId) {

        List<Role> roleList = memberDao.getRolesByMemberId(memberId);

        // 檢查訂閱狀態
        boolean isSubscribed = checkSubscribeStatus(roleList);

        if (isSubscribed) {
            Role vipRole = memberDao.getRoleByName("ROLE_VIP_MEMBER");
            memberDao.removeRoleForMemberId(memberId, vipRole);
            return "取消訂閱成功！請刪除 Cookie 重新登入";
        } else {
            return "尚未訂閱，無法執行取消訂閱操作";
        }
    }

    private boolean checkSubscribeStatus(List<Role> roleList) {
        boolean isSubscribed = false;

        for (Role role : roleList) {
            if (role.getRoleName().equals("ROLE_VIP_MEMBER")) {
                isSubscribed = true;
            }
        }

        return isSubscribed;
    }
}
