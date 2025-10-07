package com.williamhsieh.fruitandessence.dao;

import com.williamhsieh.fruitandessence.dto.MemberRegisterRequest;
import com.williamhsieh.fruitandessence.model.Member;
import com.williamhsieh.fruitandessence.model.MemberSubscription;
import com.williamhsieh.fruitandessence.model.Role;
import com.williamhsieh.fruitandessence.rowmapper.MemberRowMapper;
import com.williamhsieh.fruitandessence.rowmapper.RoleRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MemberDaoImpl implements MemberDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Member getMemberById(Integer memberId) {

        String sql = "SELECT member_id, email, password, " +
                "name, phone, birthday, created_date, last_modified_date " +
                "FROM member WHERE member_id = :memberId";

        Map<String, Object> map = new HashMap<>();
        map.put("memberId", memberId);

        List<Member> memberList = namedParameterJdbcTemplate.query(sql, map, new MemberRowMapper());

        if (memberList.isEmpty()) {
            return null;
        } else {
            return memberList.get(0);
        }
    }

    @Override
    public Member getMemberByEmail(String email) {

        String sql = "SELECT member_id, email, password, " +
                "name, phone, birthday, created_date, last_modified_date " +
                "FROM member WHERE email = :email";

        Map<String, Object> map = new HashMap<>();
        map.put("email", email);

        List<Member> memberList = namedParameterJdbcTemplate.query(sql, map, new MemberRowMapper());

        if (memberList.isEmpty()) {
            return null;
        } else {
            return memberList.get(0);
        }
    }

    @Override
    public Integer createMember(MemberRegisterRequest memberRegisterRequest) {

        String sql = "INSERT INTO member(email, password, name, phone, birthday, " +
                "created_date, last_modified_date) " +
                "VALUES (:email, :password, :name, :phone, :birthday, " +
                ":created_date, :last_modified_date) ";

        Map<String, Object> map = new HashMap<>();
        map.put("email", memberRegisterRequest.getEmail());
        map.put("password", memberRegisterRequest.getPassword());
        map.put("name", memberRegisterRequest.getName());
        map.put("phone", memberRegisterRequest.getPhone());
        map.put("birthday", memberRegisterRequest.getBirthday());

        LocalDateTime now = LocalDateTime.now();
        map.put("created_date", now);
        map.put("last_modified_date", now);

        // 取得與儲存資料庫自動生成的 id
        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        int memberId = keyHolder.getKey().intValue();

        return memberId;
    }

    @Override
    public void updateMember(Member member) {

        String sql = "";

    }

    @Override
    public List<MemberSubscription> getSubscriptionsByMemberId(Integer memberId) {
        return List.of();
    }

    @Override
    public void addMemberSubscription(MemberSubscription memberSubscription) {

    }

    @Override
    public void updateMemberSubscription(MemberSubscription memberSubscription) {

    }

    @Override
    public List<Role> getRolesByMemberId(Integer memberId) {

        String sql = """
                SELECT role.role_id, role.role_name FROM role
                JOIN member_has_role ON role.role_id = member_has_role.role_id
                WHERE member_has_role.member_id = :memberId
                """;
        Map<String, Object> map = new HashMap<>();
        map.put("memberId", memberId);

        List<Role> roleList = namedParameterJdbcTemplate.query(sql, map, new RoleRowMapper());

        return roleList;
    }

    @Override
    public Role getRoleByName(String roleName) {
        String sql = "SELECT role_id, role_name FROM role WHERE role_name = :roleName";

        Map<String, Object> map = new HashMap<>();
        map.put("roleName", roleName);

        List<Role> roleList = namedParameterJdbcTemplate.query(sql, map, new RoleRowMapper());

        if (roleList.isEmpty()) {
            return null;
        } else {
            return roleList.get(0);
        }
    }

    @Override
    public void addRoleForMemberId(Integer memberId, Role role) {
        String sql = "INSERT INTO member_has_role(member_id, role_id) VALUES (:memberId, :roleId)";

        Map<String, Object> map = new HashMap<>();
        map.put("memberId", memberId);
        map.put("roleId", role.getRoleId());

        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public void removeRoleForMemberId(Integer memberId, Role role) {
        String sql = "DELETE FROM member_has_role WHERE member_id = :memberId AND role_id = :roleId";

        Map<String, Object> map = new HashMap<>();
        map.put("memberId", memberId);
        map.put("roleId", role.getRoleId());

        namedParameterJdbcTemplate.update(sql, map);
    }
}
