package tk.mybatis.springboot.config;


import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Created by Administrator on 2017/1/5.
 */
public class SimpleValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {

    }
}
