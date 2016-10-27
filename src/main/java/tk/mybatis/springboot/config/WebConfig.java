package tk.mybatis.springboot.config;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.springboot.interceptors.SessionInterceptors;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by Administrator on 2016/9/12.
 */
@Configuration
public class WebConfig {

    @Bean
    public RestTemplate restTemplate(){
        RestTemplate rest =  new RestTemplate();
        return rest;
    }

    @Bean
    public SessionInterceptors sessionInterceptors(){
        return new SessionInterceptors();
    }
}
