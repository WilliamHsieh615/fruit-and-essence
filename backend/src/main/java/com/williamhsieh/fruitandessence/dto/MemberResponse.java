package com.williamhsieh.fruitandessence.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MemberResponse {

    private Integer memberId;
    private String email;

    // 忽略密碼，不要回傳給前端
    @JsonIgnore
    private String password;

    private String name;
    private String phone;
    private LocalDate birthday;
    private List<MemberSubscriptionResponse> memberSubscriptionResponses;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public List<MemberSubscriptionResponse> getMemberSubscriptionResponses() {
        return memberSubscriptionResponses;
    }

    public void setMemberSubscriptionResponses(List<MemberSubscriptionResponse> memberSubscriptionResponses) {
        this.memberSubscriptionResponses = memberSubscriptionResponses;
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
