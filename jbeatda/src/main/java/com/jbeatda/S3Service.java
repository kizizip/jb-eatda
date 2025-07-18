package com.jbeatda;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.s3.bucket}")
    private String bucket;

    // 프로필 이미지 최대 크기
    private static final int MAX_WIDTH = 500;
    private static final int MAX_HEIGHT = 500;

    // 이미지 압축 품질
    private static final float COMPRESSION_QUALITY = 0.7f;

    public String uploadFile(MultipartFile file) throws IOException {
        try {
            System.out.println("=== S3 업로드 시작 ===");
            System.out.println("파일명: " + file.getOriginalFilename());
            System.out.println("파일 크기: " + file.getSize());

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ?
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : "";

            boolean isImage = extension.equals(".jpg") || extension.equals(".jpeg") ||
                    extension.equals(".png") || extension.equals(".gif");

            // 고유한 파일명 생성
            String fileName = UUID.randomUUID().toString() + extension;
            System.out.println("생성된 파일명: " + fileName);

            InputStream inputStream;
            long contentLength;
            String contentType;

            // 이미지 파일이면 리사이징 처리
            if (isImage) {
                System.out.println("이미지 리사이징 시작...");
                ByteArrayOutputStream resizedImageStream = resizeImage(file.getInputStream(), extension);
                inputStream = new ByteArrayInputStream(resizedImageStream.toByteArray());
                contentLength = resizedImageStream.size();
                contentType = file.getContentType();
                System.out.println("리사이징 완료, 새 크기: " + contentLength);
            } else {
                inputStream = file.getInputStream();
                contentLength = file.getSize();
                contentType = file.getContentType();
            }

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(contentLength);
            metadata.setContentType(contentType);

            System.out.println("S3 업로드 요청 시작...");
            amazonS3Client.putObject(new PutObjectRequest(
                    bucket,
                    fileName,
                    inputStream,
                    metadata
            ));

            // 업로드된 파일의 URL 반환
            String fileUrl = amazonS3Client.getUrl(bucket, fileName).toString();
            System.out.println("S3 업로드 성공! URL: " + fileUrl);
            System.out.println("===================");

            return fileUrl;
        } catch (Exception e) {
            log.error("S3 파일 업로드 실패: {}", e.getMessage());
            e.printStackTrace(); // 상세 에러 로그 추가
            throw new IOException("S3 파일 업로드 실패: " + e.getMessage(), e);
        }
    }


    /**
     * 이미지 리사이징 및 압축 메소드
     */
    private ByteArrayOutputStream resizeImage(InputStream inputStream, String extension) throws IOException {
        // 원본 이미지 읽기
        BufferedImage originalImage = ImageIO.read(inputStream);

        // 원본 이미지 크기
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // 리사이징할 크기 계산
        int newWidth = originalWidth;
        int newHeight = originalHeight;

        // 최대 크기를 초과하는 경우에만 리사이징
        if (originalWidth > MAX_WIDTH || originalHeight > MAX_HEIGHT) {
            if (originalWidth > originalHeight) {
                // 가로가 더 긴 경우
                newWidth = MAX_WIDTH;
                newHeight = (int) (originalHeight * ((double) MAX_WIDTH / originalWidth));
            } else {
                // 세로가 더 긴 경우
                newHeight = MAX_HEIGHT;
                newWidth = (int) (originalWidth * ((double) MAX_HEIGHT / originalHeight));
            }
        }

        // 이미지 리사이징
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();

        // 이미지 품질 설정
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 이미지 그리기
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();

        // 압축 및 출력
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // PNG는 압축률 조정이 불가능하므로 JPG로 처리
        if (extension.equals(".png")) {
            ImageIO.write(resizedImage, "png", outputStream);
        } else {
            // JPG 압축 품질 조정
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
            if (!writers.hasNext()) {
                throw new IOException("No image writer found for JPEG format");
            }

            ImageWriter writer = writers.next();
            ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream);
            writer.setOutput(ios);

            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(COMPRESSION_QUALITY);

            writer.write(null, new IIOImage(resizedImage, null, null), param);
            writer.dispose();
            ios.close();
        }

        return outputStream;
    }

    // 이미지 삭제
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            log.warn("삭제할 파일 URL이 비어있습니다.");
            return;
        }

        String fileName = extractFileNameFromUrl(fileUrl);
        amazonS3Client.deleteObject(bucket, fileName);
        log.info("S3에서 파일 삭제 완료: {}", fileName);
    }



    private String extractFileNameFromUrl(String fileUrl) {
        String bucketUrl = amazonS3Client.getUrl(bucket, "").toString(); // ex: https://bucket-name.s3.amazonaws.com/
        return fileUrl.replace(bucketUrl, ""); // 전체 경로 추출 (폴더 포함)
    }

}