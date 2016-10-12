package tk.mybatis.springboot.domain;

import lombok.Data;

/**
 * Created by Administrator on 2016/10/12.
 */
@Data
public class Host {

    private String ID;
    private String Name;
    private String Status;
    private String TaskState;
    private String PowerState;
    private String Networks;
}
