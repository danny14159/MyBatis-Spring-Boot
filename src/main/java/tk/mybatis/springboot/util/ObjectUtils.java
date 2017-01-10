package tk.mybatis.springboot.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/1/10.
 */
public class ObjectUtils {

    public static Map obj2Map(Object thisObj) {
        Map map = new HashMap();
        Class c;
        try {
            c = thisObj.getClass();
            Method[] methods = c.getMethods();
            for (Method method : methods) {
                String methodName = method.getName();
                if (methodName.startsWith("get") && !methodName.endsWith("Class")) {
                    try {
                        Object value = method.invoke(thisObj);
                        if (value != null) {
                            String key = methodName.substring(3);
                            if (key.length() > 0) {
                                key = key.substring(0, 1).toLowerCase() + key.substring(1);
                            }
                            map.put(key, value);
                        }
                    } catch (Exception e) {
                        // Ignore
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            //Ignore
            e.printStackTrace();
        }
        return map;
    }
}
