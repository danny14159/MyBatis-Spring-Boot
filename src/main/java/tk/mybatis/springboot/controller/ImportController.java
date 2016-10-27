package tk.mybatis.springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.internal.util.xml.impl.Input;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.springboot.domain.City;
import tk.mybatis.springboot.mapper.ImportMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
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

    private int begionPort=46001;

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
        /*_insertNatPort("172.28.0.",45001,45254,"linux",1);
        _insertNatPort("172.28.0.",45255,45508,"windows",1);
        _insertNatPort("172.28.1.",45509,45762 ,"linux",1);
        _insertNatPort("172.28.1.",45763 ,46016 ,"windows",1);

        _insertNatPort("172.28.2.",46270  ,46522  ,"linux",2);
        _insertNatPort("172.28.2.",46523  ,46775  ,"windows",2);
        _insertNatPort("172.28.3.",46776  ,46016 ,"linux",2);
        _insertNatPort("172.28.3.",47029  ,47281  ,"windows",2);
        _insertNatPort("172.28.4.",47282  ,47534  ,"linux",2);
        _insertNatPort("172.28.4.",47535  ,47787  ,"windows",2);*/
        /*_insertNatPort("172.23.64.",45001,45252,"linux",2);
        _insertNatPort("172.23.64.",45253,45504,"windows",2);
        _insertNatPort("172.23.65.",45505,45756,"linux",2);
        _insertNatPort("172.23.65.",45757,46008,"windows",2);
        _insertNatPort("172.23.67.",46009,46260,"linux",2);
        _insertNatPort("172.23.67.",46261,46512,"windows",2);
        _insertNatPort("172.23.68.",46513,46764,"linux",2);
        _insertNatPort("172.23.68.",46765,47016,"windows",2);
        _insertNatPort("172.23.70.",47017,47268,"linux",2);
        _insertNatPort("172.23.70.",47269,47520,"windows",2);
        _insertNatPort("172.23.71.",47521,47772,"linux",2);
        _insertNatPort("172.23.71.",47773,48024,"windows",2);
        _insertNatPort("172.23.66.",48025,48276,"linux",2);
        _insertNatPort("172.23.66.",48277,48528,"windows",2);
        _insertNatPort("172.23.72.",48529,48780,"linux",2);
        _insertNatPort("172.23.72.",48781,49032,"windows",2);
        _insertNatPort("172.23.73.",49033,49284,"linux",2);
        _insertNatPort("172.23.73.",49285,49536,"windows",2);*/

        _insertNatPort("172.21.1.");
        _insertNatPort("172.21.2.");
        _insertNatPort("172.21.3.");
        _insertNatPort("172.21.4.");
        _insertNatPort("172.21.5.");
        _insertNatPort("172.21.6.");
        System.out.println(begionPort);
        return "success";
    }

    public void _insertNatPort(String inner_ip){
        Map map = new HashMap<>();
        for(int i = 0;i<252 + 252;i++){
            int netAdd = i/2;
            if(i%2 == 0){
                map.put("inner_ip",inner_ip+(netAdd+2));
                map.put("port",begionPort);
                map.put("platform","linux");
                importMapper.insertNatPort(map);
            }
            else {
                map.put("inner_ip", inner_ip + (netAdd + 2));
                map.put("port", begionPort);
                map.put("platform", "windows");
                importMapper.insertNatPort(map);
            }
            begionPort ++ ;
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
