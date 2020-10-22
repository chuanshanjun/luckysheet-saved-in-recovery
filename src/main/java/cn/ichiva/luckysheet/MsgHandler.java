package cn.ichiva.luckysheet;

import com.alibaba.fastjson.JSONObject;

/**
 * ws 消息处理器
 */
public interface MsgHandler {
    Object handler(JSONObject request);
}
