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
    protected String realname = "姓名";
    protected String email = "email";
    protected String mobile = "mobile";

    @Override
    public boolean equals(Object obj) {
        return Objects.equals(userId,((UserBean)obj).getUserId());
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }

    UserBean(String userId){
        this.userId = userId;
    }
}
