package tk.mybatis.springboot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tk.mybatis.springboot.domain.RequestString;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Created by Administrator on 2016/9/12.
 */
@Slf4j
public class ServerUtil {

    static ObjectMapper objectMapper = new ObjectMapper();

    private ServerUtil() {}

    public static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static HttpServletResponse getHttpServletResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

    }

    public static String getRequestString(HttpServletRequest request) {
        if(null == request){
            request = getHttpServletRequest();
        }
        try{
            String headerStr = "";
            Enumeration<String> headerNames = request.getHeaderNames();
            while(headerNames.hasMoreElements()){
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                headerStr += headerName + ":" + headerValue + ";";
            }
            return "Path:"+request.getRequestURI()+",Parameters:"+objectMapper.writeValueAsString(request.getParameterMap()+",Headers:" + headerStr);
        }
        catch (IOException e){
            log.error(e.getMessage());
        }
        return "";
    }

    public static RequestString getRequestStringObject(HttpServletRequest request){
        if(null == request){
            request = getHttpServletRequest();
        }
        String headerStr = "";
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headerStr += headerName + ":" + headerValue + ";";
        }
        String param = null;
        try {
            param = objectMapper.writeValueAsString(request.getParameterMap());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return new RequestString(request.getRequestURI(),param,request.getMethod(),headerStr);
    }
    public static String getRequestString() {
        return getRequestString(null);
    }
}
