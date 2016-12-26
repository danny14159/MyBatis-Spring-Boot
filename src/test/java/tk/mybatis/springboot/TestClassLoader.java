package tk.mybatis.springboot;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by Administrator on 2016/10/12.
 */
@Slf4j
public class TestClassLoader {

    static A a;

    public static void setA(A a){
        TestClassLoader.a = a;
    }

    public static void main(String[] args) {
        A a = a1-> a1.length * 12;
        setA(a);
        System.out.println(a.fun(new int[]{5,7,1}));
    }

    @FunctionalInterface
    public interface A{
        int fun(int[] a);
    }
}
