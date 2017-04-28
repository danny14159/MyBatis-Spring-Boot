package tk.mybatis.springboot.event;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

/**
 * Created by Administrator on 2017/4/28.
 */
@Data
public class MyEvent extends ApplicationEvent {
    private String message;
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public MyEvent(Object source,String message) {
        super(source);
        this.message = message;
    }
}
