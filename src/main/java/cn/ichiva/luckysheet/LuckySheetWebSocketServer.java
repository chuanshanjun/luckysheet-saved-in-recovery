package cn.ichiva.luckysheet;

import cn.ichiva.luckysheet.utils.PakoGzipUtils;
import cn.ichiva.luckysheet.utils.ResponseDTO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class LuckySheetWebSocketServer extends WebSocketServer {

    Set<WebSocket> set = new HashSet<>();

    public LuckySheetWebSocketServer(int port){
        super(new InetSocketAddress(port));
    }

    JSONObject json = JSON.parseObject("{\"t\":\"v\",\"i\":\"sheet_01\",\"v\":{\"v\":123,\"ct\":{\"fa\":\"General\",\"t\":\"n\"},\"m\":\"0\"},\"r\":0,\"c\":0}");
    int n = 0;
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        set.add(conn);
        log.info("conn n = {}",++n);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        set.remove(conn);
        log.info("disConn n = {}",--n);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        if (null != message && message.length() != 0) {
            try {
                if ("rub".equals(message)) {
                    return;
                }
                String unMessage = PakoGzipUtils.unCompressURI(message);
                log.info("==>" + unMessage);
                JSONObject jsonObject = JSON.parseObject(unMessage);

                //广播
                for (WebSocket socket : set) {
                    if(conn == socket) continue;

                    if ("mv".equals(jsonObject.getString("t"))) {
                        socket.send(JSON.toJSONString(new ResponseDTO(3, "0", "0", unMessage)));
                    }else if(!"shs".equals(jsonObject.getString("t"))){
                        socket.send(JSON.toJSONString(new ResponseDTO(2, "0", "0", unMessage)));
                    }
                }
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
