package cn.ichiva;

import cn.ichiva.luckysheet.LuckySheetServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@CrossOrigin
@RestController
@Configuration
@SpringBootApplication
public class Application{

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@GetMapping("/version")
	public Object version(){
		return new Object(){ public String version = "v0.0.2"; };
	}


	@RequestMapping("/load")
	public Object load() throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get("./def.json"));
		return new String(bytes);
	}

	@Bean
	public LuckySheetServer main() throws Exception {
		LuckySheetServer server = new LuckySheetServer(11551);
		server.start();
		log.info("LuckySheetServer started on port: ws://127.0.0.1:11551");
		return server;
	}
}
