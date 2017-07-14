package tk.mybatis.springboot.controller;

import lombok.Data;

import java.util.Objects;

/**
 * Created by Administrator on 2017/7/14.
 */
@Data
public class User {
    private String id;
    private String mobile;
    private String email;
    private String realname;

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
