package cn.ichiva;

import cn.ichiva.luckysheet.LuckySheetServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Configuration
@SpringBootApplication
public class Application{

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@GetMapping("/version")
	public Object version(){
		return new Object(){ public String version = "v0.0.1"; };
	}

	@Bean
	public LuckySheetServer main() throws Exception {
		LuckySheetServer server = new LuckySheetServer(11551);
		server.start();
		log.info("LuckySheetServer started on port: ws://127.0.0.1:11551");
		return server;
	}
}
