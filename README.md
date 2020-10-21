# luckysheet保存与恢复

## master分支是单机版本
## dev分支服务版本

### luckysheet-demo 前端
- lib 目录为编译后的文件
- index.html 目前只实现了加载后保存两个功能

    我不是专业的前端,比较简陋
### websocket-server 后端

主要依赖
```
        <!-- websocket server -->
        <dependency>
          <groupId>org.java-websocket</groupId>
          <artifactId>Java-WebSocket</artifactId>
          <version>1.4.1</version>
        </dependency>

        <!-- kv 数据库 -->
        <dependency>
            <groupId>org.iq80.leveldb</groupId>
            <artifactId>leveldb</artifactId>
            <version>0.7</version>
        </dependency>
        <dependency>
            <groupId>org.iq80.leveldb</groupId>
            <artifactId>leveldb-api</artifactId>
            <version>0.7</version>
        </dependency>

```

启动类
```
@Slf4j
public class Main {

    public static void main(String[] args) {
        String port = (String) PropertiesUtil.get("websocket.port");
        LuckySheetServer server;
        if(null != port) server = new LuckySheetServer(Integer.parseInt(port));
        else server = new LuckySheetServer();
        server.start();

        log.info("LuckySheetServer started on port: ws://127.0.0.1:{}",port);
    }
}
```

websocket-server
```
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
        log.info("onMessage = {}",s);
        if ("ping".equals(s)) {
            webSocket.send("pong");
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
```

定义请求处理接口
```
public interface MsgHandler {
    Object handler(JSONObject request);
}
```

实现get和set表格请求
```
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
        String path = PropertiesUtil.get("db.path","./data");
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
```

注册处理器
```
    public LuckySheetServer(int port){
        super(new InetSocketAddress(port));

        GetAndSetLuckySheetHandler getAndSetLuckySheetHandler = new GetAndSetLuckySheetHandler();
        handlers.put("get",getAndSetLuckySheetHandler);
        handlers.put("set",getAndSetLuckySheetHandler);
    }
```


启动,按普通java程序启动