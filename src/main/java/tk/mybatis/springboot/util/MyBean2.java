package tk.mybatis.springboot.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2016/9/6.
 */
@Component
@Order(1)
@Slf4j
public class MyBean2 implements ApplicationRunner{
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("first");
    }
}
