package cn.ichiva.luckysheet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@CrossOrigin
@RestController
public class LuckySheetController {


    @GetMapping("/version")
    public Object version(){
        return new Object(){ public String version = "v0.0.2"; };
    }


    @RequestMapping("/load")
    public Object load() throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get("./def.json"));
        return new String(bytes);
    }

}
