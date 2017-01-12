package tk.mybatis.springboot.config;

import org.apache.catalina.ssi.ByteArrayServletOutputStream;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;

/**
 * Created by Administrator on 2017/1/12.
 */
public class WrappedResponse extends HttpServletResponseWrapper {


    private ByteArrayServletOutputStream byteArrayServletOutputStream;

    private String  body;

    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @param response The response to be wrapped
     * @throws IllegalArgumentException if the response is null
     */
    public WrappedResponse(HttpServletResponse response) throws IOException {
        super(response);
        byteArrayServletOutputStream = new ByteArrayServletOutputStream();
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(new OutputStreamWriter(byteArrayServletOutputStream, "UTF-8"));
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if(!StringUtils.isEmpty(body)){
            byteArrayServletOutputStream = new ByteArrayServletOutputStream();
            byteArrayServletOutputStream.flush();
            getWriter().write(body);
        }
        else {
            body = new String (byteArrayServletOutputStream.toByteArray(),"UTF-8");
        }
        return byteArrayServletOutputStream;
    }

    @Override
    public String toString() {

        try {
            return body;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    private class ByteArrayServletOutputStreamEx extends ByteArrayServletOutputStream{

        @Override
        public String toString() {
            return super.toString();
        }
    }

}
