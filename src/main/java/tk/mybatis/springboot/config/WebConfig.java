package tk.mybatis.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tk.mybatis.springboot.interceptors.SessionInterceptors;

/**
 * Created by Administrator on 2016/9/12.
 */
@Configuration
public class WebConfig {

    @Bean
    public SessionInterceptors sessionInterceptors(){
        return new SessionInterceptors();
    }
}
