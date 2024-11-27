package com.carely.backend.controller.docs;

import com.carely.backend.dto.certificate.volunteerDTO;
import com.carely.backend.dto.response.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface CertificateAPI {

    @Operation(summary = "봉사 세션 추가하기", description = "채팅에서 봉사하고 봉사 승인되면 알아서 들어갑니다. 혹시나 하고 둔 거")

    public ResponseEntity<String> createVolunteerSession(@RequestBody volunteerDTO volunteer);

    @Operation(summary = "봉사 세션 불러오기(전체)", description = "봉사인지 요양보호인지 구분 없이 불러옵니다.")
    public ResponseEntity<ResponseDTO<?>> getSessionsByUserId(@PathVariable String userId);

    @Operation(summary = "봉사 세션 불러오기(요양보호/자원봉사)", description = "volunteerType에 volunteer/care_worker 각각의 타입에 해당하는 리스트가 반환됩니다.")
    public ResponseEntity<ResponseDTO<?>> getSessionsByUserAndTypeId(@PathVariable String volunteerType, @PathVariable String userId);

    @Operation(summary = "자격증 발급하기", description = "블록체인에 80시간 봉사 시간이 없으면 발급되지 않습니다.")
    public ResponseEntity<ResponseDTO<?>> issueCertificate(@PathVariable String userId) throws Exception;


    @Operation(summary = "발급된 자격증 불러오기(certificateId)", description = "자격증 번호를 통해 해당 자격증이 존재하는지 검증할 수 있습니다.")
    public ResponseEntity<ResponseDTO<?>> getCertificateById(@PathVariable String certificateId) throws Exception;

    @Operation(summary = "발급된 자격증 불러오기(userId)", description = "userId를 통해 해당 자격증이 존재하는지 검증할 수 있습니다. (자격증 번호 알게 하려고 둔 거임)")
    public ResponseEntity<ResponseDTO<?>> getCertificateByUserId(@PathVariable String userId);

    @Operation(summary = "총 봉사 시간이 얼마인지 불러오기", description = "총 봉사 시간을 받아올 수 있습니다.")
    public ResponseEntity<Integer> getTotalVolunteerHours(@RequestParam String userId);

}
