package tk.mybatis.springboot.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import java.util.Date;

/**
 * Created by Administrator on 2017/1/11.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskTrace {
    @Id
    private String id;
    private String uri;
    private String taskId;
    private String requestHeaders;
    private String requestBody;
    private String responseHeaders;
    private String responseBody;
    private String exception;
    private Integer statusCode;
    private Long millis;
    private Date createTime;
    private Date updateTime;
    private String method;
}
