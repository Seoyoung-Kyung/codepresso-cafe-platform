package com.codepresso.codepresso.coupon.repository;

import com.codepresso.codepresso.coupon.entity.CouponType;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CouponTypeRepository extends CrudRepository<CouponType, Integer> {
    Optional<CouponType> findByCouponType(String couponType);
}
