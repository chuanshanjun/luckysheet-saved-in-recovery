package cn.ichiva.luckysheet.entity;

//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
//@ApiModel("路径映射")
public class Route {
    //@ApiModelProperty("路由id,唯一")
    private String id;

    //@ApiModelProperty("路径断言,如:/appstore/**")
    private String path;

    //@ApiModelProperty("请求头断言,如:channel=appstore")
    private String header;

    //@ApiModelProperty("目标服务器,如果目标服务器多余一个将按权重进行负载均衡(灰度发布)")
    private List<Server> serverList = new ArrayList<>();


    //@ApiModelProperty("创建时间")
    private Long createTime;

    //@ApiModelProperty("最后更新时间")
    private Long updateTime;
}