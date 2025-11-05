package com.codepresso.codepresso.coupon.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StampResponse {
    private Integer currentStamp;          // 0~10 사이의 현재 스탬프 수
}
