package tk.mybatis.springboot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.springboot.mapper.ImportMapper;

/**
 * Created by Administrator on 2016/10/21.
 */
@Service
public class ImportService {

    @Autowired(required = false)
    private ImportMapper importMapper;

    public void insertHost(){
    }
}
