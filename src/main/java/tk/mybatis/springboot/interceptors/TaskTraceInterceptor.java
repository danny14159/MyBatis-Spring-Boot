package tk.mybatis.springboot.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import tk.mybatis.springboot.config.WrappedResponse;
import tk.mybatis.springboot.domain.TaskTrace;
import tk.mybatis.springboot.mapper.TaskTraceMapper;
import tk.mybatis.springboot.service.TaskTraceService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * 任务跟踪拦截器
 */
@Slf4j
public class TaskTraceInterceptor implements HandlerInterceptor {
    @Autowired
    private TaskTraceMapper taskTraceMapper;
    @Autowired
    private TaskTraceService taskTraceService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //先去取taskId，没有直接放行
        String taskId = request.getParameter("taskId");
        if(null == taskId){
            taskId = request.getHeader("taskId");
        }
        if(null == taskId) {
            return true;
        }
        String taskIdForQuery = taskId;

        //做任务防重
        int taskCount = taskTraceMapper.selectCount(new TaskTrace(){
            {
                setTaskId(taskIdForQuery);
                setStatusCode(200);
            }
        });
        if(0 < taskCount){
            return false;
        }
        request.setAttribute("__TASK_TRACE_START_TIME",System.currentTimeMillis());
        request.setAttribute("__TASK_TRACE_ID", UUID.randomUUID().toString());

        response = new WrappedResponse(response);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("{}",request.getAttribute("startTime"));
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        WrappedResponse wrappedResponse = (WrappedResponse)response;
        log.error("{}",ex);
    }
}
