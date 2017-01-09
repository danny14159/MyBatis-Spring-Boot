package tk.mybatis.springboot;

import java.lang.annotation.*;

/**
 * Created by Administrator on 2017/1/9.
 */
@Target(value = ElementType.PARAMETER)
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface UserId {
}
