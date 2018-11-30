package tk.mybatis.springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tk.mybatis.springboot.util.DbHelper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import static tk.mybatis.springboot.controller.ExcelUtil.createSheet;

/**
 * Created by Administrator on 2017/7/13.
 */
@Slf4j
public class ExportModule {
    public static Map<String, Object> objectToMap(Object object) {
        Map<String, Object> map = new HashMap<>();
        Method[] allDeclaredMethods = ReflectionUtils.getAllDeclaredMethods(object.getClass());
        for (Method method : allDeclaredMethods) {
            if (method.getName().startsWith("get")) {
                String temp = method.getName().substring(3);
                if (StringUtils.isEmpty(temp) || "Class".equals(temp)) continue;
                String fieldName = temp.substring(0, 1).toLowerCase() + temp.substring(1);
                try {
                    Object execute = method.invoke(object);
                    if (null != execute) {
                        map.put(fieldName, execute);
                    }
                } catch (Exception e) {
                    log.error("objectToMap失败", e);
                }
            }
        }
        return map;
    }

    static DbHelper user = new DbHelper("jdbc:mysql://183.66.65.231/test?useUnicode=true&characterEncoding=utf8&useSSL=false", "root", "cOpu=*fgK9<x", null);

    public static void main0(String[] args) {

        RestTemplate restTemplate = new RestTemplate();
        Set<User> userSet = user.executeQuery("select * from user where length(id)<32", User.class);

        for (User user : userSet) {
            String id = (String) restTemplate.getForObject("http://sso.newtouch.com/api/user-by-id/" + user.getId(), Map.class).get("id");
            ExportModule.user.executeUpdate("update user set id='" + id + "' where id='" + user.getLoginname() + "'");
        }

    }

    static DbHelper[] d = new DbHelper[]{
            //new DbHelper("jdbc:mysql://localhost:8104/nsc_console?useUnicode=true&characterEncoding=utf8&useSSL=false", "nscread", "nread!@#QWE", "华东四区"),
            new DbHelper("jdbc:mysql://localhost:8109/nsc_console?useUnicode=true&characterEncoding=utf8&useSSL=false", "nscread", "read123QWE", "西南二交易云"),
            new DbHelper("jdbc:mysql://localhost:8108/nsc_console?useUnicode=true&characterEncoding=utf8&useSSL=false", "nscread", "read123QWE", "西南二开发云"),
            new DbHelper("jdbc:mysql://localhost:8110/nsc_console?useUnicode=true&characterEncoding=utf8&useSSL=false", "nscread", "nread!@#QWE", "西南一交易云"),
            new DbHelper("jdbc:mysql://localhost:8111/nsc_console?useUnicode=true&characterEncoding=utf8&useSSL=false", "nscread", "nread!@#QWE", "西南一开发云"),
    };

    public static void main1(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        DbHelper dbHelper = new DbHelper("jdbc:mysql://localhost:8101/operation?useUnicode=true&characterEncoding=utf8&useSSL=false", "nscread", "nread!@#QWE", "华东一区");
        Set<InternalPay> internalPays = dbHelper.executeQuery("select * from internal_pay", InternalPay.class);
        internalPays.forEach(i -> {
            try {
                i.setAmount(objectMapper.readValue(i.getOrder_detail(), Map.class).get("orderMoney"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        File file = new File("d:/export/内部支付记录.xls");
    }

    public static List<User> getUserIds() throws Exception {
        Set<User> users = getUserMap();
        Set<UserBean> userBeans = new HashSet<>();
        for (DbHelper i : d) {
            userBeans.addAll(i.executeQuery("SELECT DISTINCT\n" +
                    "\t(user_id) userId\n" +
                    "FROM\n" +
                    "\thost where delete_flag = 0\n" +
                    "union SELECT DISTINCT\n" +
                    "\t(user_id) userId\n" +
                    "FROM\n" +
                    "\tdisk where delete_flag = 0\n" +
                    "union SELECT DISTINCT\n" +
                    "\t(user_id) userId\n" +
                    "FROM\n" +
                    "\tpublic_ip where delete_flag = 0", UserBean.class));
        }

        int i = 0;
        System.out.println("用户总数：" + users.size());
        System.out.println("有资源用户总数：" + userBeans.size());
        List<User> ids = new ArrayList<>();
        for (User user : users) {
            System.out.println(++i + "/" + users.size());
            if (userBeans.contains(new UserBean(user.getId()))) {
                ids.add(user);
                //System.out.println(item.getUserId());
            }
        }
        System.out.println("结果总数：" + ids.size());

        return ids;
    }

    static Set<User> userSet = null;

    static public Set<User> getUserMap() {
        if (null == userSet) {
            userSet = user.executeQuery("select id,realname,email,mobile from user", User.class);
        }
        return userSet;
    }

    static RestTemplate restTemplate = new RestTemplate();
    static ObjectMapper objectMapper = new ObjectMapper();

    static public Map<String, User> getUserFromSSO(Collection<String> userIds) {
        //Get请求，避免url过长，45个用户一个请求
        Map<String, User> ssoUserCache = new HashMap();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://sso.newtouch.com/api/users");

        int index = 0;
        for (String userId : userIds) {
            builder.queryParam("userId", userId);
            index++;
            if (index % 45 == 0 || index == userIds.size()) {
                String body = restTemplate.getForObject(builder.toUriString(), String.class);
                try {
                    List<LoginUser> loginUsers = objectMapper.readValue(body, objectMapper.getTypeFactory().constructParametrizedType(List.class, List.class, LoginUser.class));
                    System.out.println(loginUsers.size());
                    for (LoginUser loginUser : loginUsers) {
                        ssoUserCache.put(loginUser.getId(), new User(loginUser.getId(), loginUser.getDefaultMobile(), loginUser.getDefaultEmail(),
                                loginUser.getRealName(), loginUser.getLoginName()));
                    }
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
                builder = UriComponentsBuilder.fromHttpUrl("https://sso.newtouch.com/api/users");
            }

        }

        return ssoUserCache;
    }

    public static void main(String[] args) throws Exception {
        boolean selectAllUsers = true;  //是否查询所有用户的


        Collection<User> userIds = null;
        if (!selectAllUsers) {
            userIds = getUserIds();
            System.out.println("总用户数:" + userIds.size());
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        File file = new File("资源导出" + simpleDateFormat.format(new Date()) + ".xls");
        List<UserBean> list = new ArrayList<>();
        List<UserBean> iplist = new ArrayList<>();
        List<UserBean> disklist = new ArrayList<>();
        List<UserBean> hostlist = new ArrayList<>();
        int n = 0;
        for (DbHelper i : d) {
            if (n == 0) {
                list.add(new RetPublicIp());
                iplist.add(new RetPublicIp());
            }
            Set<RetPublicIp> retPublicIps = i.executeQuery("SELECT '" + i.getRegionName() + "' regionName,d.user_id userId, \n" +
                    "    d.id as id, d.name as name, d.ip as ip, d.band_width as bandWidth, d.charge_mode as chargeMode,\n" +
                    "    d.ipline as ipline, d.create_time as createTime, d.update_time as updateTime,d.update_time as updateTime,\n" +
                    "    d.no as no, d.user_id as user_id, d.`desc` as `desc`," +
                    "if(d.delete_flag=1,d.update_time,d.expire_time) as expireTime, d.openid as openid, d.delete_flag as deleteFlag,\n" +
                    "    rs.self_status as status,\n" +
                    "    rs.used as used,\n" +
                    "      ifnull(ifnull((host.name),(load_balance.name)),(router.name)) as resource,\n" +
                    "      ifnull(ifnull((host.id),(load_balance.id)),(router.id)) as resourceId\n" +
                    "    from public_ip d\n" +
                    "    left join resource_status rs on rs.id=d.id\n" +
                    "    LEFT JOIN host on host.public_ip_id = d.id\n" +
                    "    LEFT JOIN load_balance on load_balance.public_ip_id = d.id\n" +
                    "    LEFT JOIN router on router.public_ip_id = d.id\n" +
                    "   ", RetPublicIp.class);
            list.addAll(retPublicIps);
            iplist.addAll(retPublicIps);
            n++;
        }
        n = 0;
        for (DbHelper i : d) {
            if (n == 0) {
                list.add(new HostList());
                hostlist.add(new HostList());
            }
            Set<HostList> hostLists = i.executeQuery("SELECT '" + i.getRegionName() + "'regionName ,host.user_id userId, host.id,`host`.name,`host`.`desc`,type,image_id as imageId,host.delete_flag as deleteFlag,\n" +
                    "        im.cnname as imageName,\n" +
                    "        fl.cpu as cpu,\n" +
                    "        fl.memory as memory,\n" +
                    "        security_type as securityType,keystore_id as keyStoreId,\n" +
                    "        (SELECT name from keystore where keystore.id=host.keystore_id and delete_flag = 0) as keyStoreName,\n" +
                    "        sc.name as securityGroupName,pn.name as networkName ,\n" +
                    "        network_type as networkType,\n" +
                    "        public_ip_id as publicIpId,\n" +
                    "        (SELECT ip from public_ip where public_ip.id=host.public_ip_id  and delete_flag = 0) as ip,\n" +
                    "        `host`.create_time as createTime,\n" +
                    "        (case host.network_type WHEN 1 then\n" +
                    "            (SELECT cn.ip FROM classic_network cn WHERE cn.host_id=host.id)\n" +
                    "         when 2 then\n" +
                    "             (SELECT hpn.ip FROM host_private_subnetwork hpn WHERE hpn.host_id=host.id)\n" +
                    "         end) as privateNetworkIp,\n" +
                    "        rs.self_status as status,\n" +
                    "        rs.used as used,\n" +
                    "        if(host.delete_flag=1,host.update_time,host.expire_time) as expireTime,\n" +
                    "        host.update_time as updateTime,\n" +
                    "        host.begin_time as beginTime,\n" +
                    "        host.version as version,rs.openid\n" +
                    "        from host\n" +
                    "          LEFT JOIN host_private_subnetwork  hpn on hpn.host_id = host.id\n" +
                    "          LEFT JOIN private_subnetwork pn on hpn.private_subnetwork_id=pn.id\n" +
                    "          LEFT JOIN host_security_group hsg on host.id=hsg.host_id\n" +
                    "          LEFT JOIN security_group sc on sc.id = hsg.security_group_id\n" +
                    "          LEFT JOIN resource_status rs on rs.id=host.id\n" +
                    "          LEFT JOIN image im on im.id = host.image_id\n" +
                    "          LEFT JOIN flavor fl on fl.id = host.flavor_id\n" +
                    "          ", HostList.class);
            list.addAll(hostLists);
            hostlist.addAll(hostLists);
            n++;
        }
        n = 0;
        for (DbHelper i : d) {
            if (n == 0) {
                list.add(new DiskList());
                disklist.add(new DiskList());
            }
            Set<DiskList> diskLists = i.executeQuery("select '" + i.getRegionName() + "'regionName,d.user_id userId,d.delete_flag as deleteFlag,\n" +
                    "    d.id as id, d.name as name, d.`desc` as `desc`, d.type as type, d.capacity as capacity,\n" +
                    "    d.tag_name as tagName, d.user_id as user_id, d.create_time as createTime,d.begin_time as beginTime,\n" +
                    "    d.update_time as updateTime, d.no as `no`,d.update_time as updateTime, " +
                    "   if(d.delete_flag=1,d.update_time,d.expire_time) as expireTime,\n" +
                    "    rs.self_status as status,\n" +
                    "    rs.used as used,rs.openid,\n" +
                    "    version,\n" +
                    "    (select volumn_name from host_disk where disk_id=d.id) as volumnName\n" +
                    "    from disk d\n" +
                    "    LEFT JOIN resource_status rs on rs.id = d.id\n" +
                    "   ", DiskList.class);
            list.addAll(diskLists);
            disklist.addAll(diskLists);
            n++;
        }
        List<UserBean> entryList = new ArrayList<>();
        Map<String, User> userMap = new HashMap<>();
        if (!selectAllUsers) {
            Map<String, User> finalUserMap = userMap;
            getUserMap().forEach((i) -> finalUserMap.put(i.getId(), i));
        } else {
            Set<String> distinctUserIds = new HashSet<>();
            list.forEach((i) -> distinctUserIds.add(i.getUserId()));
            System.out.println("用户总数：" + distinctUserIds.size());
            userMap = getUserFromSSO(distinctUserIds);
        }

        for (UserBean i : list) {
            if (selectAllUsers || (!selectAllUsers && (userIds.contains(new User(i.getUserId())) || "用户ID".equals(i.getUserId())))) {
                User user = userMap.get(i.getUserId());
                if (null != user) {
                    i.setRealname(user.getRealname());
                    i.setEmail(user.getEmail());
                    i.setMobile(user.getMobile());
                }
                entryList.add(i);
            }
        }
        System.out.println("资源记录一共：" + entryList.size());

        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();

        createSheet(workbook, "IP原始数据", null, iplist);
        createSheet(workbook, "虚机原始数据", null, hostlist);
        createSheet(workbook, "硬盘原始数据", null, disklist);
        try {
            workbook.write(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RetPublicIp extends UserBean {
        @ExcelUtil.ExcelField(order = 1,value = "IP名称")
        private String name;
        @ExcelUtil.ExcelField(order = 2,value = "ID")
        private String id;
        @ExcelUtil.ExcelField(order = 10,value = "IP")
        private String ip;
        @ExcelUtil.ExcelField(order = 5,value = "带宽")
        private Long bandWidth;
        @ExcelUtil.ExcelField(order = 6,value = "计费模式")
        private Integer chargeMode;
        @ExcelUtil.ExcelField(order = 7,value = "线路")
        private Integer ipline;
        @ExcelUtil.ExcelField(order = 8,value = "创建时间")
        private Timestamp createTime;
        @ExcelUtil.ExcelField(order = 1000,value = "更新时间")
        private Timestamp updateTime;
        @ExcelUtil.ExcelField(order = 9,value = "到期时间")
        private Timestamp expireTime;
        @ExcelUtil.ExcelField(order = 4,value = "使用状态")
        private Integer used;
        @ExcelUtil.ExcelField(order = 3,value = "区名")
        private String regionName ;
        @ExcelUtil.ExcelField(order = 104,value = "是否删除：1已删除,0未删除")
        private String deleteFlag;
        @ExcelUtil.ExcelField(order = 102,value = "email")
        private String email ;
        @ExcelUtil.ExcelField(order = 101,value = "姓名")
        private String realname ;
        @ExcelUtil.ExcelField(order = 103,value = "mobile")
        private String mobile;
        @ExcelUtil.ExcelField(order = 100, name = "用户ID")
        private String userId ;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HostList extends UserBean {
        @ExcelUtil.ExcelField(order = 105,value = "ID")
        private String id;
        @ExcelUtil.ExcelField(order = 4,value = "虚机的后台ID")
        private String openid;
        @ExcelUtil.ExcelField(order = 104,value = "主机名")
        private String name;
        @ExcelUtil.ExcelField(order = 6,value = "CPU")
        private Integer cpu;
        @ExcelUtil.ExcelField(order = 8,value = "内存")
        private BigDecimal memory;
        @ExcelUtil.ExcelField(order = 10,value = "镜像名称")
        private String imageName;
        @ExcelUtil.ExcelField(order = 5,value = "网络类型")
        private Integer networkType;
        @ExcelUtil.ExcelField(order = 3,value = "IP")
        private String ip;
        @ExcelUtil.ExcelField(order = 2,value = "到期时间")
        private Timestamp expireTime;
        @ExcelUtil.ExcelField(order = 11,value = "私网IP")
        private String privateNetworkIp;
        @ExcelUtil.ExcelField(order = 9,value = "创建时间")
        private Timestamp beginTime;
        @ExcelUtil.ExcelField(order = 1000,value = "更新时间")
        private Timestamp updateTime;
        @ExcelUtil.ExcelField(order = 7,value = "虚机状态：1可用2关机3创建中4删除中500过期")
        private Integer status;
        @ExcelUtil.ExcelField(order = 1,value = "区名")
        private String regionName ;
        @ExcelUtil.ExcelField(order = 106,value = "是否删除：1已删除,0未删除")
        private String deleteFlag;
        @ExcelUtil.ExcelField(order = 102,value = "email")
        private String email ;
        @ExcelUtil.ExcelField(order = 101,value = "姓名")
        private String realname ;
        @ExcelUtil.ExcelField(order = 103,value = "mobile")
        private String mobile;
        @ExcelUtil.ExcelField(order = 100, name = "用户ID")
        private String userId ;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DiskList extends UserBean {
        @ExcelUtil.ExcelField(order = 2,value = "ID")
        private String id;
        @ExcelUtil.ExcelField(order = 7,value = "硬盘的后台ID")
        private String openid;
        @ExcelUtil.ExcelField(order = 8,value = "状态")
        private Integer status;
        @ExcelUtil.ExcelField(order = 1,value = "名称")
        private String name;
        @ExcelUtil.ExcelField(order = 3,value = "类型")
        private Integer type;
        @ExcelUtil.ExcelField(order = 10,value = "容量")
        private Integer capacity;
        @ExcelUtil.ExcelField(order = 9,value = "盘符")
        private String volumnName;
        @ExcelUtil.ExcelField(order = 5,value = "使用状态")
        private Integer used;
        @ExcelUtil.ExcelField(order = 6,value = "到期时间")
        private Timestamp expireTime;
        @ExcelUtil.ExcelField(order = 3,value = "创建时间")
        private Timestamp beginTime;
        @ExcelUtil.ExcelField(order = 10000,value = "更新时间")
        private Timestamp updateTime;
        @ExcelUtil.ExcelField(order = 4,value = "区名")
        private String regionName ;
        @ExcelUtil.ExcelField(order = 104,value = "是否删除：1已删除,0未删除")
        private String deleteFlag;
        @ExcelUtil.ExcelField(order = 103,value = "email")
        private String email ;
        @ExcelUtil.ExcelField(order = 102,value = "姓名")
        private String realname ;
        @ExcelUtil.ExcelField(order = 101,value = "mobile")
        private String mobile;
        @ExcelUtil.ExcelField(order = 100, name = "用户ID")
        private String userId ;
    }

}

