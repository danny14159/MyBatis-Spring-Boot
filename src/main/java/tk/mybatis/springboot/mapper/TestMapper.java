package tk.mybatis.springboot.mapper;

import tk.mybatis.springboot.domain.Image;

import java.util.List;

/**
 * Created by Administrator on 2016/11/3.
 */
public interface TestMapper {

    List<Image> selectImage();
}
