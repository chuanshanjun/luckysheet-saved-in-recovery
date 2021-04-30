package cn.ichiva.luckysheet;

import cn.ichiva.luckysheet.utils.HttpUtils;
import cn.ichiva.luckysheet.utils.PakoGzipUtils;
import cn.ichiva.luckysheet.utils.ResponseDTO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class LuckySheetWebSocketServer extends WebSocketServer {
    //存储连接及昵称
    Map<WebSocket,String> connMap = new ConcurrentHashMap<>();

    public LuckySheetWebSocketServer(int port){
        super(new InetSocketAddress(port));
    }

    int n = 0;
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        String resource = conn.getResourceDescriptor();
        String name = HttpUtils.getParameter(resource, "name");
        if(name == null) name = "" + n++;
        connMap.put(conn,name);
        log.info("{} 加入,在线人数 = {}",name,connMap.size());

    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String name = connMap.remove(conn);
        log.info("{} 离开",name);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        if (null != message && message.length() != 0) {
            try {
                if ("rub".equals(message)) {
                    return;
                }
                String unMessage = PakoGzipUtils.unCompressURI(message);
                if(log.isTraceEnabled()) log.trace(unMessage);

                JSONObject jsonObject = JSON.parseObject(unMessage);
                //广播
                String resource = conn.getResourceDescriptor();
                String name = HttpUtils.getParameter(resource, "name");
                connMap.forEach((socket,n) -> {
                    if(conn == socket) return;

                    if ("mv".equals(jsonObject.getString("t"))) {
                        socket.send(JSON.toJSONString(new ResponseDTO(3, name, name, unMessage)));
                    }else if(!"shs".equals(jsonObject.getString("t"))){
                        socket.send(JSON.toJSONString(new ResponseDTO(2, name, name, unMessage)));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {

    }

    @Override
    public void onStart() {
        log.info("ws start http://127.0.0.1:{}",getAddress().getPort());
    }
}
