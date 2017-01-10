package tk.mybatis.springboot.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Administrator on 2017/1/10.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestString {
    private String path;
    private String parameters;
    private String method;
    private String headers;

}
