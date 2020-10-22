package cn.ichiva.luckysheet.entity;

//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
//@ApiModel("客户端")
public class Client{
    //@ApiModelProperty("客户端名称,唯一,请求头中携带")
    private String name;

    //@ApiModelProperty("全局限流,-1=不限流(默认),0=禁止访问")
    private int limit = -1;

    //@ApiModelProperty("允许访问的url,默认允许访问所有url")
    private Set<String> urlList = new HashSet<>();


    //@ApiModelProperty("创建时间")
    private Long createTime;

    //@ApiModelProperty("最后更新时间")
    private Long updateTime;
}