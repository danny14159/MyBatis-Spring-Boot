package tk.mybatis.springboot.ampq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Created by Administrator on 2016/12/26.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseReceiveMessage {
    private String message;
    private Integer code;
    private Map data;
}
