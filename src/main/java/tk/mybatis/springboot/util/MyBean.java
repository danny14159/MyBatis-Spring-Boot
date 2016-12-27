package tk.mybatis.springboot.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by Administrator on 2016/9/6.
 */
@Component
@Order
@Slf4j
public class MyBean implements ApplicationRunner{

    @Value("${name:default name}")
    private String name;
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        log.info("second"+name);
        log.error(new Exception("系统错误").getLocalizedMessage());

    }

    @PostConstruct
    public void open(){
        log.info("open:"+name);
    }
}
