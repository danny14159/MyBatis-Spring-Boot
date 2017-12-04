package tk.mybatis.springboot.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ExcelUtil {

    public static void createSheet(HSSFWorkbook workbook, String title, String[] headers, Collection<?> dataSet) {
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
        if(null != headers){
            HSSFRow row = sheet.createRow(0);
            for (short i = 0; i < headers.length; i++) {
                HSSFCell cell = row.createCell(i);
                cell.setCellStyle(style);
                HSSFRichTextString text = new HSSFRichTextString(headers[i]);
                cell.setCellValue(text);
            }
        }

        // 遍历集合数据，产生数据行
        int index = 0;
        HSSFFont font3 = workbook.createFont();
        font3.setColor(HSSFColor.BLACK.index);
        for (Object t : dataSet) {
            HSSFRow row = sheet.createRow(index);
            // 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
            Field[] fields = t.getClass().getDeclaredFields();
            List<Field> fieldList = Arrays.asList(fields);
            fieldList.sort((o1,o2)->{

                ExcelField o1Annotation = o1.getAnnotation(ExcelField.class);
                ExcelField o2Annotation = o2.getAnnotation(ExcelField.class);
                if(o1Annotation != null && o2Annotation!= null){
                    return o1Annotation.order() - o2Annotation.order();
                }
                return 0;
            });
            for (int i = 0, len = fields.length, celli = 0; i < len; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                String fieldName = field.getName();
                //String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                HSSFCell cell = row.createCell(celli++);
                cell.setCellStyle(style2);
                try {
                    Object value = field.get(t);
                    if (null == value){
                        ExcelField annotation = field.getAnnotation(ExcelField.class);
                        if(annotation != null && annotation.value()!=null){
                            value = annotation.value();
                        }
                        else{
                            value = "";
                        }
                    }

                    // 判断值的类型后进行强制类型转换
                    String textValue = null;
                    if (value instanceof Date || value instanceof Timestamp) {
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
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                } finally {
                    // 清理资源
                }
            }

            index++;
        }
    }

    @Target({ElementType.FIELD,ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    public @interface ExcelField{
        int order() default 1;
        String value() default "";
        String name() default "";
    }
}
