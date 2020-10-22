package cn.ichiva.luckysheet.impl;

import cn.ichiva.luckysheet.MsgHandler;
import cn.ichiva.luckysheet.common.PropertiesUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.DbImpl;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * 获取表格
 */
@Slf4j
public class GetAndSetLuckySheetHandler implements MsgHandler {

    private static final byte[] SHEET_KEY;

    static {
        try {
            SHEET_KEY = "sheet".getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private DbImpl db;

    public GetAndSetLuckySheetHandler() {
        String path = "./data";
        try {
            db = new DbImpl(new Options(), new File(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        byte[] bytes = db.get(SHEET_KEY);
        if(null == bytes || bytes.length == 0){
            byte[] load = PropertiesUtil.load("init.json");
            db.put(SHEET_KEY,load);
        }
    }

    @Override
    public Object handler(JSONObject request) {
        Object action = request.get("action");
        if("get".equals(action)){
            return new String(db.get(SHEET_KEY));
        }else if("set".equals(action)){
            try {
                byte[] bytes = request.getJSONObject("data").toJSONString().getBytes("utf-8");
                db.put(SHEET_KEY,bytes);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }else {
            log.warn("未知请求,可能事件配置错误 action = {}",action);
        }

        return null;
    }
}
