package cn.ichiva.luckysheet.entity;

import lombok.Data;

/**
 * 应答
 */
@Data
public class Response {
    private int code;
    private Object data;
    private String msg;
    private String id;
}
