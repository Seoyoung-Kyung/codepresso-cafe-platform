package com.codepresso.codepresso.coupon.controller;


import com.codepresso.codepresso.coupon.dto.StampResponse;
import com.codepresso.codepresso.common.security.LoginUser;
import com.codepresso.codepresso.coupon.service.StampService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stamp")
@RequiredArgsConstructor
public class StampController {
    private final StampService stampService;

    @GetMapping
    public ResponseEntity<StampResponse> getMemberStamp(@AuthenticationPrincipal LoginUser loginUser) {
        Long memberId = loginUser.getMemberId();
        
        int currentStamp = stampService.getMemberStamp(memberId);
        
        StampResponse response = StampResponse.builder()
                .currentStamp(currentStamp)
                .build();
        
        return ResponseEntity.ok(response);
    }
}
