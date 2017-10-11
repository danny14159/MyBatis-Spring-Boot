package tk.mybatis.springboot.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * Created by Administrator on 2017/7/14.
 */
@Data
@AllArgsConstructor
public class User {
    private String id;
    private String mobile;
    private String email;
    private String realname;
    private String loginname;

    @Override
    public boolean equals(Object obj) {
        return Objects.equals(id,((User)obj).getId());
    }

    public User(String id) {
        this.id = id;
    }
    public User(){}

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
