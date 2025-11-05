package com.codepresso.codepresso.coupon.repository;

import com.codepresso.codepresso.coupon.entity.Stamp;
import com.codepresso.codepresso.member.entity.Member;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StampRepository extends CrudRepository<Stamp, Long> {
    /**
     * 회원의 스탬프 조회 (회원당 하나만 존재)
     * */
    Optional<Stamp> findByMember(Member member);
}
