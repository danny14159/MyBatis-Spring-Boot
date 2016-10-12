package tk.mybatis.springboot.domain;

import lombok.Data;

/**
 * Created by Administrator on 2016/10/12.
 */
@Data
public class StepHost {

    private String name;
    private String openid;
    private String vmip;
    private String region;
    private String status;
    private String deleted;
}
