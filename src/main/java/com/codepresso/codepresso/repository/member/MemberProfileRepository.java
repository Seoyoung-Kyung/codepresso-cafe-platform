package com.codepresso.codepresso.repository.member;

import com.codepresso.codepresso.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 회원 프로필 데이터 접근 레포지토리
 * JPA를 활용한 회원 프로필 데이터 CRUD 작업
 */
@Repository
public interface MemberProfileRepository extends JpaRepository<Member, Long> {

    /**
     * 회원 ID로 회원 정보 조회
     * 
     * @param memberId 조회할 회원 ID
     * @return Optional<Member> 조회된 회원 정보 (없으면 Optional.empty())
     */
    Optional<Member> findById(Long memberId);
}
