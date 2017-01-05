package tk.mybatis.springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.springboot.domain.City;
import tk.mybatis.springboot.mapper.ImportMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by Administrator on 2016/10/21.
 */
@Controller
@RequestMapping("/import")
@Transactional
@Slf4j
public class ImportController {
    @Autowired
    private ImportMapper importMapper;
    @Autowired
    private RestTemplate restTemplate;

    private int beginPort =49537  ;

   /*
    public Object importData() throws Exception{
        List result = new ArrayList();
        Date now = new Date();
        result.addAll(importData("sh1","b",now));
        result.addAll(importData("sh2",null,now));
        result.addAll(importData("sh3",null,now));
        result.addAll(importData("dl1",null,now));

        return result;
    }*/
/*   @RequestMapping(value = "",method = RequestMethod.GET)
   public String index(){
        return "import";
    }*/

    @RequestMapping(value = "",method = RequestMethod.GET)
    @ResponseBody
    public List<String> importData(String r,String rt) throws Exception{
        //http://localhost:8082/import?r=sh1&rt=a
        // sh1,a sh1 b,sh2,sh3,dl1
        String regionId = importMapper.regionId(r);
        if("sh1".equals(r) && "a".equals(rt)){
            regionId = "12";
        }
        importMapper.clear();
        importMapper.deleteMap();
        importMapper.importData(r,rt,regionId);

        Runtime runtime = Runtime.getRuntime();
        if(null == rt) rt= "";
        String cmd = "\"C:\\Program Files\\MySQL\\MySQL Server 5.7\\bin\\mysqldump\" -h 192.168.99.100 -P 3307 -uroot -padmin nsc_console disk host classic_network user_resource resource_status host_disk t_nat_lan_map t_wlan_lan_map keystore notification_message --result-file=C:\\Users\\Administrator\\Desktop\\dump\\"+r+rt+".sql";
        Process process = runtime.exec(cmd);


        /*int exitVal = doWaitFor(process);
        System.out.println("Process exitValue: " + exitVal);*/
        return importMapper.verifyData();
    }

    int doWaitFor(Process process) {
        InputStream in = null;
        InputStream err = null;
        int exitValue = -1; // returned to caller when p is finished
        try {
            in = process.getInputStream();
            err = process.getErrorStream();
            boolean finished = false; // Set to true when p is finished
            while (!finished) {
                try {
                    while (in.available() > 0) {
                        // Print the output of our system call
                        Character c = new Character((char) in.read());
                        System.out.print(c);
                    }
                    while (err.available() > 0) {
                        // Print the output of our system call
                        Character c = new Character((char) err.read());
                        System.out.print(c);
                    }
                    // Ask the process for its exitValue. If the process
                    // is not finished, an IllegalThreadStateException
                    // is thrown. If it is finished, we fall through and
                    // the variable finished is set to true.
                    exitValue = process.exitValue();
                    finished = true;
                } catch (IllegalThreadStateException e) {
                    // Process is not finished yet;
                    // Sleep a little to save on CPU cycles
                    Thread.currentThread().sleep(500);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (err != null) {
                try {
                    err.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return exitValue;
    }

    @RequestMapping("/init")
    public Object init(){
        //映射
        importMapper.deleteMapTmp();
        importMapper.dumpMap();
        importMapper.deleteMap();
        return "success";
    }

    @RequestMapping("/ins")
    public String insertNatPort(){
        importMapper.deleteNatPort();

        _insertNatPort("172.23.222.");

        System.out.println(beginPort);
        return "success";
    }

    public void _insertNatPort(String inner_ip){
        Map map = new HashMap<>();
        for(int i = 2;i<=253;i++){
                map.put("inner_ip",inner_ip+(i));
                map.put("port", beginPort);
                map.put("platform","linux");
                importMapper.insertNatPort(map);
            beginPort++ ;
        }
        for(int i = 2;i<=253;i++) {
            map.put("inner_ip", inner_ip + (i));
            map.put("port", beginPort);
            map.put("platform", "windows");
            importMapper.insertNatPort(map);
            beginPort++;
        }
    }


    @RequestMapping("/409")
    @ResponseBody
    public Object test409(){
        return new ResponseEntity(new City("name","status"),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping("/test/409")
    @ResponseBody
    public Object testRestTemplate() throws Exception{

        ResponseEntity<City> result= null;
        try {
            result = restTemplate.exchange("http://localhost:8082/import/409", HttpMethod.GET, null, City.class);
        }
        catch (HttpStatusCodeException e){
            City city = new ObjectMapper().readValue(e.getResponseBodyAsString(),City.class);
            return city;
        }
        return result.getBody();
    }
}
