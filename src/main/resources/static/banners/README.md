# Banners (정적 배너 이미지 보관 폴더)

이 폴더는 메인/프로모션 배너 이미지를 두는 정적 리소스 경로입니다.

- 정적 서빙 경로: `/banners/...`
- 실제 디렉토리: `src/main/resources/static/banners`

권장 규칙
- 포맷: `webp`(권장), `jpg`, `png`
- 크기(데스크톱 히어로): 1920x600, 1440x500, 1200x400 등 가로형 비율 유지
- 크기(모바일): 750x300 또는 1080x420 등
- 용량: 300KB 이하 권장(최대 1MB 이내)
- 파일명: `main_YYYYMMDD.webp`, `event_spring.webp` 처럼 의미+버전 포함

사용 예시 (JSP/HTML)

```html
<!-- IMG로 배치 -->
<img src="/banners/main.webp" alt="메인 배너" style="width:100%; height:auto;" />

<!-- CSS background로 배치 -->
<div class="hero-banner"></div>
<style>
.hero-banner {
  height: 360px;
  background: url('/banners/main.webp') center/cover no-repeat;
  border-radius: 16px;
}
</style>
```

캐싱 팁
- 파일 교체 시 브라우저 캐시 우회를 위해 파일명을 바꾸거나 쿼리스트링 버전을 붙여주세요.
  - 예: `/banners/main.webp?v=20250916`

