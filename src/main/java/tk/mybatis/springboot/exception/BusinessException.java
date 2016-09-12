package tk.mybatis.springboot.exception;

/**
 * Created by Administrator on 2016/9/12.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
