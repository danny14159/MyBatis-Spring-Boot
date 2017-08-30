package tk.mybatis.springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tk.mybatis.springboot.util.DbHelper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.security.PublicKey;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        for(User user:userSet){
                String id = (String) restTemplate.getForObject("http://sso.newtouch.com/api/user-by-id/"+user.getId(),Map.class).get("id");
                ExportModule.user.executeUpdate("update user set id='"+id+"' where id='"+user.getLoginname()+"'");
        }

    }

    static DbHelper[] d = new DbHelper[]{
            new DbHelper("jdbc:mysql://localhost:8101/nsc_console?useUnicode=true&characterEncoding=utf8&useSSL=false", "nscread", "nread!@#QWE", "华东一区"),
            new DbHelper("jdbc:mysql://localhost:8102/nsc_console?useUnicode=true&characterEncoding=utf8&useSSL=false", "nscread", "nread!@#QWE", "华东二区"),
            new DbHelper("jdbc:mysql://localhost:8103/nsc_console?useUnicode=true&characterEncoding=utf8&useSSL=false", "nscread", "nread!@#QWE", "华东三区"),
            new DbHelper("jdbc:mysql://localhost:8104/nsc_console?useUnicode=true&characterEncoding=utf8&useSSL=false", "nscread", "nread!@#QWE", "华东四区"),
            new DbHelper("jdbc:mysql://localhost:8106/nsc_console?useUnicode=true&characterEncoding=utf8&useSSL=false", "nscread", "nread!@#QWE", "上海一区"),
            new DbHelper("jdbc:mysql://localhost:8109/nsc_console?useUnicode=true&characterEncoding=utf8&useSSL=false", "nscread", "read123QWE", "西南二交易云"),
            new DbHelper("jdbc:mysql://localhost:8108/nsc_console?useUnicode=true&characterEncoding=utf8&useSSL=false", "nscread", "read123QWE", "西南二开发云"),
            new DbHelper("jdbc:mysql://localhost:8110/nsc_console?useUnicode=true&characterEncoding=utf8&useSSL=false", "nscread", "read123QWE", "西南一交易云")
    };

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        DbHelper dbHelper = new DbHelper("jdbc:mysql://localhost:8101/operation?useUnicode=true&characterEncoding=utf8&useSSL=false", "nscread", "nread!@#QWE", "华东一区");
        Set<InternalPay> internalPays = dbHelper.executeQuery("select * from internal_pay", InternalPay.class);
        internalPays.forEach(i->{
            try {
                i.setAmount(objectMapper.readValue(i.getOrder_detail(),Map.class).get("orderMoney"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        File file = new File("d:/export/内部支付记录.xls");
        exportExcel("sheet1", new String[]{}, internalPays, file);
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

    static public Set<User> getUserMap(){
        if(null == userSet) {
            userSet = user.executeQuery("select id,realname,email,mobile from user", User.class);
        }
        return userSet;
    }

    public static void main1(String[] args) throws Exception {
        List<User> userIds = getUserIds();


        int index = 0;
        System.out.println(++index + "/" + userIds.size());
        File file = new File("d:/export/虚机.xls");
        List<UserBean> list = new ArrayList<>();
        int n = 0;
        for (DbHelper i : d) {
            if (n == 0) {
                list.add(new RetPublicIpItem());
            }
            list.addAll(i.executeQuery("SELECT '" + i.getRegionName() + "' regionName,d.user_id userId, \n" +
                    "    d.id as id, d.name as name, d.ip as ip, d.band_width as bandWidth, d.charge_mode as chargeMode,\n" +
                    "    d.ipline as ipline, d.create_time as createTime, d.update_time as updateTime,d.expire_time as expireTime,\n" +
                    "    d.no as no, d.user_id as user_id, d.`desc` as `desc`, d.openid as openid, d.delete_flag as deleteFlag,\n" +
                    "    rs.self_status as status,\n" +
                    "    rs.used as used,\n" +
                    "      ifnull(ifnull((host.name),(load_balance.name)),(router.name)) as resource,\n" +
                    "      ifnull(ifnull((host.id),(load_balance.id)),(router.id)) as resourceId\n" +
                    "    from public_ip d\n" +
                    "    left join resource_status rs on rs.id=d.id\n" +
                    "    LEFT JOIN host on host.public_ip_id = d.id\n" +
                    "    LEFT JOIN load_balance on load_balance.public_ip_id = d.id\n" +
                    "    LEFT JOIN router on router.public_ip_id = d.id\n" +
                    "    where  d.delete_flag = 0", RetPublicIp.class));
            n++;
        }
        n = 0;
        for (DbHelper i : d) {
            if (n == 0) {
                list.add(new HostListItem());
            }
            list.addAll(i.executeQuery("SELECT '" + i.getRegionName() + "'regionName ,host.user_id userId, host.id,`host`.name,`host`.`desc`,type,image_id as imageId,\n" +
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
                    "        host.expire_time as expireTime,\n" +
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
                    "          WHERE host.delete_flag = 0", HostList.class));
            n++;
        }
        n = 0;
        for (DbHelper i : d) {
            if (n == 0) {
                list.add(new DiskListItem());
            }
            list.addAll(i.executeQuery("select '" + i.getRegionName() + "'regionName,d.user_id userId,\n" +
                    "    d.id as id, d.name as name, d.`desc` as `desc`, d.type as type, d.capacity as capacity,\n" +
                    "    d.tag_name as tagName, d.user_id as user_id, d.create_time as createTime,d.begin_time as beginTime,\n" +
                    "    d.update_time as updateTime, d.no as `no`, d.expire_time as expireTime,\n" +
                    "    rs.self_status as status,\n" +
                    "    rs.used as used,\n" +
                    "    version,\n" +
                    "    (select volumn_name from host_disk where disk_id=d.id) as volumnName\n" +
                    "    from disk d\n" +
                    "    LEFT JOIN resource_status rs on rs.id = d.id\n" +
                    "    where d.delete_flag = 0", DiskList.class));
            n++;
        }
        List<UserBean> list2 = new ArrayList<>();
        Map<String,User> userMap = new HashMap<>();
        getUserMap().forEach((i)-> userMap.put(i.getId(),i));

        for(UserBean i:list){
            if(userIds.contains(new User(i.getUserId())) || "用户ID".equals(i.getUserId())){
                User user = userMap.get(i.getUserId());
                if(null != user) {
                    i.setRealname(user.getRealname());
                    i.setEmail(user.getEmail());
                    i.setMobile(user.getMobile());
                }
                list2.add(i);
            }
        }
        System.out.println("资源记录一共："+list2.size());
        exportExcel("sheet1", new String[]{}, list2, file);


    }

    public static void exportExcel(String title, String[] headers, Collection<?> dataSet, File out) {
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 生成一个表格
        HSSFSheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth((short) 15);
        // 生成一个样式
        HSSFCellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(HSSFColor.WHITE.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 生成一个字体
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.BLACK.index);
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 把字体应用到当前的样式
        style.setFont(font);
        // 生成并设置另一个样式
        HSSFCellStyle style2 = workbook.createCellStyle();
        style2.setFillForegroundColor(HSSFColor.WHITE.index);
        style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        // 生成另一个字体
        HSSFFont font2 = workbook.createFont();
        font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        // style2.setFont(font2);

        // 声明一个画图的顶级管理器
        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
        // 定义注释的大小和位置,详见文档

        // 产生表格标题行
        HSSFRow row = sheet.createRow(0);
        for (short i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(style);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
        }

        // 遍历集合数据，产生数据行
        //Iterator<?> it = dataSet.iterator();
        int index = 0;
        HSSFFont font3 = workbook.createFont();
        font3.setColor(HSSFColor.BLACK.index);
        for (Object t : dataSet) {
            index++;
            row = sheet.createRow(index);
            // 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
            Method[] fields = ReflectionUtils.getAllDeclaredMethods(t.getClass());
            for (int i = 0,len = fields.length,celli = 0; i < len; i++) {
                Method getMethod = fields[i];
                String fieldName = getMethod.getName();
                //String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                if("getClass".equals(fieldName) || !fieldName.startsWith("get")){continue;}
                HSSFCell cell = row.createCell(celli++);
                cell.setCellStyle(style2);
                try {
                    Object value = getMethod.invoke(t);
                    if (null == value)
                        value = "";

                    // 判断值的类型后进行强制类型转换
                    String textValue = null;
                    if (value instanceof Boolean) {
                        boolean bValue = (Boolean) value;
                        textValue = "男";
                        if (!bValue) {
                            textValue = "女";
                        }
                    } else if (value instanceof Date || value instanceof Timestamp) {
                        Date date = (Date) value;
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        textValue = sdf.format(date);
                    } else if (value instanceof byte[]) {
                        // 有图片时，设置行高为60px;
                        row.setHeightInPoints(60);
                        // 设置图片所在列宽度为80px,注意这里单位的一个换算
                        sheet.setColumnWidth(i, (short) (35.7 * 80));
                        // sheet.autoSizeColumn(i);
                        byte[] bsValue = (byte[]) value;
                        HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short) 6, index, (short) 6,
                                index);
                        anchor.setAnchorType(2);
                        patriarch.createPicture(anchor, workbook.addPicture(bsValue, HSSFWorkbook.PICTURE_TYPE_JPEG));
                    } else {
                        // 其它数据类型都当作字符串简单处理
                        textValue = value.toString();
                    }
                    // 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
                    if (textValue != null) {
                        Pattern p = Pattern.compile("^//d+(//.//d+)?$");
                        Matcher matcher = p.matcher(textValue);
                        if (matcher.matches()) {
                            // 是数字当作double处理
                            cell.setCellValue(Double.parseDouble(textValue));
                        } else {
                            HSSFRichTextString richString = new HSSFRichTextString(textValue);
                            richString.applyFont(font3);
                            cell.setCellValue(richString);
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    // 清理资源
                }
            }
        }
        try {
            workbook.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RetPublicIp extends UserBean {
        private String regionName;
        private String id;
        private String name;
        private String ip;
        private Long bandWidth;
        private Integer chargeMode;
        private Integer ipline;
        private Timestamp createTime;
        private Date expireTime;
        private Integer used;
    }

    @Data
    public static class RetPublicIpItem extends UserBean implements Item {
        private String regionName = "区名";
        private String id = "ID";
        private String name = "IP名称";
        private String ip = "IP";
        private String bandWidth = "带宽";
        private String chargeMode = "计费模式";
        private String ipline = "线路";
        private String createTime = "创建时间";
        private String expireTime = "到期时间";
        private String used = "使用状态";
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HostList extends UserBean {
        private String regionName;
        private String id;
        private String openid;
        private String name;
        private Integer cpu;
        private BigDecimal memory;
        private String imageName;
        private Integer networkType;
        private String ip;
        private Timestamp expireTime;
        private String privateNetworkIp;
        private Timestamp beginTime;
        private Integer status;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HostListItem extends UserBean implements Item {
        private String regionName = "区名";
        private String id = "ID";
        private String openid = "ID";
        private String name = "主机名";
        private String cpu = "CPU";
        private String memory = "内存";
        private String imageName = "镜像名称";
        private String networkType = "网络类型";
        private String ip = "IP";
        private String expireTime = "到期时间";
        private String privateNetworkIp = "私网IP";
        private String beginTime = "创建时间";
        private String status = "虚机状态：1可用2关机3创建中4删除中500过期";
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DiskList extends UserBean {
        private String regionName;
        private String id;
        private Integer status;
        private String name;
        private Integer type;
        private Long capacity;
        private String volumnName;
        private Integer used;
        private Timestamp expireTime;
        private Timestamp beginTime;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DiskListItem extends UserBean implements Item {
        private String regionName = "区名";
        private String id = "ID";
        private String status = "状态";
        private String name = "名称";
        private String type = "类型";
        private String capacity = "容量";
        private String volumnName = "盘符";
        private String used = "使用状态";
        private String expireTime = "到期时间";
        private String beginTime = "创建时间";
    }

    interface Item {
    }


}

