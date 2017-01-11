package tk.mybatis.springboot.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import tk.mybatis.springboot.domain.RequestObject;
import tk.mybatis.springboot.domain.ReturnErrorMessage;
import tk.mybatis.springboot.domain.TaskTrace;
import tk.mybatis.springboot.retvo.ReturnMessage;
import tk.mybatis.springboot.service.TaskTraceService;
import tk.mybatis.springboot.util.ServerUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 全局异常处理
 */
@ControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TaskTraceService taskTraceService;

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object exception(HttpServletRequest httpServletRequest, HttpServletResponse response, Exception ex) {
        //log.error("Error:",ex.getMessage());
        //将错误日志堆栈记录入库
        ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        ex.printStackTrace(new java.io.PrintWriter(buf, true));
        String  expMessage = buf.toString();
        String finalExMessage = "";
        try {
            buf.close();
        } catch (IOException e) {
            //Ignore
            e.printStackTrace();
        }
        String[] split = expMessage.split("\r\n\t");
        if(split.length >= 2){
            finalExMessage += split[0]+"\r\n\t"+split[1];
        }

        //----
        String taskTraceId = (String) httpServletRequest.getAttribute("__TASK_TRACE_ID");
        if(null != taskTraceId){
            TaskTrace taskTrace = new TaskTrace();
            taskTrace.setId(taskTraceId);
            taskTrace.setException(finalExMessage);
            taskTraceService.insertOrUpdate(taskTrace);
        }
        //---

        RequestObject requestObject = ServerUtil.getRequestStringObject(httpServletRequest);
        String message = "";
        if(ex instanceof BindException){
            BindException bindException = (BindException)ex;
            List<ObjectError> allErrors = bindException.getAllErrors();
            for(ObjectError objectError:allErrors){
                message += ((FieldError)objectError).getField()+objectError.getDefaultMessage()+";";
            }
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return new ReturnErrorMessage(requestObject,message);
        }
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ReturnErrorMessage(requestObject, message + "应用程序错误日志：\r\n"+finalExMessage);
    }


}
