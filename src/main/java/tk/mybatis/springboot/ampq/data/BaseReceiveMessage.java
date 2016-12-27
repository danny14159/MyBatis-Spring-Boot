package tk.mybatis.springboot.ampq.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Created by Administrator on 2016/12/26.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseReceiveMessage extends tk.mybatis.springboot.ampq.BaseReceiveMessage{

    private Msg data ;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Msg{
        private String message;
    }

}
