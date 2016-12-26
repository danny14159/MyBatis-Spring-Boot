package tk.mybatis.springboot.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.springboot.Application;
import tk.mybatis.springboot.domain.City;
import tk.mybatis.springboot.mapper.CityMapper;
import tk.mybatis.springboot.mapper.TestMapper;

import java.io.Serializable;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2016/11/3.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@Slf4j
public class TestServiceTest {

    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    private CityMapper cityMapper;

    @Autowired
    private TestMapper testMapper;
    @Test
    public void testQueue() throws Exception {
        cityMapper.updateFlagByPrimayKey(1);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class Obj implements Serializable{
        private static final long serialVersionUID = 11L;
        String name;
    }
}