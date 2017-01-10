package tk.mybatis.springboot.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tk.mybatis.springboot.retvo.ReturnMessage;

/**
 * Created by Administrator on 2017/1/10.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnErrorMessage extends ReturnMessage {
    private RequestString requestString;
    public ReturnErrorMessage(RequestString requestString,String message,Integer code,Boolean success){
        this.message = message;
        this.code = code;
        this.success = success;
        this.requestString = requestString;
    }
}
