package tk.mybatis.springboot.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternalPay {

    private String id;
    private String project_id;
    private String project_name;
    private String client_name;
    private String department;
    private String console_user_id;

    private String order_id;

    private Date create_time;
    private Date update_time;
    private String user_id;

    private String order_detail;

    private Object amount;
}
