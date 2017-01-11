package tk.mybatis.springboot.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import tk.mybatis.springboot.domain.RequestObject;
import tk.mybatis.springboot.domain.TaskTrace;
import tk.mybatis.springboot.mapper.TaskTraceMapper;
import tk.mybatis.springboot.service.TaskTraceService;
import tk.mybatis.springboot.util.ServerUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 只对response body做记录，不影响返回体
 */
@ControllerAdvice
public class TaskTraceResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Autowired
    private TaskTraceService taskTraceService;
    @Autowired
    private ObjectMapper objectMapper;
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        HttpServletRequest httpServletRequest =  ((ServletServerHttpRequest) request).getServletRequest();
        HttpServletResponse httpServletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        Set<Map.Entry<String, List<String>>> headers = response.getHeaders().entrySet();
        String taskId = httpServletRequest.getParameter("taskId");
        if(null == taskId){
            taskId = httpServletRequest.getHeader("taskId");
        }
        if(null == taskId){
            return body;
        }

        String taskTraceId = (String)httpServletRequest.getAttribute("__TASK_TRACE_ID");
        Long startTime = (Long) httpServletRequest.getAttribute("__TASK_TRACE_START_TIME");
        Long millis = null;
        if(null != startTime){
            millis = System.currentTimeMillis() - startTime;
        }
        if(null == taskTraceId){
            return body;
        }

        RequestObject requestObject = ServerUtil.getRequestStringObject(httpServletRequest);
        TaskTrace taskTrace = new TaskTrace();

        taskTrace.setId(taskTraceId);
        taskTrace.setTaskId(taskId);
        taskTrace.setCreateTime(new Date());
        taskTrace.setUpdateTime(new Date());
        taskTrace.setMethod(requestObject.getMethod());
        taskTrace.setRequestBody(requestObject.getParameters());
        taskTrace.setRequestHeaders(requestObject.getHeaders());
        taskTrace.setUri(requestObject.getPath());
        try {
            taskTrace.setResponseBody(objectMapper.writeValueAsString(body));
        }
        catch (Exception e){
            //Ignore
            e.printStackTrace();
        }

        //处理返回头
        String responseHeaders = "";
        for(Map.Entry<String, List<String>> entry:headers){
            for(String value:entry.getValue()){
                responseHeaders += entry.getKey()+"="+value+";";
            }
        }
        taskTrace.setResponseHeaders(responseHeaders);
        taskTrace.setStatusCode(httpServletResponse.getStatus());
        taskTrace.setMillis(millis);

        taskTraceService.insertOrUpdate(taskTrace);
        return body;
    }
}
