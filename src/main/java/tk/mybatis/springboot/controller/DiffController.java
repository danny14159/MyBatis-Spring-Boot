package tk.mybatis.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import tk.mybatis.springboot.mapper.DiffMapper;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2016/10/12.
 */
@RequestMapping("/diff")
@Controller
public class DiffController {

    @Autowired
    private DiffMapper diffMapper;

    @RequestMapping("/sel1")
    public String select1(Model model,String region){

        model.addAttribute("list",diffMapper.select1(region));
        return "diff";
    }
    @RequestMapping("/sel2")
    public String select1(Model model,String region,String code){

        model.addAttribute("list",diffMapper.select2(region,code));
        return "diff";
    }
}
