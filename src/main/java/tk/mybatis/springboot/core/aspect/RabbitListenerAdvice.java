package tk.mybatis.springboot.core.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.springboot.ampq.data.BaseReceiveMessage;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2016/12/27.
 */
@Aspect
@Service
@Slf4j
public class RabbitListenerAdvice {

    @Before("execution(*  tk.mybatis.springboot.service.*Service.*(..))")
    public void before(JoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        if(!method.isAnnotationPresent(RabbitListener.class)){
            return;
        }

        Object[] args = joinPoint.getArgs();
        for(Object arg:args){
            if(arg instanceof BaseReceiveMessage){
                System.out.println("通用操作"+((BaseReceiveMessage)(arg)).getData().getMessage());
                ((BaseReceiveMessage) arg).setMessage("new message");
            }
        }
    }
}
