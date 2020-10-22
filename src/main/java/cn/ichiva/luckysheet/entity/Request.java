package cn.ichiva.luckysheet.entity;

import lombok.Data;

@Data
public class Request {

    /**
     * 请求头
     */
    private String action;

    /**
     * 请求体
     */
    private Object data;

    /**
     * 请求id
     */
    private String id;

}
