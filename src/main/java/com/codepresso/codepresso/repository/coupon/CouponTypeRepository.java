package com.codepresso.codepresso.repository.coupon;

import com.codepresso.codepresso.entity.coupon.CouponType;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CouponTypeRepository extends CrudRepository<CouponType, Integer> {
    Optional<CouponType> findByCouponType(String couponType);
}
