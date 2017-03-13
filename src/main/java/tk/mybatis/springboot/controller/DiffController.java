package tk.mybatis.springboot.controller;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import tk.mybatis.springboot.mapper.DiffMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2016/10/12.
 */
@RequestMapping("/diff")
@Controller
public class DiffController {

    @Autowired(required = false)
    private DiffMapper diffMapper;

    @RequestMapping("/download")
    public void select1(HttpServletResponse response){
        List<Map> dalian1 = diffMapper.select1("dalian1");

        Workbook workbook = new HSSFWorkbook();

        Sheet sheet1 = workbook.createSheet("大连一区 底层有数据，step没有数据");


    }

    public void createSheet(String name,List<Map> data ,Workbook workbook){
        Sheet sheet = workbook.createSheet(name);

        int index = 0;
        for(Map map:data){
            Row row = sheet.createRow(index++);

            Set<String> set = map.keySet();

            int jIndex = 0;
            for(String key:set){
                Cell cell = row.createCell(jIndex);

            }
        }
    }
}
