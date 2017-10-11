package tk.mybatis.springboot.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/29.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {
    private String id;


    private Long firstLogin;

    private String email;

    private String loginName;

    private String mailDomain;


    @NotEmpty
    private Map<String,Long> tokens;

    private Long expire;

    private String mobile;

    private String position;

    private String companyId;

    private String realName;

    private String icon;

    private String lastUpdateTime;
    /**
     * 是否是第一次登陆
     */
    private String firstLoading;

    private List<String> roomList;//多聊房间

    private List<String> singleRoomList;//单聊房间

    private String openfireIp;

    private String imageForPhone;

    private String status;


    private String userDesc;

    private String degree;
    private String career;
    private String sex;
    private String addr;
    private String companyName;
    /**
     * 备用信息
     */
    private String extraMessage;

    private Integer totalCount;
    //用户名全拼
    private String nameALL;
    //用户名简拼
    private String nameShort;
    //是否是好友
    private Boolean ifFriend;
    //是否在线
    private Boolean isOnLine;
    private String defaultMobile;
    private String defaultEmail;
    /**
     * 用户类型
     */
    private String userType;

    /**用户邮箱列表*/
    private List<UserEmailsInfo> emails = Arrays.asList();
    /**用户手机列表*/
    private List<UserMobilsInfo> mobiles = Arrays.asList();
    /**用户邮箱web端使用*/
    private List<UserEmailWebInfo> webEmails= Arrays.asList();


    /**用户状态--激活*/
    public static final String ACTIVE_STATUS = "16002";

    private String language;



    public String getDefaultEmail(){
        if(defaultEmail!=null)
            return defaultEmail;
        if(emails==null||emails.isEmpty()){
            return null;
        }
        for(UserEmailsInfo emailsInfo:emails){
            if(emailsInfo.getPrimary()){
                return emailsInfo.getEmail();
            }
        }
        return null;
    }

    public String getDefaultMobile(){
        if(null!=defaultMobile)
            return defaultMobile;
        if(mobiles==null||mobiles.isEmpty()){
            return null;
        }
        for(UserMobilsInfo mobilsInfo:mobiles){
            if(mobilsInfo.getPrimary()){
                return mobilsInfo.getMobile();
            }
        }
        return null;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static public class UserEmailsInfo {

        private String email;

        private Boolean primary;//类型default

        private Boolean active;

    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static public class UserEmailWebInfo {
        private String email;

        private String type;// 类型default

        private String status; // 16002激活，16001未激活

    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static public class UserMobilsInfo {

        private String mobile;

        private Boolean primary;

        private Boolean active;

    }
}
