package com.williamhsieh.fruitandessence.rowmapper;

import com.williamhsieh.fruitandessence.model.MemberSubscription;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberSubscriptionRowMapper implements RowMapper<MemberSubscription> {

    @Override
    public MemberSubscription mapRow(ResultSet resultSet, int i) throws SQLException {

        MemberSubscription memberSubscription = new MemberSubscription();
        memberSubscription.setMemberSubscriptionId(resultSet.getInt("memberSubscriptionId"));
        memberSubscription.setMemberId(resultSet.getInt("memberId"));
        memberSubscription.setSubscriptionType(resultSet.getString("subscriptionType"));
        memberSubscription.setSubscribed(resultSet.getBoolean("subscribed"));
        memberSubscription.setCreatedDate(resultSet.getTimestamp("created_date").toLocalDateTime());
        memberSubscription.setLastModifiedDate(resultSet.getTimestamp("last_modified_date").toLocalDateTime());

        return memberSubscription;
    }
}
