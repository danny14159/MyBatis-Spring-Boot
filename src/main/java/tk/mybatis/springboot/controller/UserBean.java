package tk.mybatis.springboot.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Administrator on 2017/7/14.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class
UserBean {
    protected String userId = "用户ID";

    @Override
    public boolean equals(Object obj) {
        return Objects.equals(userId,((UserBean)obj).getUserId());
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }

    public static void main(String[] args) {
        System.out.println("a".hashCode());
        System.out.println("aa".hashCode());
        System.out.println("9798244615d741368bc43a5694d99e72".hashCode());

        Set<UserBean> beans = new HashSet<>();
        beans.add(new UserBean("111"));

        System.out.println(beans.contains(new UserBean("111")));
    }
}
