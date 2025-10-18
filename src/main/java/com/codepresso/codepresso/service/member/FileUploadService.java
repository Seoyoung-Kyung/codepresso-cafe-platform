package com.codepresso.codepresso.service.member;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 파일 업로드 서비스 (회원 프로필 이미지 전용)
 */
@Service
@Slf4j
public class FileUploadService {

    @Value("${app.file.upload.path}")
    private String uploadPath;

    /**
     * 프로필 이미지 업로드
     * @param file 업로드할 파일
     * @param memberId 회원 ID
     * @return 업로드된 파일의 URL
     */
    public String uploadProfileImage(MultipartFile file, Long memberId) throws IOException {
        // 파일 유효성 검사
        if (file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        // 이미지 파일 타입 검사
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }

        // 파일 크기 검사 (5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("파일 크기는 5MB를 초과할 수 없습니다.");
        }

        // 업로드 디렉토리 생성
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // 고유한 파일명 생성
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
        String filename = "profile_" + memberId + "_" + UUID.randomUUID().toString() + extension;

        // 파일 저장
        Path filePath = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // URL 반환
        String fileUrl = "/uploads/profile-images/" + filename;
        log.info("프로필 이미지 업로드 완료: memberId={}, filename={}, url={}", memberId, filename, fileUrl);

        return fileUrl;
    }

    /**
     * 프로필 이미지 삭제
     * @param imageUrl 삭제할 이미지 URL
     */
    public void deleteProfileImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        try {
            // URL에서 파일명 추출
            String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadPath, filename);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("프로필 이미지 삭제 완료: {}", filename);
            }
        } catch (IOException e) {
            log.error("프로필 이미지 삭제 실패: {}", imageUrl, e);
        }
    }
}