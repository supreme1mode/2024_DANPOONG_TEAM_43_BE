package com.carely.backend.dto.ocr;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Setter
public class OCRResponseDto {
    private String name;
    private String birth;
    private String certificateNum;
    private String certificateType;
    private String certificateDate;
    private String certificateName;
}
