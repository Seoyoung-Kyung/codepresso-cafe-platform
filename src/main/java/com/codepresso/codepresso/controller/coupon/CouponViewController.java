package com.codepresso.codepresso.controller.coupon;

import com.codepresso.codepresso.security.LoginUser;
import com.codepresso.codepresso.service.coupon.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponViewController {
}
