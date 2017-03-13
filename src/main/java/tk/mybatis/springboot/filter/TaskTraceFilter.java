package tk.mybatis.springboot.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.util.StreamUtils;
import tk.mybatis.springboot.config.WrappedResponse;
import tk.mybatis.springboot.domain.RequestObject;
import tk.mybatis.springboot.domain.TaskTrace;
import tk.mybatis.springboot.mapper.TaskTraceMapper;
import tk.mybatis.springboot.service.TaskTraceService;
import tk.mybatis.springboot.util.ServerUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by Administrator on 2017/1/12.
 */
@WebFilter(value = "/*")

@Configuration
@Slf4j
public class TaskTraceFilter implements Filter{

    @Autowired
    private TaskTraceService taskTraceService;
    @Autowired(required = false)
    private TaskTraceMapper taskTraceMapper;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse)response;
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        HttpServletRequest requestWrapper = new BodyReaderHttpServletRequestWrapper((HttpServletRequest) request);

        String taskId = httpServletRequest.getParameter("taskId");
        if(null == taskId){
            taskId = httpServletRequest.getHeader("taskId");
        }
        if(null == taskId){
            //放行
            chain.doFilter(requestWrapper,response);
            return;
        }
        long startTime = System.currentTimeMillis();
        String taskTraceId = UUID.randomUUID().toString();
        httpServletRequest.setAttribute("__TASK_TRACE_ID",taskTraceId);

        String taskIdForQuery = taskId;
        //做任务防重
        int taskCount = taskTraceMapper.selectCount(new TaskTrace(){
            {
                setTaskId(taskIdForQuery);
                setStatusCode(200);
            }
        });
        if(0 < taskCount){
            //有成功的记录了，不放行
            return;
        }

        HttpServletResponseCopier responseCopier = new HttpServletResponseCopier((HttpServletResponse) response);
        try {
            chain.doFilter(requestWrapper, responseCopier);
            responseCopier.flushBuffer();
        }
        finally {
            String body = new String(responseCopier.getCopy(), response.getCharacterEncoding());
            RequestObject requestObject = ServerUtil.getRequestStringObject(requestWrapper);
            TaskTrace taskTrace = new TaskTrace();

            taskTrace.setId(taskTraceId);
            taskTrace.setTaskId(taskId);
            taskTrace.setCreateTime(new Date());
            taskTrace.setUpdateTime(new Date());
            taskTrace.setMethod(requestObject.getMethod());
            taskTrace.setRequestBody(requestObject.getParameters());
            taskTrace.setRequestHeaders(requestObject.getHeaders());
            taskTrace.setUri(requestObject.getPath());
            taskTrace.setMillis(System.currentTimeMillis() - startTime);
            taskTrace.setResponseBody(body);
            taskTrace.setStatusCode(httpServletResponse.getStatus());

            //取response_headers
            String responseHeaders = "";
            Collection<String> headerNames = httpServletResponse.getHeaderNames();
            for(String headerName:headerNames){
                for(String headerValue:httpServletResponse.getHeaders(headerName)){
                    responseHeaders += headerName+"="+headerValue+";";
                }
            }

            taskTrace.setResponseHeaders(responseHeaders);

            taskTraceService.insertOrUpdate(taskTrace);
        }
    }

    @Override
    public void destroy() {

    }
}
