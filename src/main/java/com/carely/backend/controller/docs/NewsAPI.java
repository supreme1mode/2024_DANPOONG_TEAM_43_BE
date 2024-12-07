package com.carely.backend.controller.docs;

import com.carely.backend.dto.news.CreateCommentDTO;
import com.carely.backend.dto.news.CreateNewsDTO;
import com.carely.backend.dto.response.ErrorResponseDTO;
import com.carely.backend.dto.response.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface NewsAPI {
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 소식 목록을 조회한 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_RETRIEVE_NEWS\", \"message\": \"소식을 성공적으로 조회했습니다.\", \"data\": [ { \"newsId\": 2, \"title\": \"오늘 아침은 유난히 쌀쌀했네요.\", \"content\": \"오늘 아침은 유난히 쌀쌀했네요. 환자분께서 밤새 잠을 잘 못 주무셨다고 하셔서, 아침 식사 후에 가벼운 스트레칭을 도와드렸습니다. 조금씩 기분이 좋아지시는 모습에 보람을 느꼈습니다. 이런 작은 변화들이 참 감사하게 느껴지는 하루였습니다.\", \"writerType\": \"VOLUNTEER\", \"writer\": \"서유나\", \"commentCount\": 0, \"createdAt\": \"2024-12-01T07:10:01\" }, { \"newsId\": 3, \"title\": \"우리 같이걸음 멤버들 덕분에 요즘 큰 힘을 얻고 있습니다.\", \"content\": \"우리 같이걸음 멤버들 덕분에 요즘 큰 힘을 얻고 있습니다. 산책도 좋았지만, 무엇보다 따뜻한 대화가 더 의미 있었어요. 다음 모임에서는 제가 준비한 작은 간식을 나누고 싶습니다.\", \"writerType\": \"CAREGIVER\", \"writer\": \"박은영\", \"commentCount\": 0, \"createdAt\": \"2024-12-01T07:10:01\" }, { \"newsId\": 4, \"title\": \"며칠 전 철수 님과 함께 걸었던 길이 자꾸 떠오릅니다.\", \"content\": \"며칠 전 철수 님과 함께 걸었던 길이 자꾸 떠오릅니다. 이야기를 나누는 동안 마음이 참 편안했고, 오랜만에 기분 좋은 산책을 했습니다. 다음에도 다 같이 모여 걸을 수 있는 날을 기다립니다.\", \"writerType\": \"VOLUNTEER\", \"writer\": \"정지우\", \"commentCount\": 0, \"createdAt\": \"2024-12-01T07:10:01\" }, { \"newsId\": 5, \"title\": \"오늘은 스스로를 위해 시간을 조금 내어 동네 공원을 다녀왔습니다.\", \"content\": \"간병을 하다 보면 가끔씩 마음이 무겁게 느껴질 때가 있는데, 오늘은 스스로를 위해 시간을 조금 내어 동네 공원을 다녀왔습니다. 걸으며 생각을 정리하고 나니 다시 에너지가 채워지는 느낌입니다. 간병도 중요하지만, 저 자신도 돌보는 하루가 되어야겠다고 생각했습니다.\", \"writerType\": \"CAREGIVER\", \"writer\": \"김민주\", \"commentCount\": 0, \"createdAt\": \"2024-12-01T07:10:01\" } ] }"))),
            @ApiResponse(responseCode = "401", description = "잘못된 토큰으로 요청할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(name = "INVALID_ACCESS_TOKEN", value = """
                                        {
                                            "status": 401,
                                            "code": "INVALID_ACCESS_TOKEN",
                                            "message": "유효하지 않은 토큰입니다.",
                                            "data": null
                                        }
                                        """),
                                    @ExampleObject(name = "TOKEN_EXPIRED", value = """
                                        {
                                            "status": 401,
                                            "code": "TOKEN_EXPIRED",
                                            "message": "토큰이 만료되었습니다.",
                                            "data": null
                                        }
                                        """),
                                    @ExampleObject(name = "TOKEN_MISSING", value = """
                                        {
                                            "status": 401,
                                            "code": "TOKEN_MISSING",
                                            "message": "요청 헤더에 토큰이 없습니다.",
                                            "data": null
                                        }
                                        """)
                            })),
            @ApiResponse(responseCode = "404", description = "해당 groupId에 해당하는 소식 목록을 찾을 수 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 404, \"code\": \"GROUP_NOT_FOUND\", \"message\": \"해당 groupId에 해당하는 소식 목록을 찾을 수 없습니다.\", \"data\": null }")))
    })
    @Operation(summary = "groupId에 해당하는 소식 목록 보기", description = "groupId에 해당하는 소식 목록을 조회합니다.")
    public ResponseEntity<ResponseDTO> getGroupNewsList(@PathVariable("groupId") Long groupId);

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 소식 상세 정보를 조회한 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_RETRIEVE_NEWS\", \"message\": \"소식을 성공적으로 조회했습니다.\", \"data\": { \"newsId\": 1, \"title\": \"그의 편지, 남겨진 따뜻한 기억\", \"content\": \"우연히 회사 이메일을 확인하다 받은 그의편지.\\n\\n칭찬교직원 고객의소리 였다.\\n\\n의료진의 칭찬을 도모하기위해 고객의 소리(건의함)에 입원환자,진료환자는 불만,칭찬,건의를 할수 있는 제도이다.\\n\\n건당 1만원씩 주기도 했었고(지금은 잠정중단)\\n\\n개인메일로 어떤환자가 어떤글을 썼는지 알수있다\\n\\n엄마는 입원할때마다 내이름을 적어내며 나에게 조금이라도 도움이 되었으면 ..하셨었다\\n\\n여느칭찬카드와 달리 그의 이름 석자를 보자마자 나는 순간멈칫했다.\\n\\n‘하늘나라에 잘\", \"writerType\": \"VOLUNTEER\", \"writer\": \"김세훈\", \"commentCount\": null, \"createdAt\": \"2024-12-01T07:09:00\", \"newsComments\": [ { \"newsCommentId\": 3, \"content\": \"첫 모임에 참석했던 날이 아직도 기억나요. 낯설고 어색했던 제게 다들 따뜻하게 대해주셔서 정말 놀랐습니다.\\n\\n그 이후로 제 삶에서 우리돌봄이는 없어서는 안 될 존재가 되었어요. 여러분 덕분에 저는 오늘도 하루를 잘 버텨내고 있습니다. 정말 고맙습니다.\", \"writerType\": \"CAREGIVER\", \"writer\": \"유진호\", \"createdAt\": \"2024-12-01T07:09:46\" }, { \"newsCommentId\": 1, \"content\": \"최근에 한 환자분이 퇴원하면서 건넨 짧은 편지가 계속 마음에 남습니다.\\n\\n덕분에 힘들지만 평온하게 퇴원합니다. 감사합니다.\\n\\n간병은 때로는 고되고 지치지만, 이런 짧은 감사의 표현이 모든 것을 잊게 만들더군요.\\n\\n우리 함께 이 길을 걸으며 서로에게 힘이 되어줍시다.\", \"writerType\": \"CARE_WORKER\", \"writer\": \"박진우\", \"createdAt\": \"2024-12-01T07:09:46\" }, { \"newsCommentId\": 2, \"content\": \"며칠 전 모임에서 나눴던 이야기들이 계속 생각나네요. 특히 민철 씨가 어머니를 간병하며 경험했던 이야기는 큰 울림을 주었어요. 우리 모두가 이렇게 서로를 지지하고 격려하며 함께 나아가니 참 감사하다는 생각이 듭니다.\", \"writerType\": \"CAREGIVER\", \"writer\": \"박수정\", \"createdAt\": \"2024-12-01T07:09:46\" } ] } }"))),
            @ApiResponse(responseCode = "401", description = "잘못된 토큰으로 요청할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(name = "INVALID_ACCESS_TOKEN", value = """
                                        {
                                            "status": 401,
                                            "code": "INVALID_ACCESS_TOKEN",
                                            "message": "유효하지 않은 토큰입니다.",
                                            "data": null
                                        }
                                        """),
                                    @ExampleObject(name = "TOKEN_EXPIRED", value = """
                                        {
                                            "status": 401,
                                            "code": "TOKEN_EXPIRED",
                                            "message": "토큰이 만료되었습니다.",
                                            "data": null
                                        }
                                        """),
                                    @ExampleObject(name = "TOKEN_MISSING", value = """
                                        {
                                            "status": 401,
                                            "code": "TOKEN_MISSING",
                                            "message": "요청 헤더에 토큰이 없습니다.",
                                            "data": null
                                        }
                                        """)
                            })),
            @ApiResponse(responseCode = "404", description = "해당 소식을 찾을 수 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 404, \"code\": \"NEWS_NOT_FOUND\", \"message\": \"해당 소식을 찾을 수 없습니다.\", \"data\": null }")))
    })
    @Operation(summary = "소식 상세보기", description = "소식에 달린 답글과 함께 소식을 조회합니다.")
    public ResponseEntity<ResponseDTO> getNewsDetail(@PathVariable("newsId") Long newsId);

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 소식을 생성한 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_CREATE_NEWS\", \"message\": \"소식을 성공적으로 생성했습니다.\", \"data\": { \"newsId\": null, \"title\": \"신영이가 쓰는 소식\", \"content\": \"우헤헤\", \"writer\": \"강신영\", \"createdAt\": \"2024-12-03T11:54:46.716401011\" } }"))),

            @ApiResponse(responseCode = "401", description = "잘못된 토큰으로 요청할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(name = "INVALID_ACCESS_TOKEN", value = """
                                        {
                                            "status": 401,
                                            "code": "INVALID_ACCESS_TOKEN",
                                            "message": "유효하지 않은 토큰입니다.",
                                            "data": null
                                        }
                                        """),
                                    @ExampleObject(name = "TOKEN_EXPIRED", value = """
                                        {
                                            "status": 401,
                                            "code": "TOKEN_EXPIRED",
                                            "message": "토큰이 만료되었습니다.",
                                            "data": null
                                        }
                                        """),
                                    @ExampleObject(name = "TOKEN_MISSING", value = """
                                        {
                                            "status": 401,
                                            "code": "TOKEN_MISSING",
                                            "message": "요청 헤더에 토큰이 없습니다.",
                                            "data": null
                                        }
                                        """)
                            })),
            @ApiResponse(responseCode = "404", description = "해당 groupId를 찾을 수 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 404, \"code\": \"GROUP_NOT_FOUND\", \"message\": \"해당 groupId를 찾을 수 없습니다.\", \"data\": null }")))
    })
    @Operation(summary = "소식 생성하기", description = "소식을 생성합니다.")
    public ResponseEntity<ResponseDTO<CreateNewsDTO.Res>> createNews(@PathVariable("groupId") Long groupId, @Valid @RequestBody CreateNewsDTO createNewsDTO);


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 소식 댓글을 생성한 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_CREATE_NEWS_COMMENT\", \"message\": \"소식 댓글을 성공적으로 생성했습니다.\", \"data\": { \"commentId\": null, \"content\": \"신영이가 쓰는 댓글\", \"writer\": \"강신영\", \"createdAt\": \"2024-12-03T11:58:27.252507299\" } }"))),

            @ApiResponse(responseCode = "401", description = "잘못된 토큰으로 요청할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(name = "INVALID_ACCESS_TOKEN", value = """
                                        {
                                            "status": 401,
                                            "code": "INVALID_ACCESS_TOKEN",
                                            "message": "유효하지 않은 토큰입니다.",
                                            "data": null
                                        }
                                        """),
                                    @ExampleObject(name = "TOKEN_EXPIRED", value = """
                                        {
                                            "status": 401,
                                            "code": "TOKEN_EXPIRED",
                                            "message": "토큰이 만료되었습니다.",
                                            "data": null
                                        }
                                        """),
                                    @ExampleObject(name = "TOKEN_MISSING", value = """
                                        {
                                            "status": 401,
                                            "code": "TOKEN_MISSING",
                                            "message": "요청 헤더에 토큰이 없습니다.",
                                            "data": null
                                        }
                                        """)
                            })),
            @ApiResponse(responseCode = "404", description = "해당 newsId를 찾을 수 없는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 404, \"code\": \"NEWS_NOT_FOUND\", \"message\": \"해당 소식을 찾을 수 없습니다.\", \"data\": null }")))
    })
    @Operation(summary = "소식에 댓글 남기기", description = "소식에 댓글을 남깁니다.")
    public ResponseEntity<ResponseDTO<CreateCommentDTO.Res>> createNewsComment(@PathVariable("newsId") Long newsId, @Valid @RequestBody CreateCommentDTO createNewsDTO);

    }
