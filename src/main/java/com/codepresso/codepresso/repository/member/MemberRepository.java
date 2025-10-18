package com.codepresso.codepresso.repository.member;

import com.codepresso.codepresso.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByAccountId(String accountId);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
    Optional<Member> findById(Long memberId);
    Optional<Member> findByAccountId(String accountId);
    Optional<Member> findByAccountIdAndEmail(String accountId, String email);
    Optional<Member> findByNicknameAndEmail(String nickname, String email);
    Optional<Member> findByEmail(String email);

    @Modifying
    @Query("update Member m SET m.lastLoginAt = :loginTime where m.id = :memberId")
    void updateLastLoginAt(@Param("memberId") Long memberId,@Param("loginTime") LocalDateTime localDateTime);
}
