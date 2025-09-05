package com.williamhsieh.fruitandessence.rowmapper;

import com.williamhsieh.fruitandessence.model.Member;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberRowMapper implements RowMapper<Member> {

    public Member mapRow(ResultSet resultSet, int i) throws SQLException {

        Member member = new Member();
        member.setMemberId(resultSet.getInt("member_id"));
        member.setEmail(resultSet.getString("email"));
        member.setPassword(resultSet.getString("password"));
        member.setName(resultSet.getString("name"));
        member.setPhone(resultSet.getString("phone"));
        member.setBirthday(resultSet.getDate("birthday").toLocalDate());
        member.setCreatedDate(resultSet.getTimestamp("created_date").toLocalDateTime());
        member.setLastModifiedDate(resultSet.getTimestamp("last_modified_date").toLocalDateTime());

        return member;

    }

}
