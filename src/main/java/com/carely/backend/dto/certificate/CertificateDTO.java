package com.carely.backend.dto.certificate;


import lombok.Builder;
import lombok.Data;

import lombok.Builder;
import lombok.Data;
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.Type;

@Data
@Builder
public class CertificateDTO extends DynamicStruct {
    private String certificateId; // 자격증 ID (UUID)
    private String userId; // 사용자 ID
    private String username;
    private String issueDate; // 발급일
    private int totalHours; // 총 봉사 시간
}
