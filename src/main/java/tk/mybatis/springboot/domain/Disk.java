package tk.mybatis.springboot.domain;

import lombok.Data;

/**
 * Created by Administrator on 2016/10/12.
 */
@Data
public class Disk {
    private String ID;
    private String Name;
    private String status;
    private String TaskState;
    private String PowerState;
    private String Networks;
}
