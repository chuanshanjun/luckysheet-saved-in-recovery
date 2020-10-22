package cn.ichiva.luckysheet;

import cn.ichiva.luckysheet.common.HttpUtils;
import cn.ichiva.luckysheet.entity.Response;
import cn.ichiva.luckysheet.impl.GetAndSetLuckySheetHandler;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class LuckySheetServer extends WebSocketServer {

    private ConcurrentHashMap<WebSocket,Object> socketMap = new ConcurrentHashMap<>();
    private Map<String,MsgHandler> handlers = new HashMap<>();

    public LuckySheetServer(){
        this(80);
    }

    public LuckySheetServer(int port){
        super(new InetSocketAddress(port));

        GetAndSetLuckySheetHandler getAndSetLuckySheetHandler = new GetAndSetLuckySheetHandler();
        handlers.put("get",getAndSetLuckySheetHandler);
        handlers.put("set",getAndSetLuckySheetHandler);
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        String resource = webSocket.getResourceDescriptor();
        String uname = HttpUtils.getParameter(resource, "uname");
        String pwd = HttpUtils.getParameter(resource, "pwd");

        if(isLogin(uname,pwd)){
            socketMap.put(webSocket,new Date());
            webSocket.send("1");
            log.info("uname = {} 创建链接! num = {}",uname,socketMap.size());
        }else {
            webSocket.send("0");
            log.info("uname = {} 登陆失败!",uname);
            webSocket.close();
        }
    }

    private boolean isLogin(String uname, String pwd) {
        return true;
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        String resource = webSocket.getResourceDescriptor();
        String uname = HttpUtils.getParameter(resource, "uname");
        socketMap.remove(webSocket);
        log.info("uname = {} 关闭链接! num = {}",uname,socketMap.size());
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        if (log.isTraceEnabled()) {
            log.trace("onMessage = {}",s);
        }

        if ("ping".equals(s)) {
            webSocket.send("pong");
            return;
        }

        JSONObject request = JSON.parseObject(s);
        String action = request.getString("action");
        String id = request.getString("id");
        MsgHandler msgHandler = handlers.get(action);
        if(null == msgHandler){
            log.warn("未配置处理器 action = {}",action);
        }

        Response res = new Response();
        res.setId(id);
        try {
            Object handler = msgHandler.handler(request);
            res.setCode(200);
            res.setData(handler);
        }catch (Exception e){
            res.setCode(500);
            res.setData(e);
        }
        webSocket.send(JSON.toJSONString(res));
    }



    @Override
    public void onError(WebSocket webSocket, Exception e) {
        log.warn("WebSocket 链接异常!",e);
    }

    @Override
    public void onStart() {

    }
}
