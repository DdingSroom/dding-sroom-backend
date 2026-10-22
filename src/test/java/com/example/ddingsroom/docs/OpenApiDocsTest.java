package com.example.ddingsroom.docs;

import com.example.ddingsroom.config.SwaggerConfig;
import com.example.ddingsroom.support.ControllerSliceTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.configuration.SpringDocSpecPropertiesConfiguration;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.webmvc.core.configuration.MultipleOpenApiSupportConfiguration;
import org.springdoc.webmvc.core.configuration.SpringDocWebMvcConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// DB 없이 springdoc 문서를 생성해 프론트 타입 생성에 필요한 정보가 들어 있는지 검증
@ImportAutoConfiguration({
        SpringDocConfiguration.class,
        SpringDocConfigProperties.class,
        SpringDocSpecPropertiesConfiguration.class,
        SpringDocWebMvcConfiguration.class,
        MultipleOpenApiSupportConfiguration.class
})
@Import(SwaggerConfig.class)
class OpenApiDocsTest extends ControllerSliceTest {

    private static final Path OUTPUT = Path.of("build", "openapi", "openapi.json");

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void generatedDocsAreTypedAndAccurate() throws Exception {
        String json = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        Files.createDirectories(OUTPUT.getParent());
        Files.writeString(OUTPUT, json, StandardCharsets.UTF_8);

        JsonNode root = objectMapper.readTree(json);
        JsonNode paths = root.get("paths");
        JsonNode schemas = root.at("/components/schemas");

        assertThat(List.of("/user/test", "/get-info-test", "/user/join", "/user/delete/{email}", "/user/mypage",
                "/api/community-posts/user/{userId}/category/{category}", "/api/community-posts/user/{userId}/posts",
                "/api/community-posts/comments/me", "/api/community-posts/comments/{commentId}",
                "/api/community-posts/comments/user/{userId}/comments", "/api/community-posts/comments/user/{userId}/replies",
                "/api/community-posts/comments/user/{userId}/post/{postId}"))
                .allSatisfy(removed -> assertThat(paths.has(removed)).as(removed).isFalse());
        assertThat(List.of("/api/community-posts/me", "/api/notification/read", "/login", "/logout"))
                .allSatisfy(kept -> assertThat(paths.has(kept)).as(kept).isTrue());

        assertThat(json).doesNotContain("\"*/*\"");
        assertThat(schemas.has("BaseResponseDTO")).isFalse();
        assertThat(root.at("/servers/0/url").asText()).isEqualTo("/");
        assertThat(schemas.at("/ReservationDTO/properties/startTime/format").asText()).isEqualTo("date-time");
        assertThat(schemas.at("/ReservationRequestDTO/required").toString()).contains("roomId");
        assertThat(schemas.at("/NotificationResponseDTO/nullable").isMissingNode()).isTrue();
        assertThat(schemas.at("/BaseResponseDTONotificationResponseDTO/properties/data/allOf/0/$ref").asText())
                .endsWith("/NotificationResponseDTO");
        assertThat(schemas.at("/BaseResponseDTOVoid/properties/data/enum/0").isNull()).isTrue();

        String commonError = "#/components/schemas/" + SwaggerConfig.COMMON_ERROR;
        assertThat(schemas.at("/" + SwaggerConfig.COMMON_ERROR + "/required").toString())
                .contains("timestamp", "status", "error", "path");
        assertThat(paths.at("/~1api~1reservations~1me/get/responses/403/content/application~1json/schema/$ref").asText())
                .isEqualTo(commonError);
        assertThat(paths.at("/~1api~1suggestions/put/responses/403/content/application~1json/schema/oneOf").toString())
                .contains("SuggestionErrorResponse", commonError);
        assertThat(List.of("post", "put")).allSatisfy(method -> assertThat(
                paths.at("/~1api~1suggestions/" + method + "/responses/400/content/application~1json/schema/oneOf").toString())
                .contains("ValidationErrorResponse"));
        assertThat(List.of("/~1api~1reservations~1me", "/~1api~1reservations~1all-reservation", "/~1api~1reservations~1crowding-level"))
                .allSatisfy(path -> assertThat(paths.at(path + "/get/responses/500").isMissingNode()).as(path).isFalse());

        assertThat(root.at("/info/description").asText()).contains("`/reissue`가 `400`이면", "(405)");
        assertThat(Map.of(
                "/~1user~1modify-password/post/description", "인증 완료 여부를 따로 확인하지 않습니다",
                "/~1api~1reservations/post/description", "예약 처리 중 오류가 발생했습니다",
                "/~1api~1reservations~1cancel/post/description", "예약 취소 중 오류가 발생했습니다",
                "/~1api~1reservations~1crowding-level/get/description", "혼잡도 조회 중 오류가 발생했습니다.",
                "/~1reissue/post/description", "다시 로그인해야 합니다",
                "/~1admin~1users~1{userId}~1status/put/description", "로그인 자체는 막지 않",
                "/~1admin~1users~1{userId}~1role/put/description", "최대 1시간")).allSatisfy((pointer, phrase) ->
                assertThat(paths.at(pointer).asText()).as(pointer).contains(phrase));
        assertThat(schemas.at("/SignUpDTO/properties/email/description").asText()).contains("도메인은 제한하지 않음");

        List<String> operationIds = new ArrayList<>();
        List<String> untyped = new ArrayList<>();
        paths.fields().forEachRemaining(path -> path.getValue().fields().forEachRemaining(method -> {
            JsonNode operation = method.getValue();
            String name = method.getKey().toUpperCase() + " " + path.getKey();
            assertThat(operation.hasNonNull("operationId")).as(name).isTrue();
            assertThat(operation.hasNonNull("summary")).as(name).isTrue();
            operationIds.add(operation.get("operationId").asText());
            operation.get("responses").fields().forEachRemaining(response -> {
                JsonNode content = response.getValue().get("content");
                if (response.getKey().startsWith("2") && content != null) {
                    content.fields().forEachRemaining(media -> {
                        if (!isTyped(media.getValue().get("schema"), schemas)) {
                            untyped.add(name + " " + response.getKey() + " " + media.getKey());
                        }
                    });
                }
            });
        }));
        assertThat(operationIds).doesNotHaveDuplicates().noneMatch(id -> id.matches(".*_\\d+$"));
        assertThat(untyped).as("타입 정보가 없는 성공 응답").isEmpty();

        assertThat(paths.at("/~1api~1reservations~1all-reservation/get/security").isMissingNode()).isTrue();
        assertThat(paths.at("/~1api~1reservations~1me/get/security/0").has(SwaggerConfig.BEARER_AUTH)).isTrue();
        assertThat(paths.at("/~1api~1notification~1list/get/security").size()).isEqualTo(2);
    }

    private boolean isTyped(JsonNode schema, JsonNode schemas) {
        if (schema == null) {
            return false;
        }
        if (schema.has("$ref")) {
            String name = schema.get("$ref").asText().replace("#/components/schemas/", "");
            JsonNode target = schemas.get(name);
            return target != null && (target.has("properties") || target.has("additionalProperties"));
        }
        if (schema.has("oneOf") || schema.has("properties") || schema.has("additionalProperties")) {
            return true;
        }
        String type = schema.path("type").asText();
        if ("array".equals(type)) {
            return isTyped(schema.get("items"), schemas) || schema.path("items").has("type");
        }
        return Map.of("string", 1, "integer", 1, "number", 1, "boolean", 1).containsKey(type);
    }
}
