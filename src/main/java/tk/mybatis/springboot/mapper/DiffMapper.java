package tk.mybatis.springboot.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.springboot.domain.Host;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/12.
 */
public interface DiffMapper {

    @Select("select * from `${region}-vm` left join step_tmp_vm vm on vm.openid = `${region}-vm`.ID where vm.openid is null and `${region}-vm`.name like '%-host%'")
    List<Map> select1(@Param("region") String region);

    @Select("select * from step_tmp_vm vm left join `${region}-vm` on vm.openid=`dalian1-vm`.ID where deleted = 0 and region=#{code} and `${region}-vm`.ID is null and vm.name like '%-host%'")
    List<Map> select2(@Param("region") String region, @Param("code") String code);
}
