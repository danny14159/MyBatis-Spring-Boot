package tk.mybatis.springboot.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/21.
 */
public interface ImportMapper {

    void importData(@Param("region") String region,@Param("regionType") String regionType,@Param("regionId")String regionId);

    String regionId(String region);

     void insertNatPort(Map map);
    void clear();
    void deleteNatPort();
    List<String> verifyData();
    void deleteMapTmp();
    void deleteMap();
    void dumpMap();
}