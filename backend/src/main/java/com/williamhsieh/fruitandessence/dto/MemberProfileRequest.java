package com.williamhsieh.fruitandessence.dto;

import jakarta.validation.constraints.NotBlank;

public class MemberProfileRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String phone;

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
}
