package com.codepresso.codepresso.dto.coupon;

import com.codepresso.codepresso.entity.coupon.Stamp;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class StampResponse {
    private Integer currentStamp;          // 0~10 사이의 현재 스탬프 수
}
