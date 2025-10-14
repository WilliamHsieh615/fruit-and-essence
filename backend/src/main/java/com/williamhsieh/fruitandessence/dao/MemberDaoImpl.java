package com.williamhsieh.fruitandessence.dao;

import com.williamhsieh.fruitandessence.dto.LoginHistoryQueryParams;
import com.williamhsieh.fruitandessence.model.LoginHistory;
import com.williamhsieh.fruitandessence.model.Member;
import com.williamhsieh.fruitandessence.model.MemberSubscription;
import com.williamhsieh.fruitandessence.model.Role;
import com.williamhsieh.fruitandessence.rowmapper.MemberRowMapper;
import com.williamhsieh.fruitandessence.rowmapper.MemberSubscriptionRowMapper;
import com.williamhsieh.fruitandessence.rowmapper.RoleRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
    public Integer createMember(Member member) {

        String sql = "INSERT INTO member(email, password, name, phone, birthday, " +
                "created_date, last_modified_date) " +
                "VALUES (:email, :password, :name, :phone, :birthday, " +
                ":created_date, :last_modified_date) ";

        Map<String, Object> map = new HashMap<>();
        map.put("email", member.getEmail());
        map.put("password", member.getPassword());
        map.put("name", member.getName());
        map.put("phone", member.getPhone());
        map.put("birthday", member.getBirthday());
        map.put("created_date", member.getCreatedDate());
        map.put("last_modified_date", member.getLastModifiedDate());

        // 取得與儲存資料庫自動生成的 id
        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        int memberId = keyHolder.getKey().intValue();

        return memberId;
    }

    @Override
    public void updateMember(Member member) {

        String sql = "UPDATE member SET email = :email, password = :password, name = :name, phone = :phone, " +
                "birthday = :birthday, last_modified_date = :last_modified_date " +
                "WHERE member_id = :memberId";

        Map<String, Object> map = new HashMap<>();
        map.put("memberId", member.getMemberId());

        map.put("email", member.getEmail());
        map.put("password", member.getPassword());
        map.put("name", member.getName());
        map.put("phone", member.getPhone());
        map.put("birthday", member.getBirthday());

        map.put("last_modified_date", member.getLastModifiedDate());

        namedParameterJdbcTemplate.update(sql, map);

    }

    @Override
    public void updateMemberProfile(Member member) {

        String sql = "UPDATE member SET name = :name, phone = :phone, last_modified_date = :last_modified_date " +
                "WHERE member_id = :memberId";

        Map<String, Object> map = new HashMap<>();
        map.put("memberId", member.getMemberId());

        map.put("name", member.getName());
        map.put("phone", member.getPhone());

        map.put("last_modified_date", member.getLastModifiedDate());

        namedParameterJdbcTemplate.update(sql, map);

    }

    @Override
    public void updateMemberPassword(Member member) {

        String sql = "UPDATE member SET password = :password, last_modified_date = :last_modified_date " +
                "WHERE member_id = :memberId";

        Map<String, Object> map = new HashMap<>();
        map.put("memberId", member.getMemberId());

        map.put("password", member.getPassword());

        map.put("last_modified_date", member.getLastModifiedDate());

        namedParameterJdbcTemplate.update(sql, map);

    }

    @Override
    public List<MemberSubscription> getSubscriptionsByMemberId(Integer memberId) {

        String sql = "SELECT member_subscription_id, member_id, subscription_type, " +
                "subscribed, created_date, last_modified_date " +
                "FROM member_subscription WHERE member_id = :memberId";

        Map<String, Object> map = new HashMap<>();
        map.put("memberId", memberId);

        List<MemberSubscription> memberSubscriptionList = namedParameterJdbcTemplate.query(sql, map, new MemberSubscriptionRowMapper());

        return memberSubscriptionList;
    }

    @Override
    public void addMemberSubscriptions(List<MemberSubscription> memberSubscriptionList) {

        String sql = "INSERT INTO member_subscription(member_id, subscription_type, subscribed, " +
                "created_date, last_modified_date) " +
                "VALUES (:memberId, :subscriptionType, :subscribed, :created_date, :last_modified_date) ";

        List<Map<String, Object>> mapArrayList = new ArrayList<>();

        for (MemberSubscription memberSubscription : memberSubscriptionList) {
            Map<String, Object> map = new HashMap<>();
            map.put("memberId", memberSubscription.getMemberId());
            map.put("subscriptionType", memberSubscription.getSubscriptionType());
            map.put("subscribed", memberSubscription.getSubscribed());
            map.put("created_date", memberSubscription.getCreatedDate());
            map.put("last_modified_date", memberSubscription.getLastModifiedDate());

            mapArrayList.add(map);
        }

        namedParameterJdbcTemplate.batchUpdate(sql, mapArrayList.toArray(new Map[0]));

    }

    @Override
    public void updateMemberSubscriptions(List<MemberSubscription> memberSubscriptionList) {

        String sql = "UPDATE member_subscription SET subscribed = :subscribed, last_modified_date = :last_modified_date " +
                "WHERE member_id = :memberId AND subscription_type = :subscriptionType";

        List<Map<String, Object>> mapArrayList = new ArrayList<>();
        for (MemberSubscription memberSubscription : memberSubscriptionList) {
            Map<String, Object> map = new HashMap<>();
            map.put("memberId", memberSubscription.getMemberId());
            map.put("subscriptionType", memberSubscription.getSubscriptionType());
            map.put("subscribed", memberSubscription.getSubscribed());
            map.put("last_modified_date", memberSubscription.getLastModifiedDate());

            mapArrayList.add(map);
        }

        namedParameterJdbcTemplate.batchUpdate(sql, mapArrayList.toArray(new Map[0]));

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

    @Override
    public void insertLoginHistory(LoginHistory loginHistory) {

        String sql = """
                INSERT INTO login_history(member_id, email, login_time, user_agent, ip_address, success)
                VALUES (:memberId, :email, :loginTime, :userAgent, :ipAddress, :success)
                """;

        Map<String, Object> map = new HashMap<>();
        map.put("memberId", loginHistory.getMemberId());
        map.put("email", loginHistory.getEmail());
        map.put("loginTime", loginHistory.getLoginTime());
        map.put("userAgent", loginHistory.getUserAgent());
        map.put("ipAddress", loginHistory.getIpAddress());
        map.put("success", loginHistory.getSuccess());

        namedParameterJdbcTemplate.update(sql, map);

    }

    @Override
    public List<LoginHistory> getLoginHistoryList(LoginHistoryQueryParams loginHistoryQueryParams) {

        String sql = "SELECT * FROM login_history WHERE 1=1";

        Map<String, Object> map = new HashMap<>();

        sql = addFilteringSql(sql, map, loginHistoryQueryParams);

        sql += " ORDER BY login_time DESC";

        List<LoginHistory> loginHistoryList = namedParameterJdbcTemplate.query(sql, map, new BeanPropertyRowMapper<>(LoginHistory.class));

        return loginHistoryList;
    }

    private String addFilteringSql(String sql, Map<String, Object> map, LoginHistoryQueryParams params) {
        if (params.getMemberId() != null) {
            sql += " AND member_id = :memberId";
            map.put("memberId", params.getMemberId());
        }
        if (params.getEmail() != null) {
            sql += " AND email = :email";
            map.put("email", params.getEmail());
        }
        if (params.getStartTime() != null) {
            sql += " AND login_time >= :startTime";
            map.put("startTime", params.getStartTime());
        }
        if (params.getEndTime() != null) {
            sql += " AND login_time <= :endTime";
            map.put("endTime", params.getEndTime());
        }
        return sql;
    }
}
