package com.williamhsieh.fruitandessence.model;

import java.time.LocalDateTime;

public class MemberSubscription {

    private Integer memberSubscriptionId;
    private Integer memberId;
    private String subscriptionType; // 訂閱種類，像是：VIP、PROMOTION、NEW_ARRIVAL、NEWSLETTER、SYSTEM_ALERT 不使用 enum 保持彈性
    private Boolean subscribed;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    public Integer getMemberSubscriptionId() {
        return memberSubscriptionId;
    }

    public void setMemberSubscriptionId(Integer memberSubscriptionId) {
        this.memberSubscriptionId = memberSubscriptionId;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public Boolean getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(Boolean subscribed) {
        this.subscribed = subscribed;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
