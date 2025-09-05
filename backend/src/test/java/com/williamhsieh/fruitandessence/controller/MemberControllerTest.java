package com.williamhsieh.fruitandessence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.williamhsieh.fruitandessence.dao.MemberDao;
import com.williamhsieh.fruitandessence.dto.MemberRegisterRequest;
import com.williamhsieh.fruitandessence.dto.MemberLoginRequest;
import com.williamhsieh.fruitandessence.model.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberDao memberDao;

    private ObjectMapper objectMapper = new ObjectMapper();

    // 註冊新帳號
    @Test
    public void register_success() throws Exception {
        MemberRegisterRequest memberRegisterRequest = new MemberRegisterRequest();
        memberRegisterRequest.setEmail("test1@gmail.com");
        memberRegisterRequest.setPassword("123");

        String json = objectMapper.writeValueAsString(memberRegisterRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.userId", notNullValue()))
                .andExpect(jsonPath("$.email", equalTo("test1@gmail.com")))
                .andExpect(jsonPath("$.createdDate", notNullValue()))
                .andExpect(jsonPath("$.lastModifiedDate", notNullValue()));

        // 檢查資料庫中的密碼不為明碼
        Member member = memberDao.getMemberByEmail(memberRegisterRequest.getEmail());
        assertNotEquals(memberRegisterRequest.getPassword(), member.getPassword());
    }

    @Test
    public void register_invalidEmailFormat() throws Exception {
        MemberRegisterRequest memberRegisterRequest = new MemberRegisterRequest();
        memberRegisterRequest.setEmail("3gd8e7q34l9");
        memberRegisterRequest.setPassword("123");

        String json = objectMapper.writeValueAsString(memberRegisterRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }

    @Test
    public void register_emailAlreadyExist() throws Exception {
        // 先註冊一個帳號
        MemberRegisterRequest memberRegisterRequest = new MemberRegisterRequest();
        memberRegisterRequest.setEmail("test2@gmail.com");
        memberRegisterRequest.setPassword("123");

        String json = objectMapper.writeValueAsString(memberRegisterRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(201));

        // 再次使用同個 email 註冊
        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }

    // 登入
    @Test
    public void login_success() throws Exception {
        // 先註冊新帳號
        MemberRegisterRequest memberRegisterRequest = new MemberRegisterRequest();
        memberRegisterRequest.setEmail("test3@gmail.com");
        memberRegisterRequest.setPassword("123");

        register(memberRegisterRequest);

        // 再測試登入功能
        MemberLoginRequest memberLoginRequest = new MemberLoginRequest();
        memberLoginRequest.setEmail(memberRegisterRequest.getEmail());
        memberLoginRequest.setPassword(memberRegisterRequest.getPassword());

        String json = objectMapper.writeValueAsString(memberRegisterRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.userId", notNullValue()))
                .andExpect(jsonPath("$.email", equalTo(memberRegisterRequest.getEmail())))
                .andExpect(jsonPath("$.createdDate", notNullValue()))
                .andExpect(jsonPath("$.lastModifiedDate", notNullValue()));
    }

    @Test
    public void login_wrongPassword() throws Exception {
        // 先註冊新帳號
        MemberRegisterRequest memberRegisterRequest = new MemberRegisterRequest();
        memberRegisterRequest.setEmail("test4@gmail.com");
        memberRegisterRequest.setPassword("123");

        register(memberRegisterRequest);

        // 測試密碼輸入錯誤的情況
        MemberLoginRequest memberLoginRequest = new MemberLoginRequest();
        memberLoginRequest.setEmail(memberRegisterRequest.getEmail());
        memberLoginRequest.setPassword("unknown");

        String json = objectMapper.writeValueAsString(memberLoginRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }

    @Test
    public void login_invalidEmailFormat() throws Exception {
        MemberLoginRequest memberLoginRequest = new MemberLoginRequest();
        memberLoginRequest.setEmail("hkbudsr324");
        memberLoginRequest.setPassword("123");

        String json = objectMapper.writeValueAsString(memberLoginRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }

    @Test
    public void login_emailNotExist() throws Exception {
        MemberLoginRequest memberLoginRequest = new MemberLoginRequest();
        memberLoginRequest.setEmail("unknown@gmail.com");
        memberLoginRequest.setPassword("123");

        String json = objectMapper.writeValueAsString(memberLoginRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }

    private void register(MemberRegisterRequest memberRegisterRequest) throws Exception {
        String json = objectMapper.writeValueAsString(memberRegisterRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(201));
    }

}