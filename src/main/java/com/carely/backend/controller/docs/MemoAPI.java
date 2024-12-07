package com.carely.backend.controller.docs;

import com.carely.backend.dto.memo.CreateMemoDTO;
import com.carely.backend.dto.response.ErrorResponseDTO;
import com.carely.backend.dto.response.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface MemoAPI {
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 메모를 작성한 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_CREATE_MEMO\", \"message\": \"메모를 성공적으로 작성했습니다.\", \"data\": { \"memoId\": 33, \"content\": \"오늘 오전 9시에 체온을 측정했으며, 37.2도로 약간의 열이 있습니다. 환자가 식사를 절반 정도만 드셨고, 평소보다 식욕이 떨어진 모습을 보였습니다. 약물 복용은 오전 8시 정시에 완료되었으며, 부작용은 없습니다. 배변은 하루 두 번으로 정상적인 상태를 보였지만, 약간의 변비 증상이 있습니다. 오늘 물리치료 중 걸음걸이가 이전보다 조금 더 안정적이었습니다. 손님(보호자)이 오후 3시에 방문하여 환자와 약 1시간 동안 대화를 나눴습니다. 환자가 오른쪽 무릎 통증을 호소하여 보호자에게 보고 후 추가 검사 필요성을 전달했습니다. 교대 시, 환자가 밤사이에 불안감을 느껴 잠을 제대로 못 잤다는 점을 다음 간병인에게 알렸습니다. 오전 목욕을 진행했으며, 대상자가 상쾌하다는 반응을 보였습니다. 환자가 '손자가 보고 싶다'라고 말씀하셔서 보호자와 영상통화를 도와드렸습니다.\", \"writerName\": \"김은서\", \"all\": \"환자의 체온은 37.2도로 약간의 열이 있습니다. 변비 증상이 있으나 배변은 정상 범위를 유지하고 있습니다. 식사량은 평소와 비슷합니다. 약물 복용은 정상적으로 이루어졌으며 부작용은 없습니다. 물리치료를 통해 걸음걸이가 안정적으로 개선되었습니다. 수면에 어려움을 겪고 있습니다. 오른쪽 무릎에 통증이 있어 추가 검사가 필요합니다. 배뇨는 하루에 세 번으로 정상입니다.\", \"healthy\": \"환자의 체온은 37.2도입니다.\", \"eat\": \"환자는 식사량을 평소와 비슷하게 유지하고 있습니다. 약물 복용은 정상적으로 이루어졌으며 부작용은 없습니다.\", \"additionalHealth\": \"오른쪽 무릎에 통증이 있어 추가 검사가 필요합니다.\", \"social\": \"환자는 손자와의 영상통화를 통해 대화를 나누었습니다.\", \"voiding\": \"배변이 정상적으로 이루어졌습니다.\" } }"))),

            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터로 메모 생성 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 400, \"code\": \"INVALID_REQUEST\", \"message\": \"요청 데이터가 잘못되었습니다.\", \"data\": null }")))
    })
    @Operation(summary = "방명록 작성하기(메모)", description = "방명록을 작성합니다. 작성한 방명록은 AI가 요약합니다.")
    ResponseEntity<ResponseDTO> createMemo(@RequestBody CreateMemoDTO createMemoDTO);

//    @Operation(summary = "방명록 조회하기", description = "다른 유저가 작성한 방명록을 조회합니다.")
//    public ResponseEntity<ResponseDTO> getMemoList(@PathVariable("userId") Long userId);

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 자원봉사 요청 상세 정보를 조회한 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_RETRIEVE_VOLUNTEER\", \"message\": \"자원봉사 요청을 성공적으로 조회했습니다.\", \"data\": { \"id\": 5, \"volunteerId\": 51, \"volunteerName\": \"김은서\", \"volunteerAge\": 0, \"phoneNum\": \"string\", \"address\": \"뒷골 1로 42\", \"startTime\": \"2024-11-17T04:33:06.36\", \"endTime\": \"2024-11-17T04:33:06.36\", \"durationHours\": 4, \"salary\": 0, \"location\": \"경기도 용인시 수지구 어딘가\", \"mainTask\": \"설거지\", \"volunteerType\": \"VOLUNTEER_REQUEST\", \"roomId\": \"c0babffb-a39b-45e5-b326-a964b49e9409\" } }"))),

    })
    @Operation(summary = "메모를 작성하지 않은 약속 조회하기", description = "메모를 작성하지 않은 약속 조회하기")
    ResponseEntity<ResponseDTO> getNotWrittenVolunteer();

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 메모를 조회한 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_RETRIEVE_MEMO\", \"message\": \"성공적으로 메모를 조회했습니다.\", \"data\": [ { \"memoId\": 32, \"content\": \"오늘 오전 9시에 체온을 측정했으며, 37.2도로 약간의 열이 있습니다. 환자가 식사를 절반 정도만 드셨고, 평소보다 식욕이 떨어진 모습을 보였습니다...\", \"writerName\": \"김은서\", \"all\": \"환자의 체온은 37.2도로 약간의 열이 있습니다...\", \"healthy\": \"환자의 체온은 37.2도로 약간의 열이 있습니다.\", \"eat\": \"식욕은 감소하였으나 식사량은 평소와 비슷하며, 영양 보충제도 섭취하였습니다...\", \"additionalHealth\": \"변비 증상이 있으나 배변은 정상 범위를 유지하고 있습니다.\", \"social\": \"수면에 어려움을 겪고 있으며 오른쪽 무릎 통증으로 추가 검사가 필요합니다...\", \"voiding\": null } ] }"))),

            @ApiResponse(responseCode = "404", description = "해당 userId를 찾을 수 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 404, \"code\": \"USER_NOT_FOUND\", \"message\": \"해당 유저를 찾을 수 없습니다.\", \"data\": null }"))),
    })
    @Operation(summary = "유저의 요약된 메모 조회하기", description = "유저의 요약된 메모 조회합니다.")
    ResponseEntity<ResponseDTO> getRecentMemo(@PathVariable("userId") Long userId);

}
