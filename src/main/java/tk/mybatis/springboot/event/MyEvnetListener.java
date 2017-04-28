package tk.mybatis.springboot.event;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Administrator on 2017/4/28.
 */
@Component
public class MyEvnetListener  {

    @EventListener(MyEvent.class)
    @Async
    public void onApplicationEvent(MyEvent event) {
        System.out.println("开始处理事件");
        new RestTemplate().postForEntity("http://www.baidu.com",null,Object.class);
        System.out.println("处理事件"+event.getMessage()+"结束");
        System.out.println(event);
    }
}
