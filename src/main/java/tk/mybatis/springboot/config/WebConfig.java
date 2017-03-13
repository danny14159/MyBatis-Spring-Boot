package tk.mybatis.springboot.config;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import tk.mybatis.springboot.interceptors.SessionInterceptors;
import tk.mybatis.springboot.interceptors.TaskTraceInterceptor;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Created by Administrator on 2016/9/12.
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Bean
    public RestTemplate restTemplate(){
        RestTemplate rest =  new RestTemplate();
        rest.setMessageConverters(Arrays.asList(new StringHttpMessageConverter(Charset.forName("UTF-8"))));
        return rest;
    }

    @Bean
    public TaskTraceInterceptor taskTraceInterceptor(){
        return new TaskTraceInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionInterceptors()).addPathPatterns("/**");
        registry.addInterceptor(new LocaleChangeInterceptor()).addPathPatterns("/**");
        //registry.addInterceptor(taskTraceInterceptor()).addPathPatterns("/**");
    }

    @Bean
    public AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor(){
        AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor = new AutowiredAnnotationBeanPostProcessor();
        autowiredAnnotationBeanPostProcessor.setRequiredParameterValue(false);
        return autowiredAnnotationBeanPostProcessor;
    }
}
