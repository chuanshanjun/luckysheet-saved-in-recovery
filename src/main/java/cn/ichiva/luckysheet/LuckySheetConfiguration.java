package cn.ichiva.luckysheet;

import lombok.extern.slf4j.Slf4j;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.DbImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Configuration
public class LuckySheetConfiguration {

    @Value("${ws.port:11551}")
    int port;

    //启动ws服务
    @Bean
    public LuckySheetWebSocketServer getWebSocketServer() throws Exception {
        LuckySheetWebSocketServer server = new LuckySheetWebSocketServer(port);
        server.start();
        log.info("LuckySheetServer started on port: ws://127.0.0.1:{}",port);
        return server;
    }

    //为了简化部署,这里使用嵌入式kv数据库
    @Bean
    public DB getLeveDB() throws IOException {
        DbImpl db = new DbImpl(new Options(), new File("./data"));

        //初始化文档
        if(db.get(Keys.FILE) == null){
            Path path = ResourceUtils.getFile("classpath:def.json").toPath();
            db.put(Keys.FILE, Files.readAllBytes(path));
        }
        return db;
    }
}
