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
    @ExcelUtil.ExcelField(order = 100, name = "用户ID")
    private String userId ;

    @Override
    public boolean equals(Object obj) {
        return Objects.equals(userId,((UserBean)obj).getUserId());
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }

    public void setRealname(String realname){}
    public void setEmail(String realname){}
    public void setMobile(String mobile){}
    public void setRegionName(String regionName){}
}
