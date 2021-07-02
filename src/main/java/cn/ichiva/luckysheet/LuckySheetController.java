package cn.ichiva.luckysheet;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.iq80.leveldb.DB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@CrossOrigin
@RestController
public class LuckySheetController {

    @Autowired
    DB db;

    private static List<String> dbList = new CopyOnWriteArrayList<>();

    @Value("${website}")
    String website;


    @GetMapping("/version")
    public Object version(){
        return new Object(){ public String version = "v0.0.3"; };
    }

    //取文件
    @RequestMapping("/get")
    public Object get() throws IOException {
        return new String(db.get(Keys.FILE), StandardCharsets.UTF_8);
    }

    //设置文件
    @PostMapping("/set")
    public Object set(String jsonExcel) throws IOException {
        db.put(Keys.FILE,jsonExcel.getBytes(StandardCharsets.UTF_8));
        return true;
    }

    // curl -X POST http://localhost:11427/upload \
    //  -F "file=@/Users/chuanshan/Documents/open-storage-collect.xlsx" \
    //  -H "Content-Type: multipart/form-data"
    //upload-excel (form表单上传)
    @PostMapping("/upload")
    public boolean upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            try (BufferedOutputStream buffedOut = new BufferedOutputStream(new FileOutputStream(new File("./tmp/" + file.getOriginalFilename())))) {
                buffedOut.write(file.getBytes());
                buffedOut.flush();
            }

            return true;
        }
        return false;
    }

    // localhost:11427/list
    //list (展示tmp目录和db中有的文件)
    @GetMapping("/list")
    public Object list() {
        // tmp目录下文件名
        List<FileDTO> results = new ArrayList<>();

        // 1 遍历 tmp目录
        String path = "./tmp";
        File file = new File(path);
        File[] fs = file.listFiles();

        if (fs != null && fs.length != 0) {
            for (int i = 0; i < fs.length; i++) {
                if (!fs[i].isDirectory()) {
                    String fileName = fs[i].getName();
                    if (!dbList.contains(fileName)) {
                        FileDTO fileDTO = new FileDTO();
                        fileDTO.setName(fileName);
                        fileDTO.setDownloadURL("http://" + website + ":11427" + "/download" + "?filename=" + fileName);
                        results.add(fileDTO);
                    }
                }
            }
        }

        // 2 遍历levelDB
        dbList.forEach(x -> {
            FileDTO fileDTO = new FileDTO();
            fileDTO.setName(x);
            fileDTO.setDownloadURL("http://" + website + ":11427" + "/getFile" + "?filename=" + x);
            results.add(fileDTO);
        });

        return results;
    }

    @Getter
    @Setter
    class FileDTO {
        private String name;
        private String downloadURL;
    }

    // localhost:11427/transfer?filename=open-storage-collecxt.xlsx
    //文件是否已经转换
    @GetMapping("/transfer")
    public boolean transfer(@PathParam("filename") String filename) {
        return db.get(filename.getBytes(StandardCharsets.UTF_8)) != null;
    }

    // http://127.0.0.1:11472/download?filename=open-storage-collect.xlsx
    //文件下载接口下载tmp中的excel
    @GetMapping("/download")
    public boolean download(@PathParam("filename") String filename, HttpServletResponse response) {
        File f = new File("./tmp/" + filename);
        if (f.exists()) {
            try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));) {
                byte[] buff = new byte[1024];
                OutputStream os  = response.getOutputStream();
                int i = 0;
                while ((i = bis.read(buff)) != -1) {
                    os.write(buff, 0, i);
                    os.flush();
                }
            } catch (IOException e) {
                log.error("{}",e);
                return false;
            }
        }
        return true;
    }

    // localhost:11427/setFile?filename=open-storage-collect.xlsx
    // set/filename
    @PostMapping("/setFile")
    public Object setFile(@PathParam("filename") String filename, String jsonExcel) throws IOException {
        if (StringUtils.isEmpty(filename) || StringUtils.isEmpty(jsonExcel)) {
            return false;
        }

        db.put(filename.getBytes(StandardCharsets.UTF_8), jsonExcel.getBytes(StandardCharsets.UTF_8));
        dbList.add(filename);
        return true;
    }

    // localhost:11427/getFile?filename=open-storage-collect.xlsx
    // get/filename
    @RequestMapping("/getFile")
    public Object getFile(@PathParam("filename") String filename) throws IOException {
        return new String(db.get(filename.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }
}
