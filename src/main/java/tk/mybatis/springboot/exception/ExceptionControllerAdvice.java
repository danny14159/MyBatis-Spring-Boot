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
import tk.mybatis.springboot.retvo.ReturnMessage;
import tk.mybatis.springboot.util.ServerUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 全局异常处理
 */
@ControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {

    @Autowired
    private ObjectMapper objectMapper;

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object exception(HttpServletRequest httpServletRequest, HttpServletResponse response, Exception ex) {
        log.error("Error:",ex.getMessage());
        String message = ex.getMessage();
        if(ex instanceof BindException){
            message = "";
            BindException bindException = (BindException)ex;
            List<ObjectError> allErrors = bindException.getAllErrors();
            for(ObjectError objectError:allErrors){
                message += ((FieldError)objectError).getField()+objectError.getDefaultMessage()+";";
            }
        }
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ReturnMessage(message,response.getStatus(),false);
    }

}
