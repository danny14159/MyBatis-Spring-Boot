package tk.mybatis.springboot.ampq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/12/23.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageEntity implements Serializable {
    private static final long serialVersionUID = 11L;
    private String name;
}
