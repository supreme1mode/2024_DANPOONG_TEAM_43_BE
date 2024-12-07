package com.carely.backend.controller.docs;

import com.carely.backend.dto.volunteer.CreateVolunteerDTO;
import com.carely.backend.dto.volunteer.UpdateVolunteerApprovalDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface VolunteerAPI {
    @Operation(summary = "자원봉사 요청하기", description = "채팅방에서 자원봉사를 요청합니다.")
    ResponseEntity<?> createVolunteer(@RequestBody CreateVolunteerDTO dto) ;

    @Operation(summary = "자원봉사 승인하기", description = "간병인이 채팅방에서 자원봉사를 승인합니다.")
    ResponseEntity<?> approveVolunteer(@PathVariable("volunteerId") Long volunteerId,
                                       @RequestBody() UpdateVolunteerApprovalDTO updateVolunteerApprovalDTO) throws Exception;

    @Operation(summary = "자원봉사 상세보기", description = "자원봉사자, 혹은 요양보호사가 간병인에게 요청한 자원봉사를 조회합니다.")
    ResponseEntity<?> getVolunteerInfo(@PathVariable("volunteerId") Long volunteerId) ;
}
