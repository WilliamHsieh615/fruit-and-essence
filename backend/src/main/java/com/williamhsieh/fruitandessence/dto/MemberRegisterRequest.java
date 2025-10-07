package com.williamhsieh.fruitandessence.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public class MemberRegisterRequest {

    @JsonProperty("account")
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 12)
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private String phone;

    @NotNull
    private LocalDate birthday;

    private List<MemberSubscriptionRequest> memberSubscriptionRequests;

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

    public List<MemberSubscriptionRequest> getMemberSubscriptionRequests() {
        return memberSubscriptionRequests;
    }

    public void setMemberSubscriptionRequests(List<MemberSubscriptionRequest> memberSubscriptionRequests) {
        this.memberSubscriptionRequests = memberSubscriptionRequests;
    }
}
