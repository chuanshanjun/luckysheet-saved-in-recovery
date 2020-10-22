package cn.ichiva.luckysheet.entity;

//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
//@ApiModel("目标服务器")
public class Server{
    //@ApiModelProperty("目标服务器地址")
    private String uri;
    //@ApiModelProperty("权重,同一组目标服务器权重总和等于100")
    private int weight;
    //@ApiModelProperty("限流,-1=不限流(默认),0=禁止访问")
    private int limit = -1;
}