package tk.mybatis.springboot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by Administrator on 2017/1/9.
 */
@ControllerAdvice
@Slf4j
public class MyResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return (returnType.getGenericParameterType()) != Callable.class;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        log.info("beforeBodyWrite Thread ID:{}", Thread.currentThread().getId());

        Map map = new HashMap();
        map.put("body", body);
        map.put("millis", System.currentTimeMillis() - (long) ((ServletServerHttpRequest) request).getServletRequest().getAttribute("startTime"));

        if (StringHttpMessageConverter.class == selectedConverterType) {
            try {
                return new ObjectMapper().writeValueAsString(map);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return map;
    }
}
