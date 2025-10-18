INSERT INTO nutrition_info (product_id, calories, carbohydrate, protein, fat, saturated_fat, trans_fat, cholesterol, sodium, sugar) VALUES
-- 커피류 (1-23)
(1, 8, 1.2, 0.3, 0.1, 0.0, 0.0, 0, 2, 0.0),    -- 아메리카노
(2, 12, 1.8, 0.4, 0.1, 0.0, 0.0, 0, 3, 0.0),   -- 시그니처아메리카노
(3, 8, 1.2, 0.3, 0.1, 0.0, 0.0, 0, 2, 0.0),    -- 디카페인 아메리카노
(4, 85, 20.5, 0.8, 0.2, 0.0, 0.0, 0, 8, 18.2), -- 바나리카노
(5, 85, 20.5, 0.8, 0.2, 0.0, 0.0, 0, 8, 18.2), -- 디카페인 바나리카노
(6, 65, 15.8, 0.5, 0.2, 0.0, 0.0, 0, 5, 14.2), -- 핑크아메리카노
(7, 65, 15.8, 0.5, 0.2, 0.0, 0.0, 0, 5, 14.2), -- 디카페인 핑크아메리카노
(8, 168, 18.5, 8.2, 6.8, 4.2, 0.0, 25, 95, 16.5), -- 핑크카페라떼
(9, 168, 18.5, 8.2, 6.8, 4.2, 0.0, 25, 95, 16.5), -- 디카페인 핑크카페라떼
(10, 45, 11.2, 0.4, 0.1, 0.0, 0.0, 0, 3, 10.8), -- 허니아메리카노
(11, 45, 11.2, 0.4, 0.1, 0.0, 0.0, 0, 3, 10.8), -- 디카페인 허니아메리카노
(12, 95, 23.8, 0.8, 0.3, 0.0, 0.0, 0, 12, 22.5), -- 유자셔벗아메리카노
(13, 95, 23.8, 0.8, 0.3, 0.0, 0.0, 0, 12, 22.5), -- 디카페인 유자셔벗아메리카노
(14, 25, 6.2, 0.4, 0.1, 0.0, 0.0, 0, 3, 2.8), -- 제로슈가 스위트아메리카노
(15, 25, 6.2, 0.4, 0.1, 0.0, 0.0, 0, 3, 2.8), -- 디카페인 제로슈가 스위트아메리카노
(16, 35, 8.5, 0.6, 0.1, 0.0, 0.0, 0, 5, 7.8), -- 헛개리카노
(17, 35, 8.5, 0.6, 0.1, 0.0, 0.0, 0, 5, 7.8), -- 디카페인 헛개리카노
(18, 52, 12.8, 0.9, 0.2, 0.0, 0.0, 0, 7, 11.7), -- 빅바나 헛개리카노
(19, 52, 12.8, 0.9, 0.2, 0.0, 0.0, 0, 7, 11.7), -- 빅바나 디카페인 헛개리카노
(20, 72, 8.5, 4.2, 3.8, 2.4, 0.0, 15, 52, 7.8), -- 화이트아메리카노
(21, 72, 8.5, 4.2, 3.8, 2.4, 0.0, 15, 52, 7.8), -- 디카페인 화이트아메리카노
(22, 128, 12.8, 6.5, 5.8, 3.6, 0.0, 22, 78, 11.8), -- 클래식밀크커피
(23, 184, 18.5, 9.2, 8.2, 5.1, 0.0, 32, 112, 17.2), -- 빅바나 클래식밀크커피

-- 라떼류 (24-82)
(24, 155, 15.2, 8.5, 6.8, 4.2, 0.0, 28, 95, 14.2), -- 카페라떼
(25, 155, 15.2, 8.5, 6.8, 4.2, 0.0, 28, 95, 14.2), -- 디카페인 카페라떼
(26, 225, 22.5, 12.2, 9.8, 6.1, 0.0, 42, 138, 20.8), -- 빅바나 카페라떼
(27, 225, 22.5, 12.2, 9.8, 6.1, 0.0, 42, 138, 20.8), -- 빅바나 디카페인 카페라떼
(28, 188, 18.5, 9.2, 8.8, 5.5, 0.0, 32, 115, 17.2), -- 크리미라떼
(29, 188, 18.5, 9.2, 8.8, 5.5, 0.0, 32, 115, 17.2), -- 디카페인 크리미라떼
(30, 268, 26.8, 13.2, 12.5, 7.8, 0.0, 45, 165, 24.8), -- 빅바나 크리미라떼
(31, 268, 26.8, 13.2, 12.5, 7.8, 0.0, 45, 165, 24.8), -- 빅바나 디카페인 크리미라떼
(32, 198, 20.5, 9.8, 9.2, 5.8, 0.0, 35, 125, 18.8), -- 헛개크리미라떼
(33, 198, 20.5, 9.8, 9.2, 5.8, 0.0, 35, 125, 18.8), -- 디카페인 헛개크리미라떼
(34, 185, 22.5, 8.8, 7.2, 4.5, 0.0, 30, 105, 21.2), -- 바닐라라떼
(35, 142, 18.2, 8.8, 7.2, 4.5, 0.0, 30, 105, 12.5), -- 저당 바닐라라떼
(36, 185, 22.5, 8.8, 7.2, 4.5, 0.0, 30, 105, 21.2), -- 디카페인 바닐라라떼
(37, 142, 18.2, 8.8, 7.2, 4.5, 0.0, 30, 105, 12.5), -- 저당 디카페인 바닐라라떼
(38, 268, 32.5, 12.8, 10.5, 6.5, 0.0, 45, 152, 30.8), -- 빅바나 바닐라라떼
(39, 205, 26.2, 12.8, 10.5, 6.5, 0.0, 45, 152, 18.2), -- 저당 빅바나 바닐라라떼
(40, 268, 32.5, 12.8, 10.5, 6.5, 0.0, 45, 152, 30.8), -- 빅바나 디카페인 바닐라라떼
(41, 205, 26.2, 12.8, 10.5, 6.5, 0.0, 45, 152, 18.2), -- 저당 빅바나 디카페인 바닐라라떼
(42, 225, 28.5, 9.2, 8.8, 5.5, 0.0, 35, 185, 26.8), -- 스위티소금라떼
(43, 225, 28.5, 9.2, 8.8, 5.5, 0.0, 35, 185, 26.8), -- 디카페인 스위티소금라떼
(44, 242, 32.8, 9.5, 8.2, 5.1, 0.0, 32, 125, 31.5), -- 연유라떼
(45, 242, 32.8, 9.5, 8.2, 5.1, 0.0, 32, 125, 31.5), -- 디카페인 연유라떼
(46, 268, 35.2, 10.5, 10.8, 6.8, 0.0, 38, 145, 32.8), -- 카페모카
(47, 268, 35.2, 10.5, 10.8, 6.8, 0.0, 38, 145, 32.8), -- 디카페인 카페모카
(48, 252, 32.8, 9.8, 9.5, 5.9, 0.0, 35, 135, 30.5), -- 밀크카라멜마키아또
(49, 252, 32.8, 9.8, 9.5, 5.9, 0.0, 35, 135, 30.5), -- 디카페인 밀크카라멜마키아또
(50, 198, 24.2, 9.2, 8.1, 5.0, 0.0, 32, 118, 22.8), -- 시나몬라떼
(51, 198, 24.2, 9.2, 8.1, 5.0, 0.0, 32, 118, 22.8), -- 디카페인 시나몬라떼
(52, 235, 26.8, 11.2, 11.8, 6.8, 0.0, 35, 125, 24.5), -- 피스타치오카페라떼
(53, 235, 26.8, 11.2, 11.8, 6.8, 0.0, 35, 125, 24.5); -- 디카페인 피스타치오카페라떼

-- 계속해서 나머지 제품들의 영양정보 추가
INSERT INTO nutrition_info (product_id, calories, carbohydrate, protein, fat, saturated_fat, trans_fat, cholesterol, sodium, sugar) VALUES
-- 에스프레소류 (54-61)
(54, 15, 2.1, 0.8, 0.3, 0.1, 0.0, 0, 5, 0.0), -- 에스프레소
(55, 15, 2.1, 0.8, 0.3, 0.1, 0.0, 0, 5, 0.0), -- 디카페인 에스프레소
(56, 12, 1.8, 0.6, 0.2, 0.0, 0.0, 0, 8, 0.0), -- 콜드브루
(57, 18, 2.5, 0.9, 0.3, 0.0, 0.0, 0, 12, 0.0), -- 빅바나 콜드브루
(58, 95, 8.5, 5.2, 4.8, 3.0, 0.0, 18, 65, 7.8), -- 콜드브루 라떼
(59, 125, 18.2, 5.5, 5.1, 3.2, 0.0, 18, 68, 16.8), -- 바닐라 콜드브루
(60, 89, 12.5, 5.5, 5.1, 3.2, 0.0, 18, 68, 8.2), -- 저당 바닐라 콜드브루
(61, 158, 22.8, 6.2, 6.5, 4.1, 0.0, 22, 78, 21.2), -- 돌체 콜드브루

-- 특별 라떼류 (62-82)
(62, 168, 22.5, 7.8, 5.8, 3.6, 0.0, 22, 88, 20.2), -- 제주말차라떼
(63, 285, 38.5, 8.2, 10.5, 6.5, 0.0, 35, 125, 36.2), -- 멜론라떼
(64, 258, 35.8, 8.8, 9.2, 5.8, 0.0, 32, 118, 33.5), -- 딸기크리미라떼
(65, 195, 26.2, 8.5, 7.2, 4.5, 0.0, 28, 98, 24.8), -- 제주말차크리미라떼
(66, 188, 24.8, 9.8, 7.5, 4.7, 0.0, 35, 285, 22.5), -- 단짠치즈티라떼
(67, 212, 35.2, 8.2, 6.8, 4.2, 0.0, 25, 95, 32.8), -- 헛개버블티
(68, 175, 28.5, 7.5, 5.8, 3.6, 0.0, 22, 85, 26.2), -- 영암고구마라떼
(69, 158, 22.8, 8.8, 5.2, 3.2, 0.0, 25, 95, 20.5), -- 미숫가루라떼
(70, 165, 24.2, 8.2, 5.8, 3.6, 0.0, 28, 98, 22.8), -- 얼그레이밀크티
(71, 228, 32.8, 12.5, 7.5, 4.6, 0.0, 35, 135, 29.5), -- 빅바나 미숫가루라떼
(72, 125, 18.5, 8.2, 5.8, 3.6, 0.0, 28, 98, 12.2), -- 저당 얼그레이밀크티
(73, 198, 32.5, 8.8, 6.5, 4.1, 0.0, 28, 105, 30.2), -- 얼그레이버블티
(74, 148, 24.8, 8.8, 6.5, 4.1, 0.0, 28, 105, 18.5), -- 저당 얼그레이버블티
(75, 185, 28.5, 8.2, 6.8, 4.2, 0.0, 25, 98, 26.8), -- 흑당밀크티
(76, 218, 36.8, 8.8, 7.2, 4.5, 0.0, 28, 105, 34.5), -- 흑당버블티
(77, 285, 32.8, 12.5, 15.8, 9.8, 0.0, 48, 125, 30.5), -- 리얼초코
(78, 412, 47.5, 18.2, 22.5, 14.2, 0.0, 68, 182, 44.2), -- 빅바나 리얼초코
(79, 248, 28.5, 10.2, 12.8, 7.8, 0.0, 38, 115, 26.2), -- 토피넛라떼
(80, 235, 26.8, 11.2, 11.8, 6.8, 0.0, 35, 125, 24.5), -- 피스타치오라떼
(81, 258, 35.8, 8.8, 9.2, 5.8, 0.0, 32, 118, 33.5), -- 딸기라떼
(82, 195, 28.8, 8.2, 7.5, 4.7, 0.0, 28, 105, 26.5), -- 홍시라떼

-- 주스류 (83-89)
(83, 85, 20.5, 1.8, 0.3, 0.0, 0.0, 0, 15, 18.2), -- 토마토생과일쥬스
(84, 125, 30.5, 1.2, 0.2, 0.0, 0.0, 0, 8, 28.8), -- 딸기쥬스
(85, 148, 36.2, 1.5, 0.3, 0.0, 0.0, 0, 5, 34.5), -- 망고쥬스
(86, 138, 33.8, 1.8, 0.5, 0.0, 0.0, 0, 12, 31.2), -- 홍시쥬스
(87, 165, 28.5, 6.8, 3.2, 2.0, 0.0, 15, 65, 26.2), -- 복숭아요거트드링크
(88, 148, 25.8, 6.5, 3.0, 1.9, 0.0, 14, 62, 23.5), -- 딸기요거트드링크
(89, 125, 18.5, 7.2, 3.5, 2.2, 0.0, 16, 68, 16.8), -- 플레인요거트드링크

-- 스무디류 (90-111)
(90, 185, 32.5, 8.2, 4.8, 3.0, 0.0, 18, 85, 30.2), -- 딸기요거트스무디
(91, 158, 24.8, 8.8, 5.2, 3.2, 0.0, 22, 95, 22.5), -- 플레인요거트스무디
(92, 142, 26.5, 7.8, 4.2, 2.6, 0.0, 16, 75, 24.2), -- 레몬요거트스무디
(93, 195, 35.2, 8.5, 5.5, 3.4, 0.0, 20, 88, 32.8), -- 망고요거트스무디
(94, 158, 38.5, 2.2, 0.8, 0.0, 0.0, 0, 15, 36.2), -- 탐라는감귤스무디
(95, 268, 42.5, 8.8, 8.5, 5.2, 0.0, 32, 125, 38.8), -- 제주말차바나치노
(96, 125, 30.8, 1.8, 0.5, 0.0, 0.0, 0, 12, 28.5), -- 꿀배스무디
(97, 148, 35.2, 2.2, 0.8, 0.0, 0.0, 0, 18, 32.8), -- 꿀자몽스무디
(98, 168, 28.5, 7.5, 4.2, 2.6, 0.0, 18, 78, 26.2), -- 토마토요구르트스무디
(99, 225, 35.8, 8.2, 7.8, 4.8, 0.0, 28, 158, 33.5), -- 밀크소다바나치노
(100, 185, 28.5, 6.8, 6.2, 3.8, 0.0, 22, 95, 26.8), -- 콜드브루바나치노
(101, 485, 68.5, 12.5, 18.8, 11.5, 0.0, 65, 285, 64.2), -- 초콜릿칩쉐이크
(102, 448, 58.8, 14.2, 17.5, 10.8, 0.0, 58, 225, 55.5), -- 커피칩쉐이크
(103, 325, 52.8, 10.8, 11.5, 7.2, 0.0, 45, 185, 49.5), -- 망고치즈바나치노
(104, 465, 65.8, 12.2, 17.8, 11.2, 0.0, 62, 275, 62.5), -- 초코쉐이크
(105, 385, 55.2, 10.5, 14.8, 9.2, 0.0, 52, 195, 52.8), -- 바닐라쉐이크
(106, 135, 32.8, 2.5, 0.8, 0.0, 0.0, 0, 18, 30.5), -- 제주청귤레몬스무디
(107, 168, 40.5, 2.8, 1.2, 0.0, 0.0, 0, 15, 38.2), -- 딸기복숭아스무디
(108, 145, 35.2, 2.2, 0.8, 0.0, 0.0, 0, 12, 33.5), -- 딸기스무디
(109, 165, 40.2, 2.5, 1.0, 0.0, 0.0, 0, 8, 38.8), -- 망고스무디
(110, 412, 58.5, 11.8, 15.2, 9.5, 0.0, 48, 245, 55.2), -- 쿠앤크바나치노
(111, 395, 52.8, 13.5, 14.8, 9.2, 0.0, 45, 215, 49.8), -- 자바칩바나치노

-- 음료/차류 (112-145)
(112, 95, 22.8, 1.5, 0.2, 0.0, 0.0, 0, 8, 21.5), -- 머스캣블랙티
(113, 125, 28.5, 4.2, 2.8, 1.8, 0.0, 12, 65, 26.2), -- 요구르트레몬에이드
(114, 85, 20.8, 0.5, 0.1, 0.0, 0.0, 0, 25, 19.5), -- 라임민트스파클러
(115, 118, 29.5, 0.2, 0.1, 0.0, 0.0, 0, 35, 28.8), -- 체리콕
(116, 88, 21.2, 1.2, 0.2, 0.0, 0.0, 0, 12, 19.8), -- 유자민트티
(117, 75, 18.5, 0.8, 0.1, 0.0, 0.0, 0, 5, 17.2), -- 애플캐모마일스위티
(118, 148, 35.8, 2.2, 1.5, 0.0, 0.0, 0, 28, 33.5), -- 딸기코코레모네이드
(119, 25, 6.2, 0.8, 0.1, 0.0, 0.0, 0, 15, 5.5), -- 헛개차
(120, 35, 8.8, 1.2, 0.2, 0.0, 0.0, 0, 22, 7.8), -- 빅바나 헛개차
(121, 92, 22.5, 1.5, 0.3, 0.0, 0.0, 0, 18, 20.8), -- 탐라는감귤티
(122, 125, 30.8, 1.8, 0.5, 0.0, 0.0, 0, 22, 28.5), -- 허니유자티
(123, 12, 2.8, 0.2, 0.1, 0.0, 0.0, 0, 8, 0.5), -- 제로슈가 레몬아이스티
(124, 18, 4.2, 0.3, 0.1, 0.0, 0.0, 0, 12, 0.8), -- 빅바나 제로슈가 레몬아이스티
(125, 15, 3.5, 0.2, 0.1, 0.0, 0.0, 0, 8, 0.8), -- 제로슈가 복숭아아이스티
(126, 22, 5.2, 0.3, 0.1, 0.0, 0.0, 0, 12, 1.2), -- 빅바나 제로슈가 복숭아아이스티
(127, 68, 16.8, 0.5, 0.1, 0.0, 0.0, 0, 12, 15.8), -- 망고홍차아이스티
(128, 125, 30.5, 1.2, 0.3, 0.0, 0.0, 0, 18, 28.8), -- 플러스 망고홍차아이스티
(129, 95, 23.2, 0.8, 0.2, 0.0, 0.0, 0, 15, 21.8), -- NEW복숭아아이스티
(130, 125, 30.8, 2.5, 0.5, 0.0, 0.0, 0, 25, 28.5), -- 얼음동동식혜
(131, 185, 45.2, 3.8, 0.8, 0.0, 0.0, 0, 38, 42.5), -- 빅바나 얼음동동식혜
(132, 105, 25.2, 1.2, 0.3, 0.0, 0.0, 0, 15, 23.8), -- 자몽허니블랙티
(133, 85, 20.5, 1.5, 0.2, 0.0, 0.0, 0, 18, 19.2), -- 문경오미자티
(134, 78, 18.8, 1.2, 0.2, 0.0, 0.0, 0, 12, 17.5), -- 제주청귤티
(135, 115, 28.2, 1.5, 0.3, 0.0, 0.0, 0, 25, 26.8), -- 제주청귤에이드
(136, 158, 38.5, 2.2, 0.8, 0.0, 0.0, 0, 28, 36.2), -- 복숭아에이드
(137, 125, 30.8, 1.8, 0.5, 0.0, 0.0, 0, 22, 28.5), -- 레몬에이드
(138, 118, 28.5, 1.5, 0.4, 0.0, 0.0, 0, 25, 26.8), -- 자몽에이드
(139, 135, 33.2, 1.8, 0.6, 0.0, 0.0, 0, 22, 31.5), -- 청포도에이드
(140, 98, 23.8, 1.2, 0.3, 0.0, 0.0, 0, 18, 22.5), -- 레몬티
(141, 108, 26.2, 1.5, 0.4, 0.0, 0.0, 0, 20, 24.8), -- 자몽티
(142, 5, 1.2, 0.2, 0.0, 0.0, 0.0, 0, 2, 0.0), -- 캐모마일
(143, 8, 1.8, 0.3, 0.1, 0.0, 0.0, 0, 3, 0.0), -- 히비스커스
(144, 6, 1.5, 0.2, 0.0, 0.0, 0.0, 0, 2, 0.0), -- 얼그레이
(145, 4, 1.0, 0.1, 0.0, 0.0, 0.0, 0, 1, 0.0), -- 페퍼민트

-- 베이커리류 (146-174)
(146, 485, 68.5, 12.8, 18.5, 8.2, 0.2, 45, 385, 32.5), -- 허니브레드
(147, 385, 52.8, 8.5, 15.2, 9.8, 0.3, 35, 295, 28.5), -- 더블크로플
(148, 155, 2.2, 12.8, 10.5, 3.2, 0.0, 285, 185, 0.8), -- 아침란
(149, 325, 48.5, 6.8, 12.5, 7.8, 0.2, 25, 245, 28.2), -- 멜론크림빵
(150, 285, 42.5, 8.2, 9.8, 6.2, 0.1, 35, 165, 38.5), -- 떠먹는 요거트딸기케이크
(151, 365, 48.2, 7.5, 16.8, 10.2, 0.2, 55, 225, 32.8), -- 크레이프 롤케이크
(152, 425, 38.5, 22.8, 21.5, 8.5, 0.1, 65, 885, 8.2), -- 소불고기치즈파니니
(153, 385, 35.2, 28.5, 16.8, 6.2, 0.1, 75, 825, 6.5), -- 매콤닭가슴살파니니
(154, 295, 28.5, 18.2, 14.8, 8.5, 0.2, 45, 685, 5.8), -- 크로크무슈
(155, 248, 22.5, 15.2, 12.5, 4.8, 0.1, 225, 485, 3.2), -- 에그듬뿍모닝샌드위치
(156, 265, 24.8, 16.8, 13.2, 5.2, 0.1, 185, 525, 4.5), -- 게살듬뿍모닝샌드위치
(157, 185, 32.5, 6.8, 4.2, 2.8, 0.1, 95, 125, 18.5), -- 클래식카스테라
(158, 465, 45.2, 25.8, 22.5, 8.8, 0.2, 85, 965, 8.8), -- 핫치킨부리또
(159, 325, 42.8, 8.5, 14.2, 8.8, 0.3, 25, 685, 6.2), -- 버터리소금빵
(160, 285, 26.5, 16.8, 14.5, 7.2, 0.2, 45, 585, 4.8), -- 햄치즈잉글리시머핀
(161, 225, 42.5, 8.2, 2.8, 0.5, 0.0, 0, 485, 2.5), -- 플레인베이글
(162, 238, 43.8, 9.2, 3.5, 0.8, 0.0, 0, 525, 3.2), -- 어니언베이글
(163, 285, 3.2, 6.5, 28.5, 18.2, 0.8, 85, 385, 2.8), -- 폴리크림치즈
(164, 165, 22.5, 3.2, 7.8, 4.2, 0.1, 15, 45, 20.2), -- 유자마카롱
(165, 175, 24.2, 3.8, 8.2, 4.8, 0.1, 18, 52, 21.8), -- 말차초코마카롱
(166, 158, 21.8, 3.5, 7.2, 4.5, 0.1, 16, 48, 19.5), -- 순우유마카롱
(167, 168, 23.5, 3.8, 7.8, 4.6, 0.1, 17, 50, 21.2), -- 블루베리요거트마카롱
(168, 185, 25.8, 4.2, 8.5, 5.2, 0.1, 20, 58, 23.5), -- 초코가나슈마카롱
(169, 285, 38.5, 4.8, 12.5, 5.2, 0.2, 25, 185, 22.8), -- 아메리칸쿠키
(170, 325, 42.8, 5.5, 15.8, 8.5, 0.3, 28, 225, 28.5), -- 더블초코칩쿠키
(171, 268, 42.5, 5.8, 9.8, 3.8, 0.1, 15, 165, 18.2), -- 오트밀크랜베리쿠키
(172, 485, 58.5, 12.8, 22.5, 6.8, 0.2, 35, 285, 42.5), -- 말렌카케이크호두
(173, 465, 62.8, 8.5, 18.8, 11.2, 0.3, 25, 245, 48.2), -- 말렌카케이크코코아
(174, 0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0.0), -- 베이커리 박스

-- 세트메뉴류 (175-182)
(175, 163, 4.2, 14.8, 10.7, 3.2, 0.0, 285, 187, 1.8), -- 아침란 세트 (아침란+아메리카노)
(176, 580, 54.7, 31.3, 28.3, 12.7, 0.1, 93, 980, 22.4), -- 소불고기치즈파니니 세트
(177, 540, 51.4, 37.0, 23.6, 10.4, 0.1, 103, 920, 20.7), -- 매콤 닭가슴살파니니 세트
(178, 450, 44.7, 26.7, 21.6, 12.7, 0.2, 73, 780, 19.0), -- 크로크무슈 세트
(179, 620, 61.4, 34.3, 29.3, 12.0, 0.2, 113, 1060, 23.0), -- 핫치킨부리또 세트
(180, 380, 58.7, 16.7, 9.6, 4.7, 0.0, 28, 580, 16.7), -- 플레인베이글 세트
(181, 393, 60.0, 17.7, 10.3, 5.0, 0.0, 28, 620, 17.4), -- 어니언베이글 세트
(182, 440, 42.7, 25.3, 21.3, 11.4, 0.2, 73, 680, 19.0), -- 햄치즈머핀 세트

-- 굿즈류 (183-186) - 영양정보 없음
(183, 0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0.0), -- 텀블러
(184, 0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0.0), -- 빅머그
(185, 0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0.0), -- 머그컵_화이트
(186, 0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0.0); -- 머그컵_핑크


-- 2. 카페인 데이터 업데이트
-- 커피류 (1-23)
UPDATE nutrition_info SET caffeine = 75 WHERE product_id = 1;   -- 아메리카노
UPDATE nutrition_info SET caffeine = 95 WHERE product_id = 2;   -- 시그니처아메리카노
UPDATE nutrition_info SET caffeine = 5 WHERE product_id = 3;    -- 디카페인 아메리카노
UPDATE nutrition_info SET caffeine = 75 WHERE product_id = 4;   -- 바나리카노
UPDATE nutrition_info SET caffeine = 5 WHERE product_id = 5;    -- 디카페인 바나리카노
UPDATE nutrition_info SET caffeine = 75 WHERE product_id = 6;   -- 핑크아메리카노
UPDATE nutrition_info SET caffeine = 5 WHERE product_id = 7;    -- 디카페인 핑크아메리카노
UPDATE nutrition_info SET caffeine = 75 WHERE product_id = 8;   -- 핑크카페라떼
UPDATE nutrition_info SET caffeine = 5 WHERE product_id = 9;    -- 디카페인 핑크카페라떼
UPDATE nutrition_info SET caffeine = 75 WHERE product_id = 10;  -- 허니아메리카노
UPDATE nutrition_info SET caffeine = 5 WHERE product_id = 11;   -- 디카페인 허니아메리카노
UPDATE nutrition_info SET caffeine = 75 WHERE product_id = 12;  -- 유자셔벗아메리카노
UPDATE nutrition_info SET caffeine = 5 WHERE product_id = 13;   -- 디카페인 유자셔벗아메리카노
UPDATE nutrition_info SET caffeine = 75 WHERE product_id = 14;  -- 제로슈가 스위트아메리카노
UPDATE nutrition_info SET caffeine = 5 WHERE product_id = 15;   -- 디카페인 제로슈가 스위트아메리카노
UPDATE nutrition_info SET caffeine = 75 WHERE product_id = 16;  -- 헛개리카노
UPDATE nutrition_info SET caffeine = 5 WHERE product_id = 17;   -- 디카페인 헛개리카노
UPDATE nutrition_info SET caffeine = 115 WHERE product_id = 18; -- 빅바나 헛개리카노
UPDATE nutrition_info SET caffeine = 8 WHERE product_id = 19;   -- 빅바나 디카페인 헛개리카노
UPDATE nutrition_info SET caffeine = 75 WHERE product_id = 20;  -- 화이트아메리카노
UPDATE nutrition_info SET caffeine = 5 WHERE product_id = 21;   -- 디카페인 화이트아메리카노
UPDATE nutrition_info SET caffeine = 75 WHERE product_id = 22;  -- 클래식밀크커피
UPDATE nutrition_info SET caffeine = 115 WHERE product_id = 23; -- 빅바나 클래식밀크커피

-- 라떼류 (24-53)
UPDATE nutrition_info SET caffeine = 75 WHERE product_id = 24;  -- 카페라떼
UPDATE nutrition_info SET caffeine = 5 WHERE product_id = 25;   -- 디카페인 카페라떼
UPDATE nutrition_info SET caffeine = 115 WHERE product_id = 26; -- 빅바나 카페라떼
UPDATE nutrition_info SET caffeine = 8 WHERE product_id = 27;   -- 빅바나 디카페인 카페라떼
UPDATE nutrition_info SET caffeine = 75 WHERE product_id = 28;  -- 크리미라떼
UPDATE nutrition_info SET caffeine = 5 WHERE product_id = 29;   -- 디카페인 크리미라떼
UPDATE nutrition_info SET caffeine = 115 WHERE product_id = 30; -- 빅바나 크리미라떼
UPDATE nutrition_info SET caffeine = 8 WHERE product_id = 31;   -- 빅바나 디카페인 크리미라떼
UPDATE nutrition_info SET caffeine = 75 WHERE product_id = 32;  -- 헛개크리미라떼
UPDATE nutrition_info SET caffeine = 5 WHERE product_id = 33;   -- 디카페인 헛개크리미라떼
UPDATE nutrition_info SET caffeine = 75 WHERE product_id = 34;  -- 바닐라라떼
UPDATE nutrition_info SET caffeine = 75 WHERE product_id = 35;  -- 저당 바닐라라떼
UPDATE nutrition_info SET caffeine = 5 WHERE product_id = 36;   -- 디카페인 바닐라라떼
UPDATE nutrition_info SET caffeine = 5 WHERE product_id = 37;   -- 저당 디카페인 바닐라라떼
UPDATE nutrition_info SET caffeine = 115 WHERE product_id = 38; -- 빅바나 바닐라라떼
UPDATE nutrition_info SET caffeine = 115 WHERE product_id = 39; -- 저당 빅바나 바닐라라떼
UPDATE nutrition_info SET caffeine = 8 WHERE product_id = 40;   -- 빅바나 디카페인 바닐라라떼
UPDATE nutrition_info SET caffeine = 8 WHERE product_id = 41;   -- 저당 빅바나 디카페인 바닐라라떼
UPDATE nutrition_info SET caffeine = 75 WHERE product_id = 42;  -- 스위티소금라떼
UPDATE nutrition_info SET caffeine = 5 WHERE product_id = 43;   -- 디카페인 스위티소금라떼
UPDATE nutrition_info SET caffeine = 75 WHERE product_id = 44;  -- 연유라떼
UPDATE nutrition_info SET caffeine = 5 WHERE product_id = 45;   -- 디카페인 연유라떼
UPDATE nutrition_info SET caffeine = 75 WHERE product_id = 46;  -- 카페모카
UPDATE nutrition_info SET caffeine = 5 WHERE product_id = 47;   -- 디카페인 카페모카
UPDATE nutrition_info SET caffeine = 75 WHERE product_id = 48;  -- 밀크카라멜마키아또
UPDATE nutrition_info SET caffeine = 5 WHERE product_id = 49;   -- 디카페인 밀크카라멜마키아또
UPDATE nutrition_info SET caffeine = 75 WHERE product_id = 50;  -- 시나몬라떼
UPDATE nutrition_info SET caffeine = 5 WHERE product_id = 51;   -- 디카페인 시나몬라떼
UPDATE nutrition_info SET caffeine = 75 WHERE product_id = 52;  -- 피스타치오카페라떼
UPDATE nutrition_info SET caffeine = 5 WHERE product_id = 53;   -- 디카페인 피스타치오카페라떼

-- 에스프레소류 (54-61)
UPDATE nutrition_info SET caffeine = 63 WHERE product_id = 54;  -- 에스프레소
UPDATE nutrition_info SET caffeine = 3 WHERE product_id = 55;   -- 디카페인 에스프레소
UPDATE nutrition_info SET caffeine = 105 WHERE product_id = 56; -- 콜드브루
UPDATE nutrition_info SET caffeine = 155 WHERE product_id = 57; -- 빅바나 콜드브루
UPDATE nutrition_info SET caffeine = 105 WHERE product_id = 58; -- 콜드브루 라떼
UPDATE nutrition_info SET caffeine = 105 WHERE product_id = 59; -- 바닐라 콜드브루
UPDATE nutrition_info SET caffeine = 105 WHERE product_id = 60; -- 저당 바닐라 콜드브루
UPDATE nutrition_info SET caffeine = 105 WHERE product_id = 61; -- 돌체 콜드브루

-- 특별 라떼류 (62-82)
UPDATE nutrition_info SET caffeine = 35 WHERE product_id = 62;  -- 제주말차라떼 (말차 카페인)
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 63;   -- 멜론라떼
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 64;   -- 딸기크리미라떼
UPDATE nutrition_info SET caffeine = 35 WHERE product_id = 65;  -- 제주말차크리미라떼
UPDATE nutrition_info SET caffeine = 25 WHERE product_id = 66;  -- 단짠치즈티라떼 (홍차 카페인)
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 67;   -- 헛개버블티
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 68;   -- 영암고구마라떼
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 69;   -- 미숫가루라떼
UPDATE nutrition_info SET caffeine = 25 WHERE product_id = 70;  -- 얼그레이밀크티
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 71;   -- 빅바나 미숫가루라떼
UPDATE nutrition_info SET caffeine = 25 WHERE product_id = 72;  -- 저당 얼그레이밀크티
UPDATE nutrition_info SET caffeine = 25 WHERE product_id = 73;  -- 얼그레이버블티
UPDATE nutrition_info SET caffeine = 25 WHERE product_id = 74;  -- 저당 얼그레이버블티
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 75;   -- 흑당밀크티
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 76;   -- 흑당버블티
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 77;   -- 리얼초코
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 78;   -- 빅바나 리얼초코
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 79;   -- 토피넛라떼
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 80;   -- 피스타치오라떼
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 81;   -- 딸기라떼
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 82;   -- 홍시라떼

-- 주스류 (83-89) - 모두 카페인 없음
UPDATE nutrition_info SET caffeine = 0 WHERE product_id BETWEEN 83 AND 89;

-- 스무디류 (90-111)
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 90;   -- 딸기요거트스무디
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 91;   -- 플레인요거트스무디
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 92;   -- 레몬요거트스무디
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 93;   -- 망고요거트스무디
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 94;   -- 탐라는감귤스무디
UPDATE nutrition_info SET caffeine = 35 WHERE product_id = 95;  -- 제주말차바나치노
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 96;   -- 꿀배스무디
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 97;   -- 꿀자몽스무디
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 98;   -- 토마토요구르트스무디
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 99;   -- 밀크소다바나치노
UPDATE nutrition_info SET caffeine = 105 WHERE product_id = 100; -- 콜드브루바나치노
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 101;  -- 초콜릿칩쉐이크
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 102;  -- 커피칩쉐이크
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 103;  -- 망고치즈바나치노
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 104;  -- 초코쉐이크
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 105;  -- 바닐라쉐이크
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 106;  -- 제주청귤레몬스무디
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 107;  -- 딸기복숭아스무디
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 108;  -- 딸기스무디
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 109;  -- 망고스무디
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 110;  -- 쿠앤크바나치노
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 111;  -- 자바칩바나치노

-- 음료/차류 (112-145)
UPDATE nutrition_info SET caffeine = 25 WHERE product_id = 112; -- 머스캣블랙티
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 113;  -- 요구르트레몬에이드
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 114;  -- 라임민트스파클러
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 115;  -- 체리콕
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 116;  -- 유자민트티
UPDATE nutrition_info SET caffeine = 12 WHERE product_id = 117; -- 애플캐모마일스위티
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 118;  -- 딸기코코레모네이드
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 119;  -- 헛개차
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 120;  -- 빅바나 헛개차
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 121;  -- 탐라는감귤티
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 122;  -- 허니유자티
UPDATE nutrition_info SET caffeine = 25 WHERE product_id = 123; -- 제로슈가 레몬아이스티
UPDATE nutrition_info SET caffeine = 35 WHERE product_id = 124; -- 빅바나 제로슈가 레몬아이스티
UPDATE nutrition_info SET caffeine = 25 WHERE product_id = 125; -- 제로슈가 복숭아아이스티
UPDATE nutrition_info SET caffeine = 35 WHERE product_id = 126; -- 빅바나 제로슈가 복숭아아이스티
UPDATE nutrition_info SET caffeine = 25 WHERE product_id = 127; -- 망고홍차아이스티
UPDATE nutrition_info SET caffeine = 35 WHERE product_id = 128; -- 플러스 망고홍차아이스티
UPDATE nutrition_info SET caffeine = 25 WHERE product_id = 129; -- NEW복숭아아이스티
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 130;  -- 얼음동동식혜
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 131;  -- 빅바나 얼음동동식혜
UPDATE nutrition_info SET caffeine = 25 WHERE product_id = 132; -- 자몽허니블랙티
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 133;  -- 문경오미자티
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 134;  -- 제주청귤티
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 135;  -- 제주청귤에이드
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 136;  -- 복숭아에이드
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 137;  -- 레몬에이드
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 138;  -- 자몽에이드
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 139;  -- 청포도에이드
UPDATE nutrition_info SET caffeine = 25 WHERE product_id = 140; -- 레몬티
UPDATE nutrition_info SET caffeine = 25 WHERE product_id = 141; -- 자몽티
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 142;  -- 캐모마일
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 143;  -- 히비스커스
UPDATE nutrition_info SET caffeine = 25 WHERE product_id = 144; -- 얼그레이
UPDATE nutrition_info SET caffeine = 0 WHERE product_id = 145;  -- 페퍼민트

-- 베이커리류, 세트메뉴류, 굿즈류 (146-186) - 모두 카페인 없음
UPDATE nutrition_info SET caffeine = 0 WHERE product_id BETWEEN 146 AND 186;