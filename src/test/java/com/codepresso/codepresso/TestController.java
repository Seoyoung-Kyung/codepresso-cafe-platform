package com.codepresso.codepresso;

import com.codepresso.codepresso.member.entity.Member;
import com.codepresso.codepresso.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login-check")
    public Map<String, Object> checkLogin(@RequestParam String accountId) {
        Map<String, Object> result = new HashMap<>();

        try {
            Member member = memberRepository.findByAccountId(accountId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String rawPassword = "asdf1234";
            boolean matches = passwordEncoder.matches(rawPassword, member.getPassword());

            result.put("status", "success");
            result.put("accountId", member.getAccountId());
            result.put("dbPasswordHash", member.getPassword());
            result.put("passwordMatches", matches);  // ← 이게 핵심!
            result.put("role", member.getRole());

        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
        }

        return result;
    }
}