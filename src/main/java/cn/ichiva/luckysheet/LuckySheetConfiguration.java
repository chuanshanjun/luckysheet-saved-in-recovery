package cn.ichiva.luckysheet;

import lombok.extern.slf4j.Slf4j;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.DbImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

@Slf4j
@Configuration
public class LuckySheetConfiguration {

    //启动ws服务
    @Bean
    public LuckySheetWebSocketServer main() throws Exception {
        LuckySheetWebSocketServer server = new LuckySheetWebSocketServer(11551);
        server.start();
        log.info("LuckySheetServer started on port: ws://127.0.0.1:11551");
        return server;
    }

    //为了简化部署,这里使用嵌入式kv数据库
    @Bean
    public DB getLeveDB() throws IOException {
        return new DbImpl(new Options(),new File("./data"));
    }
}
