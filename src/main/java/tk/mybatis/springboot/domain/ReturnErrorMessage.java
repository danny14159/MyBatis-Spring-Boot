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
    private RequestObject requestObject;
    public ReturnErrorMessage(RequestObject requestObject, String message){
        this.message = message;
        this.requestObject = requestObject;
    }
}
