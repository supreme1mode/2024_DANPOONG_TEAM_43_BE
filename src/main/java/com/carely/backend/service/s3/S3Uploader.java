package com.carely.backend.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Uploader {
    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        // 파일 이름에서 공백을 제거한 새로운 파일 이름 생성
        //String originalFileName = multipartFile.getOriginalFilename();

        // UUID를 파일명에 추가
        String uuid = UUID.randomUUID().toString();

        //String uniqueFileName = uuid + "_" + originalFileName.replaceAll("\\s", "_");

        String fileName = dirName + "/" + uuid;
        log.info("fileName: " + fileName);
        // S3에 파일 업로드
        return putS3(multipartFile, fileName);
    }

    private String putS3(MultipartFile multipartFile, String fileName) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, multipartFile.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3.getUrl(bucket, fileName).toString();
    }
    public void deleteFile(String fileName) {
        try {
            // URL 디코딩을 통해 원래의 파일 이름을 가져옴
            String decodedFileName = URLDecoder.decode(fileName, "UTF-8");
            log.info("Deleting file from S3: " + decodedFileName);
            amazonS3.deleteObject(bucket, decodedFileName);
        } catch (UnsupportedEncodingException e) {
            log.error("Error while decoding the file name: {}", e.getMessage());
        }
    }
}
