package tk.mybatis.springboot.retvo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Administrator on 2016/9/12.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnMessage {

    private String message;

    private Integer code;

    private Boolean success;
}
