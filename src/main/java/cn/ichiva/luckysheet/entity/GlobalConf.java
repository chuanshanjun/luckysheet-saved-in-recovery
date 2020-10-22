package cn.ichiva.luckysheet.entity;

//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
//@ApiModel("全局配置")
public class GlobalConf {

    //@ApiModelProperty("全局限流,-1=不限流(默认,0=禁止访问")
    private int globalLimit = -1;

    //@ApiModelProperty("客户端列表,默认允许所有客户端访问")
    private List<Client> clientList = new ArrayList<>();
}