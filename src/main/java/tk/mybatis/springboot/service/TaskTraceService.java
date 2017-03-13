package tk.mybatis.springboot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import tk.mybatis.springboot.domain.TaskTrace;
import tk.mybatis.springboot.mapper.TaskTraceMapper;

/**
 * Created by Administrator on 2017/1/11.
 */
@Service
public class TaskTraceService {
    @Autowired(required = false)
    private TaskTraceMapper taskTraceMapper;

    /**插入一条任务堆栈记录，如果该任务id找到了成功的记录，则不去记录
     * @param taskTrace
     */
    public void insertOrUpdate(TaskTrace taskTrace){
        Assert.notNull(taskTrace.getId(),"tasktrace id不能为空");

        if(null != taskTraceMapper.selectByPrimaryKey(taskTrace.getId())){
            taskTraceMapper.updateByPrimaryKeySelective(taskTrace);
        }
        else{
            taskTraceMapper.insertSelective(taskTrace);
        }
    }
}
