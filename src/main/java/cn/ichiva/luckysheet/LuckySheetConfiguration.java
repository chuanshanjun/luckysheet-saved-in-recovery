package cn.ichiva.luckysheet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class LuckySheetConfiguration {
    @Bean
    public LuckySheetWebSocketServer main() throws Exception {
        LuckySheetWebSocketServer server = new LuckySheetWebSocketServer(11551);
        server.start();
        log.info("LuckySheetServer started on port: ws://127.0.0.1:11551");
        return server;
    }
}
