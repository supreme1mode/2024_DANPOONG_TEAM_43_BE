package com.carely.backend.controller.docs;

import com.carely.backend.dto.response.ErrorResponseDTO;
import com.carely.backend.dto.response.ResponseDTO;
import com.carely.backend.dto.volunteer.CreateVolunteerDTO;
import com.carely.backend.dto.volunteer.UpdateVolunteerApprovalDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface VolunteerAPI {
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 자원봉사 요청을 생성한 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_CREATE_VOLUNTEER\", \"message\": \"자원봉사 요청을 성공적으로 생성했습니다.\", \"data\": { \"id\": 3, \"volunteerId\": 51, \"caregiverId\": 50, \"startTime\": \"2024-11-17T04:33:06.36\", \"endTime\": \"2024-11-17T04:33:06.36\", \"durationHours\": 4, \"salary\": 0, \"location\": \"경기도 용인시 수지구 어딘가\", \"mainTask\": \"설거지\", \"volunteerType\": \"VOLUNTEER_REQUEST\", \"roomId\": \"c0babffb-a39b-45e5-b326-a964b49e9409\" } }"))),

            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터로 자원봉사 요청 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 400, \"code\": \"INVALID_REQUEST\", \"message\": \"요청 데이터가 잘못되었습니다.\", \"data\": null }"))),

            @ApiResponse(responseCode = "404", description = "해당 채팅방이나 사용자를 찾을 수 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 404, \"code\": \"NOT_FOUND\", \"message\": \"해당 채팅방 또는 사용자를 찾을 수 없습니다.\", \"data\": null }")))
    })
    @Operation(summary = "자원봉사 요청하기", description = "채팅방에서 자원봉사를 요청합니다.")
    public ResponseEntity<?> createVolunteer(@RequestBody CreateVolunteerDTO dto);

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 자원봉사 요청을 승인한 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_APPROVAL\", \"message\": \"자원봉사 요청을 성공적으로 승인했습니다.\", \"data\": { \"id\": 3, \"volunteerId\": 51, \"caregiverId\": 50, \"startTime\": \"2024-11-17T04:33:06.36\", \"endTime\": \"2024-11-17T04:33:06.36\", \"durationHours\": 4, \"salary\": 0, \"location\": \"경기도 용인시 수지구 어딘가\", \"mainTask\": \"설거지\", \"volunteerType\": \"VOLUNTEER_REQUEST\", \"roomId\": \"c0babffb-a39b-45e5-b326-a964b49e9409\" } }"))),

            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터로 자원봉사 승인 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 400, \"code\": \"INVALID_REQUEST\", \"message\": \"요청 데이터가 잘못되었습니다.\", \"data\": null }"))),

            @ApiResponse(responseCode = "404", description = "해당 자원봉사 요청을 찾을 수 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 404, \"code\": \"VOLUNTEER_NOT_FOUND\", \"message\": \"해당 자원봉사 요청을 찾을 수 없습니다.\", \"data\": null }"))),

            @ApiResponse(responseCode = "403", description = "승인 권한이 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 403, \"code\": \"FORBIDDEN\", \"message\": \"자원봉사 요청을 승인할 권한이 없습니다.\", \"data\": null }")))
    })
    @Operation(summary = "자원봉사 승인하기", description = "간병인이 채팅방에서 자원봉사를 승인합니다.")
    public ResponseEntity<?> approveVolunteer(@PathVariable("volunteerId") Long volunteerId,
                                              @RequestBody() UpdateVolunteerApprovalDTO updateVolunteerApprovalDTO) throws Exception;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 자원봉사 요청 상세 정보를 조회한 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_RETRIEVE_VOLUNTEER\", \"message\": \"자원봉사 요청을 성공적으로 조회했습니다.\", \"data\": { \"id\": 5, \"volunteerId\": 51, \"volunteerName\": \"김은서\", \"volunteerAge\": 0, \"phoneNum\": \"string\", \"address\": \"뒷골 1로 42\", \"startTime\": \"2024-11-17T04:33:06.36\", \"endTime\": \"2024-11-17T04:33:06.36\", \"durationHours\": 4, \"salary\": 0, \"location\": \"경기도 용인시 수지구 어딘가\", \"mainTask\": \"설거지\", \"volunteerType\": \"VOLUNTEER_REQUEST\", \"roomId\": \"c0babffb-a39b-45e5-b326-a964b49e9409\" } }"))),

            @ApiResponse(responseCode = "404", description = "해당 자원봉사 요청을 찾을 수 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 404, \"code\": \"VOLUNTEER_NOT_FOUND\", \"message\": \"해당 자원봉사 요청을 찾을 수 없습니다.\", \"data\": null }")))
    })
    @Operation(summary = "자원봉사 상세보기", description = "자원봉사자, 혹은 요양보호사가 간병인에게 요청한 자원봉사를 조회합니다.")
    public ResponseEntity<?> getVolunteerInfo(@PathVariable("volunteerId") Long volunteerId);
}
