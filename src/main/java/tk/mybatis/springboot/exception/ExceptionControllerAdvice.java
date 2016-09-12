package tk.mybatis.springboot.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import tk.mybatis.springboot.retvo.ReturnMessage;
import tk.mybatis.springboot.util.ServerUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

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
        ex.printStackTrace();
        log.info(ServerUtil.getRequestString(httpServletRequest));
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ReturnMessage(ex.getMessage(),response.getStatus(),false);
    }

}
