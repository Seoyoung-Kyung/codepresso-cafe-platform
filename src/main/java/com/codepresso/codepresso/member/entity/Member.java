package com.codepresso.codepresso.member.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 회원 엔티티.
 * - accountId(로그인 아이디), nickname, email에 유니크 제약을 둡니다.
 * - role은 기본값 USER로 저장되며, 필요 시 ADMIN으로 승격 가능합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "member",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_member_account", columnNames = "account_id"),
                @UniqueConstraint(name = "uq_member_nickname", columnNames = "nickname"),
                @UniqueConstraint(name = "uq_member_email", columnNames = "email")
        })
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id; // 고유 멤버 ID

    @Column(name = "account_id", nullable = false, length = 50)
    private String accountId; // 로그인 ID

    @Column(name = "password", length = 100)
    private String password; // 비밀번호

    @Column(length = 50)
    private String name; // 이름

    @Column(nullable = false, length = 50)
    private String nickname; // 닉네임

    private LocalDate birthDate; // 생년월일

    @Column(length = 20)
    private String phone; // 휴대전화

    private String email; // 이메일

    @Column(name = "profile_image")
    private String profileImage; // 프로필 이미지

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role; // USER/ADMIN

    @Column(name = "last_login_at", columnDefinition = "datetime")
    private LocalDateTime lastLoginAt; // 마지막 로그인 시간

    public enum Role { USER, ADMIN }

    @PrePersist
    void onCreate() {
        // Role default 'USER'
        if (role == null) role = Role.USER;
    }
}
