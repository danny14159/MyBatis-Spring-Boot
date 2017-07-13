package tk.mybatis.springboot.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.springboot.util.DbHelper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.security.PublicKey;
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

    public static void main(String[] args) throws Exception {
        DbHelper user = new DbHelper("jdbc:mysql://58.17.243.161/test?useUnicode=true&characterEncoding=utf8&useSSL=false", "root", "cOpu=*fgK9<x",null);
        Set<User> userIds = user.executeQuery("select * from user limit 1", User.class);

        DbHelper[] d = new DbHelper[]{
                /***/new DbHelper("jdbc:mysql://localhost:8101/nsc_console?useUnicode=true&characterEncoding=utf8&useSSL=false", "nscread", "nread!@#QWE","华东一区"),
                /***/new DbHelper("jdbc:mysql://localhost:8102/nsc_console?useUnicode=true&characterEncoding=utf8&useSSL=false", "nscread", "nread!@#QWE","华东二区"),
                /***/new DbHelper("jdbc:mysql://localhost:8103/nsc_console?useUnicode=true&characterEncoding=utf8&useSSL=false", "nscread", "nread!@#QWE","华东三区"),
                /***/new DbHelper("jdbc:mysql://localhost:8104/nsc_console?useUnicode=true&characterEncoding=utf8&useSSL=false", "nscread", "nread!@#QWE","华东四区"),
                /***/new DbHelper("jdbc:mysql://localhost:8106/nsc_console?useUnicode=true&characterEncoding=utf8&useSSL=false", "nscread", "nread!@#QWE","上海一区"),
                /***/new DbHelper("jdbc:mysql://localhost:8109/nsc_console?useUnicode=true&characterEncoding=utf8&useSSL=false", "nscread", "read123QWE","西南二交易云"),
                /***/new DbHelper("jdbc:mysql://localhost:8108/nsc_console?useUnicode=true&characterEncoding=utf8&useSSL=false", "nscread", "read123QWE","西南二开发云"),
                /***/new DbHelper("jdbc:mysql://localhost:8110/nsc_console?useUnicode=true&characterEncoding=utf8&useSSL=false", "nscread", "read123QWE","西南一交易云")
        };

        //int index = 0;
        //for(User u:userIds) {
         //   System.out.println(++index+"/"+userIds.size());
        //    File file = new File("d:/export/"+u.getId()+".xls");
            List< UserBean> list = new ArrayList<>();
            for (DbHelper i : d) {
                boolean isFirst = i.getRegionName().equals("华东一区");
                if(isFirst) {list.add(new RetPublicIpItem());}
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
                if(isFirst) {list.add(new HostListItem());}
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
                        "        host.version as version\n" +
                        "        from host\n" +
                        "          LEFT JOIN host_private_subnetwork  hpn on hpn.host_id = host.id\n" +
                        "          LEFT JOIN private_subnetwork pn on hpn.private_subnetwork_id=pn.id\n" +
                        "          LEFT JOIN host_security_group hsg on host.id=hsg.host_id\n" +
                        "          LEFT JOIN security_group sc on sc.id = hsg.security_group_id\n" +
                        "          LEFT JOIN resource_status rs on rs.id=host.id\n" +
                        "          LEFT JOIN image im on im.id = host.image_id\n" +
                        "          LEFT JOIN flavor fl on fl.id = host.flavor_id\n" +
                        "          WHERE host.delete_flag = 0", HostList.class));
                if(isFirst) {list.add(new DiskListItem());}
                list.addAll(i.executeQuery("select '"+i.getRegionName()+"'regionName,d.user_id userId,\n" +
                        "    d.id as id, d.name as name, d.`desc` as `desc`, d.type as type, d.capacity as capacity,\n" +
                        "    d.tag_name as tagName, d.user_id as user_id, d.create_time as createTime,d.begin_time as beginTime,\n" +
                        "    d.update_time as updateTime, d.no as `no`, d.expire_time as expireTime,\n" +
                        "    rs.self_status as status,\n" +
                        "    rs.used as used,\n" +
                        "    version,\n" +
                        "    (select volumn_name from host_disk where disk_id=d.id) as volumnName\n" +
                        "    from disk d\n" +
                        "    LEFT JOIN resource_status rs on rs.id = d.id\n" +
                        "    where d.delete_flag = 0",DiskList.class));
            }
            /*if(list.size() > 3) {
                exportExcel(u.getId(), new String[]{u.getId(), u.getEmail(), u.getMobile(), u.getRealname()}, list, file);
            }*/
            for(UserBean item:list){
                if(userIds.contains(item.getUserId())){
                    System.out.println(item.getUserId());
                }
            }

        //}
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
        for(Object t:dataSet){
            index++;
            row = sheet.createRow(index);
            // 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
            Field[] fields = t.getClass().getDeclaredFields();
            for (short i = 0; i < fields.length; i++) {
                HSSFCell cell = row.createCell(i);
                cell.setCellStyle(style2);
                Field field = fields[i];
                String fieldName = field.getName();
                String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                try {
                    Class<?> tCls = t.getClass();
                    Method getMethod = tCls.getMethod(getMethodName, new Class[]{});
                    Object value = getMethod.invoke(t, new Object[]{});
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
                    } else if (value instanceof Date) {
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
                            HSSFFont font3 = workbook.createFont();
                            font3.setColor(HSSFColor.BLACK.index);
                            richString.applyFont(font3);
                            cell.setCellValue(richString);
                        }
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
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

    public static class UserBean{
        protected String userId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        private String id;
        private String mobile;
        private String email;
        private String realname;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RetPublicIp extends UserBean{
        private String regionName;
        private String id;
        private String name;
        private String ip;
        private Long bandWidth;
        private Byte chargeMode;
        private Byte ipline;
        private Date createTime;
        private Date expireTime;
        private Integer used;
    }
    @Data
    public static class RetPublicIpItem extends UserBean{
        private String regionName="区名";
        private String id="ID";
        private String name="IP名称";
        private String ip="IP";
        private String bandWidth="带宽";
        private String chargeMode="计费模式";
        private String ipline="线路";
        private String createTime="创建时间";
        private String expireTime="到期时间";
        private String used="使用状态";
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HostList  extends UserBean{
        private String regionName;
        private String id;
        private String name;
        private Integer cpu;
        private BigDecimal memory;
        private String imageName;
        private Integer networkType;
        private String ip;
        private Date expireTime;
        private String privateNetworkIp;
        private Date beginTime;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HostListItem extends UserBean{
        private String regionName="区名";
        private String id="ID";
        private String name="主机名";
        private String cpu="CPU";
        private String memory="内存";
        private String imageName="镜像名称";
        private String networkType="网络类型";
        private String ip="IP";
        private String expireTime="到期时间";
        private String privateNetworkIp="私网IP";
        private String beginTime="创建时间";
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DiskList extends UserBean{
        private String regionName;
        private String id;
        private Integer status;
        private String name;
        private Byte type;
        private Long capacity;
        private String volumnName;
        private Integer used;
        private Date expireTime;
        private Date beginTime;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DiskListItem extends UserBean{
        private String regionName="区名";
        private String id="ID";
        private String status="状态";
        private String name="名称";
        private String type="类型";
        private String capacity="容量";
        private String volumnName="盘符";
        private String used="使用状态";
        private String expireTime="到期时间";
        private String beginTime="创建时间";
    }
}
