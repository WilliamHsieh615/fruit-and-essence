package com.williamhsieh.fruitandessence.controller;

import com.williamhsieh.fruitandessence.dto.MemberLoginRequest;
import com.williamhsieh.fruitandessence.dto.MemberRegisterRequest;
import com.williamhsieh.fruitandessence.dto.SubscribeRequest;
import com.williamhsieh.fruitandessence.dto.UnsubscribeRequest;
import com.williamhsieh.fruitandessence.model.Member;
import com.williamhsieh.fruitandessence.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class MemberController {

    @Autowired
    private MemberService memberService;

    @PostMapping("/members/register")
    public ResponseEntity<Member> register(@RequestBody @Valid MemberRegisterRequest memberRegisterRequest) {
        Integer memberId = memberService.register(memberRegisterRequest);

        Member member = memberService.getMemberById(memberId);

        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }

    @PostMapping("/members/login")
    public ResponseEntity<Member> login(@RequestBody @Valid MemberLoginRequest memberLoginRequest) {

        Member member = memberService.login(memberLoginRequest);

        return ResponseEntity.status(HttpStatus.OK).body(member);

    }

//    @PostMapping("/members/verify")
//    @PostMapping("/members/retrieve")

    @PostMapping("/members/subscribe")
    public ResponseEntity<String> subscribe(@RequestBody SubscribeRequest subscribeRequest) {

        String result = memberService.subscribe(subscribeRequest.getMemberId());

        return ResponseEntity.status(HttpStatus.OK).body(result);

    }

    @PostMapping("/members/unsubscribe")
    public ResponseEntity<String> unsubscribe(@RequestBody UnsubscribeRequest unsubscribeRequest) {

        String result = memberService.unsubscribe(unsubscribeRequest.getMemberId());

        return ResponseEntity.status(HttpStatus.OK).body(result);

    }

}

