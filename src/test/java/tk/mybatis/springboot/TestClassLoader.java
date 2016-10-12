package tk.mybatis.springboot;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by Administrator on 2016/10/12.
 */
@Slf4j
public class TestClassLoader {

    public static void main(String[] args) {
        log.info("{}",String.class.getClassLoader() );
    }
}
