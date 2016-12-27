package tk.mybatis.springboot.service;

import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.springboot.ampq.data.BaseReceiveMessage;
import tk.mybatis.springboot.domain.Image;
import tk.mybatis.springboot.mapper.TestMapper;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/11/3.
 */
@Service
public class TestService {

    @Autowired
    private TestMapper testMapper;


    public List<Image> list() {
        return testMapper.selectImage();
    }

    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(value = "host",durable = "true"),
            value = @Queue(value = "host", durable = "true"), key = "create"))
    public void testReceiveMessage(BaseReceiveMessage baseReceiveMessage) {

        System.out.println(baseReceiveMessage.getData().getMessage());
    }
}