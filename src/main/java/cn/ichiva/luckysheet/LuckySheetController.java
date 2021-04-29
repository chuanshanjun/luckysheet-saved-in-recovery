package cn.ichiva.luckysheet;

import cn.ichiva.luckysheet.utils.StringDb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@CrossOrigin
@RestController
public class LuckySheetController {

    @Autowired
    StringDb db;

    String ACCOUNT_KEY = "account:%s:%s";
    String FILE_KEY = "file:%s:%s";
    @RequestMapping("/login")
    public Object login(String uname,String pwd){
        String key = getKey(uname,pwd);
        List<String> list = (List)db.getObject(key);
        if(list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    private String getKey(String uname, String pwd) {
        return String.format(ACCOUNT_KEY,uname,pwd);
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

}
