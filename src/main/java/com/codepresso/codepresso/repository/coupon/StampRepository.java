package com.codepresso.codepresso.repository.coupon;

import com.codepresso.codepresso.entity.coupon.Stamp;
import com.codepresso.codepresso.entity.member.Member;
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
