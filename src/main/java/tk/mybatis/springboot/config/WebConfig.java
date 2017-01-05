package tk.mybatis.springboot.config;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import tk.mybatis.springboot.interceptors.SessionInterceptors;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by Administrator on 2016/9/12.
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Bean
    public RestTemplate restTemplate(){
        RestTemplate rest =  new RestTemplate();
        return rest;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionInterceptors()).addPathPatterns("/**");
        registry.addInterceptor(new LocaleChangeInterceptor()).addPathPatterns("/**");
    }
}
