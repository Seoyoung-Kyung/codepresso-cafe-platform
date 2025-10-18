-- 기본 쿠폰 타입 데이터 (idempotent)
INSERT IGNORE INTO coupon_type (coupon_type, valid_period, available_menu)
        VALUES ('STAMP_REWARD', '2025-12-31 23:59:59', '전체메뉴');