package com.carely.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {

        Server testServer = new Server();
        testServer.setUrl("http://localhost:8080/");

        String jwt = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
        Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .description("토큰값을 입력하여 인증을 활성화할 수 있습니다.")
                .bearerFormat("JWT")
        );

        OpenAPI openAPI = new OpenAPI()
                .components(components)
                .info(new Info()
                        .title("2024 단풍톤 carely API")
                        .description("2024 단풍톤 백엔드 carely API 명세서")
                        .version("1.0.0"))
                .addSecurityItem(securityRequirement)
                .servers(List.of(testServer));

        addLogoutPath(openAPI);
        return openAPI;
    }

    private void addLogoutPath(OpenAPI openAPI) {
        Parameter refreshParameter = new Parameter()
                .name("refresh")
                .in("header")
                .required(true)
                .schema(new Schema<String>().type("string"))
                .description("리프레시 토큰");

        ApiResponse successResponse = new ApiResponse()
                .description("성공적으로 로그아웃된 경우")
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .schema(new Schema<>()
                                        .addProperty("status", new Schema<Integer>().type("integer"))
                                        .addProperty("code", new Schema<String>().type("string"))
                                        .addProperty("message", new Schema<String>().type("string"))
                                        .addProperty("data", new Schema<String>().type("string").nullable(true)))
                                .example("{ \"status\": 200, \"code\": \"SUCCESS_LOGOUT\", \"message\": \"성공적으로 로그아웃했습니다.\", \"data\": null }")));

        ApiResponse invalidRequestResponse = new ApiResponse()
                .description("인증 정보가 유효하지 않을 경우")
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .schema(new Schema<>()
                                        .addProperty("status", new Schema<Integer>().type("integer"))
                                        .addProperty("code", new Schema<String>().type("string"))
                                        .addProperty("message", new Schema<String>().type("string"))
                                        .addProperty("data", new Schema<String>().type("string").nullable(true)))
                                .addExamples("TOKEN_EXPIRED", new Example()
                                        .value("{ \"status\": 401, \"code\": \"TOKEN_EXPIRED\", \"message\": \"토큰이 만료되었습니다.\", \"data\": null }"))
                                .addExamples("TOKEN_MISSING", new Example()
                                        .value("{ \"status\": 401, \"code\": \"TOKEN_MISSING\", \"message\": \"요청 헤더에 토큰이 없습니다.\", \"data\": null }"))
                                .addExamples("INVALID_REFRESH_TOKEN", new Example()
                                        .value("{ \"status\": 401, \"code\": \"INVALID_REFRESH_TOKEN\", \"message\": \"유효하지 않은 리프레시 토큰입니다.\", \"data\": null }"))));

        ApiResponses apiResponses = new ApiResponses();
        apiResponses.addApiResponse("200", successResponse);
        apiResponses.addApiResponse("401", invalidRequestResponse);

        PathItem pathItem = new PathItem()
                .post(new io.swagger.v3.oas.models.Operation()
                        .addTagsItem("user-controller")
                        .summary("로그아웃하기")
                        .description("refresh 토큰을 헤더에 추가하여 로그아웃을 진행합니다.")
                        .addParametersItem(refreshParameter)
                        .responses(apiResponses));

        openAPI.path("/logout", pathItem); // 경로 추가
    }

}