package com.example.ddingsroom.contract;

import com.example.ddingsroom.support.ControllerSliceTest;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import jakarta.validation.Valid;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

// 모든 API의 요청 계약(경로·메서드·파라미터·바디 필드)이 스냅샷과 같은지 검증
class RequestMappingSnapshotTest extends ControllerSliceTest {

    private static final Path ACTUAL = Path.of("build", "contract", "request-mappings.txt");

    @Autowired
    @Qualifier("requestMappingHandlerMapping")
    private RequestMappingHandlerMapping handlerMapping;

    private final ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();

    @Test
    void requestContractMatchesSnapshot() throws Exception {
        List<String> lines = new ArrayList<>();
        handlerMapping.getHandlerMethods().forEach((info, method) -> {
            if (method.getBeanType().getPackageName().startsWith("com.example.ddingsroom")) {
                lines.add(describe(info, method));
            }
        });
        lines.sort(String::compareTo);
        String actual = String.join("\n", lines) + "\n";

        Files.createDirectories(ACTUAL.getParent());
        Files.writeString(ACTUAL, actual, StandardCharsets.UTF_8);

        ClassPathResource expected = new ClassPathResource("contract/request-mappings.txt");
        assertThat(expected.exists()).as("스냅샷 없음. 생성된 파일: %s", ACTUAL.toAbsolutePath()).isTrue();
        assertThat(actual).isEqualTo(expected.getContentAsString(StandardCharsets.UTF_8));
    }

    private String describe(RequestMappingInfo info, HandlerMethod method) {
        String httpMethods = info.getMethodsCondition().getMethods().stream()
                .map(Enum::name).sorted().collect(Collectors.joining(","));
        StringBuilder line = new StringBuilder()
                .append(httpMethods.isEmpty() ? "ANY" : httpMethods)
                .append(' ')
                .append(String.join(",", info.getPatternValues()));
        if (!info.getConsumesCondition().isEmpty()) {
            line.append(" consumes=").append(info.getConsumesCondition().getConsumableMediaTypes());
        }
        if (!info.getProducesCondition().isEmpty()) {
            line.append(" produces=").append(info.getProducesCondition().getProducibleMediaTypes());
        }
        String params = Arrays.stream(method.getMethodParameters())
                .map(this::describeParameter)
                .collect(Collectors.joining(", "));
        return line.append(" | ").append(params).toString();
    }

    private String describeParameter(MethodParameter parameter) {
        parameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
        String type = typeName(parameter.getGenericParameterType());
        String valid = parameter.hasParameterAnnotation(Valid.class) ? "@Valid " : "";

        RequestBody body = parameter.getParameterAnnotation(RequestBody.class);
        if (body != null) {
            return "body " + valid + type + jsonProperties(parameter.getParameterType());
        }
        RequestPart part = parameter.getParameterAnnotation(RequestPart.class);
        if (part != null) {
            String props = MultipartFile.class.isAssignableFrom(parameter.getParameterType())
                    ? "" : jsonProperties(parameter.getParameterType());
            return "part " + valid + name(part.name(), part.value(), parameter) + ":" + type + props
                    + (part.required() ? "" : "?");
        }
        PathVariable path = parameter.getParameterAnnotation(PathVariable.class);
        if (path != null) {
            return "path " + name(path.name(), path.value(), parameter) + ":" + type;
        }
        RequestParam query = parameter.getParameterAnnotation(RequestParam.class);
        if (query != null) {
            boolean optional = !query.required() || parameter.getParameterType() == Optional.class;
            return "query " + name(query.name(), query.value(), parameter) + ":" + type + (optional ? "?" : "");
        }
        CookieValue cookie = parameter.getParameterAnnotation(CookieValue.class);
        if (cookie != null) {
            return "cookie " + name(cookie.name(), cookie.value(), parameter) + ":" + type;
        }
        return "arg " + type;
    }

    private String name(String name, String value, MethodParameter parameter) {
        if (!name.isEmpty()) {
            return name;
        }
        return value.isEmpty() ? parameter.getParameterName() : value;
    }

    private String jsonProperties(Class<?> type) {
        BeanDescription description = objectMapper.getDeserializationConfig()
                .introspect(objectMapper.constructType(type));
        return description.findProperties().stream()
                .filter(BeanPropertyDefinition::couldDeserialize)
                .map(p -> p.getName() + ":" + p.getRawPrimaryType().getSimpleName())
                .sorted()
                .collect(Collectors.joining(", ", "{", "}"));
    }

    private String typeName(Type type) {
        if (type instanceof ParameterizedType parameterized) {
            String args = Arrays.stream(parameterized.getActualTypeArguments())
                    .map(this::typeName)
                    .collect(Collectors.joining(","));
            return typeName(parameterized.getRawType()) + "<" + args + ">";
        }
        if (type instanceof Class<?> clazz) {
            return clazz.getSimpleName();
        }
        return type.getTypeName();
    }
}
