package com.example.ddingsroom.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
@PropertySource(value = "classpath:springdoc.properties", encoding = "UTF-8")
public class SwaggerConfig {

    public static final String BEARER_AUTH = "bearerAuth";
    public static final String COMMON_ERROR = "CommonErrorResponse";

    public static final String TAG_AUTH = "인증";
    public static final String TAG_USER = "회원";
    public static final String TAG_RESERVATION = "예약";
    public static final String TAG_COMMUNITY_POST = "커뮤니티 게시글";
    public static final String TAG_COMMUNITY_COMMENT = "커뮤니티 댓글";
    public static final String TAG_SUGGESTION = "건의";
    public static final String TAG_SUGGESTION_ANSWER = "건의 답변";
    public static final String TAG_SUGGESTION_IMAGE = "건의 이미지";
    public static final String TAG_NOTIFICATION = "공지사항";
    public static final String TAG_ADMIN_USER = "관리자 - 사용자";
    public static final String TAG_ADMIN_RESERVATION = "관리자 - 예약";
    public static final String TAG_ADMIN_ROOM = "관리자 - 스터디룸";
    public static final String TAG_ADMIN_SYSTEM = "관리자 - 시스템";

    // 토큰 없이도 호출되지만 토큰이 있으면 isRead가 채워지는 API
    private static final Set<String> OPTIONAL_AUTH_OPERATIONS = Set.of("getNotifications", "getNotification");
    // URL은 공개라 403이 없고, 서비스에서 관리자 여부를 확인해 200 + error로 응답하는 API
    private static final Set<String> SERVICE_CHECKED_OPERATIONS =
            Set.of("createNotification", "updateNotification", "deleteNotification");

    private static final String DESCRIPTION = """
            띵스룸 스터디룸 예약 서비스 API

            ### 인증
            1. `POST /login` (form 데이터 `email`, `password`) 성공 시 응답 헤더 `access`에 액세스 토큰(1시간), `refresh` HttpOnly 쿠키(24시간)가 내려옵니다.
            2. 이후 요청 헤더에 `Authorization: Bearer {access}`를 넣습니다. (우측 Authorize 버튼)
            3. 토큰을 보냈는데 `401` + 본문 `access token expired`이면 `POST /reissue`로 재발급 후 다시 요청합니다.
            4. `/reissue`가 `400`이면(refresh 만료·로그아웃, 관리자의 권한 변경·차단·회원 삭제) 다시 로그인합니다.
            - 자물쇠가 표시된 API를 토큰 없이 호출하면 `403`, `/admin/**`은 ROLE_ADMIN 토큰이 필요합니다.
            - 다음 경우는 API별 응답 형식과 별개로 공통 오류 본문 `{ timestamp, status, error, path }`로 응답합니다:
              토큰 없음·권한 부족(403), 없는 경로(404), 허용되지 않는 HTTP 메서드(405),
              JSON·파라미터 형식 오류나 필수 파라미터 누락(400, 예약 API는 `{ message }`로 응답), 처리되지 않은 서버 오류(500)

            ### 날짜 형식 (요청·응답 공통, KST, 오프셋 없음)
            - 일시: `yyyy-MM-dd'T'HH:mm:ss` 예) `2026-10-21T21:30:00`
            - 날짜: `yyyy-MM-dd` 예) `2026-10-21`

            ### 응답 형식 (도메인별)
            - 커뮤니티·공지사항: `{ message, error, data }` — 실패해도 HTTP 200이고 `error`에 사유가 담깁니다. (`POST /api/notification/read`만 상태코드 사용)
            - 예약·회원: `{ message }` — 성공·실패 모두 `message`로 전달되며 상태코드는 API별 설명 참고
            - 건의: 성공 `{ message, ... }`, 실패 `{ error }` + 상태코드 (입력값 검증 실패는 `{ 필드명: 메시지 }`)
            - 관리자: `{ message, success, data }` 또는 API별 목록 객체
            """;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("띵스룸(DdingsRoom) API").version("v1").description(DESCRIPTION))
                .servers(List.of(new Server().url("/").description("현재 접속한 서버")))
                .components(new Components().addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("로그인 응답 헤더 `access`의 토큰 값")))
                .tags(List.of(
                        new Tag().name(TAG_AUTH).description("로그인·로그아웃·토큰 재발급·회원가입·이메일 인증·비밀번호 재설정"),
                        new Tag().name(TAG_USER).description("로그인한 사용자 본인 계정 관리"),
                        new Tag().name(TAG_RESERVATION).description("스터디룸 예약·취소·조회"),
                        new Tag().name(TAG_COMMUNITY_POST).description("커뮤니티 게시글 (로그인 필요)"),
                        new Tag().name(TAG_COMMUNITY_COMMENT).description("커뮤니티 댓글·대댓글 (로그인 필요)"),
                        new Tag().name(TAG_SUGGESTION).description("건의하기 (로그인 필요)"),
                        new Tag().name(TAG_SUGGESTION_ANSWER).description("건의에 대한 관리자 답변 (작성·수정·삭제는 관리자)"),
                        new Tag().name(TAG_SUGGESTION_IMAGE).description("건의 첨부 이미지 (로그인 필요)"),
                        new Tag().name(TAG_NOTIFICATION).description("공지사항 조회·읽음 처리 (작성·수정·삭제는 관리자)"),
                        new Tag().name(TAG_ADMIN_USER).description("ROLE_ADMIN 전용"),
                        new Tag().name(TAG_ADMIN_RESERVATION).description("ROLE_ADMIN 전용"),
                        new Tag().name(TAG_ADMIN_ROOM).description("ROLE_ADMIN 전용"),
                        new Tag().name(TAG_ADMIN_SYSTEM).description("ROLE_ADMIN 전용")));
    }

    // 필터에서 처리해 컨트롤러가 없는 /login, /logout 을 문서에 추가
    @Bean
    public OpenApiCustomizer authEndpointsCustomizer() {
        return openApi -> {
            Paths paths = new Paths();
            paths.addPathItem("/login", new PathItem().post(loginOperation()));
            paths.addPathItem("/logout", new PathItem().post(logoutOperation()));
            if (openApi.getPaths() != null) {
                paths.putAll(openApi.getPaths());
            }
            openApi.setPaths(paths);
        };
    }

    // 보호 API에 공통 401/403 응답을 붙이고 응답코드를 정렬 (공통 오류 스키마는 미참조 스키마 정리 이후라 여기서 등록)
    @Bean
    public OpenApiCustomizer securityResponsesCustomizer() {
        return openApi -> {
            openApi.getComponents().addSchemas(COMMON_ERROR, commonErrorSchema());
            openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
                String operationId = operation.getOperationId();
                if (OPTIONAL_AUTH_OPERATIONS.contains(operationId)) {
                    operation.setSecurity(List.of(new SecurityRequirement(), new SecurityRequirement().addList(BEARER_AUTH)));
                }
                ApiResponses responses = operation.getResponses();
                if (operation.getSecurity() != null && !operation.getSecurity().isEmpty()) {
                    responses.putIfAbsent("401", unauthorizedResponse());
                    if (!OPTIONAL_AUTH_OPERATIONS.contains(operationId) && !SERVICE_CHECKED_OPERATIONS.contains(operationId)) {
                        addForbiddenResponse(responses);
                    }
                }
                ApiResponses sorted = new ApiResponses();
                responses.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .forEach(entry -> sorted.addApiResponse(entry.getKey(), entry.getValue()));
                operation.setResponses(sorted);
            }));
        };
    }

    // swagger-core가 data(제네릭) 필드의 nullable을 참조 스키마 전체에 붙이는 문제 보정, 속성 없는 object data(Void 등)는 null 타입으로 표시
    @Bean
    @SuppressWarnings({"rawtypes", "unchecked"})
    public OpenApiCustomizer nullableDataCustomizer() {
        return openApi -> {
            Map<String, Schema> schemas = openApi.getComponents().getSchemas();
            if (schemas == null) {
                return;
            }
            schemas.values().stream()
                    .filter(schema -> schema.getProperties() != null)
                    .forEach(schema -> schema.setNullable(null));
            schemas.forEach((name, schema) -> {
                Schema data = schema.getProperties() == null ? null : (Schema) schema.getProperties().get("data");
                if (data == null) {
                    return;
                }
                if (name.startsWith("BaseResponseDTO") && data.get$ref() != null) {
                    schema.getProperties().put("data", new Schema<>().nullable(true)
                            .description(data.getDescription())
                            .allOf(List.of(new Schema<>().$ref(data.get$ref()))));
                } else if ("object".equals(data.getType()) && data.getProperties() == null
                        && data.getAdditionalProperties() == null) {
                    Schema alwaysNull = new Schema<>().nullable(true).description(data.getDescription());
                    alwaysNull.setEnum(Collections.singletonList(null));
                    schema.getProperties().put("data", alwaysNull);
                }
            });
        };
    }

    private static Operation loginOperation() {
        Schema<?> credentials = new ObjectSchema()
                .addProperty("email", new StringSchema().format("email").example("student@mju.ac.kr").description("가입한 이메일"))
                .addProperty("password", new StringSchema().format("password").example("password123").description("비밀번호"))
                .required(List.of("email", "password"));
        return new Operation()
                .addTagsItem(TAG_AUTH)
                .operationId("login")
                .summary("로그인")
                .description("JSON이 아닌 form 데이터로 `email`, `password`를 보냅니다. 응답 본문은 없고 "
                        + "헤더 `access`(액세스 토큰, 1시간)와 `refresh` HttpOnly 쿠키(24시간)가 내려옵니다.")
                .requestBody(new RequestBody().required(true).content(new Content()
                        .addMediaType("application/x-www-form-urlencoded", new MediaType().schema(credentials))
                        .addMediaType("multipart/form-data", new MediaType().schema(credentials))))
                .responses(new ApiResponses()
                        .addApiResponse("200", new ApiResponse().description("로그인 성공 (본문 없음)")
                                .addHeaderObject("access", new Header()
                                        .description("액세스 토큰(JWT). 이후 `Authorization: Bearer {access}`로 전송")
                                        .schema(new StringSchema()))
                                .addHeaderObject("Set-Cookie", new Header()
                                        .description("`refresh={리프레시 토큰}; Max-Age=86400; HttpOnly`")
                                        .schema(new StringSchema())))
                        .addApiResponse("401", new ApiResponse().description("이메일 또는 비밀번호 불일치 (본문 없음)")));
    }

    private static Operation logoutOperation() {
        return new Operation()
                .addTagsItem(TAG_AUTH)
                .operationId("logout")
                .summary("로그아웃")
                .description("`refresh` 쿠키의 리프레시 토큰을 폐기하고 쿠키를 만료시킵니다. 응답 본문은 없습니다.")
                .addParametersItem(new Parameter().in("cookie").name("refresh").required(true)
                        .description("로그인·재발급 때 받은 리프레시 토큰").schema(new StringSchema()))
                .responses(new ApiResponses()
                        .addApiResponse("200", new ApiResponse().description("로그아웃 성공 (refresh 쿠키 만료)"))
                        .addApiResponse("400", new ApiResponse().description("refresh 쿠키가 없거나 만료·변조·이미 폐기된 토큰")));
    }

    // 토큰 없음·권한 부족은 공통 오류 본문. API가 직접 403을 내는 경우엔 두 형식 중 하나
    private static void addForbiddenResponse(ApiResponses responses) {
        Schema<?> commonError = new Schema<>().$ref(COMMON_ERROR);
        ApiResponse existing = responses.get("403");
        if (existing == null) {
            responses.addApiResponse("403", new ApiResponse()
                    .description("토큰이 없거나 권한 부족 (관리자 API는 ROLE_ADMIN 필요)")
                    .content(new Content().addMediaType("application/json", new MediaType().schema(commonError))));
            return;
        }
        MediaType json = existing.getContent() == null ? null : existing.getContent().get("application/json");
        if (json != null && json.getSchema() != null) {
            json.setSchema(new ComposedSchema().oneOf(List.of(json.getSchema(), commonError)));
            existing.setDescription(existing.getDescription() + " / 토큰 없음은 공통 오류 본문");
        }
    }

    private static Schema<?> commonErrorSchema() {
        return new ObjectSchema()
                .description("인증·권한 실패, 없는 경로, 처리되지 않은 오류에 쓰이는 Spring 기본 오류 본문")
                .addProperty("timestamp", new IntegerSchema().format("int64").example(1792647801123L)
                        .description("오류 발생 시각 (epoch milliseconds)"))
                .addProperty("status", new IntegerSchema().example(403).description("HTTP 상태 코드"))
                .addProperty("error", new StringSchema().example("Forbidden").description("상태 코드 이름"))
                .addProperty("path", new StringSchema().example("/api/reservations/me").description("요청 경로"))
                .required(List.of("timestamp", "status", "error", "path"));
    }

    private static ApiResponse unauthorizedResponse() {
        return new ApiResponse()
                .description("액세스 토큰 만료 또는 변조. `/reissue`로 재발급 후 재시도")
                .content(new Content().addMediaType("text/plain", new MediaType()
                        .schema(new StringSchema()._enum(List.of("access token expired", "invalid access token")))));
    }
}
