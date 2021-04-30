package cn.ichiva.luckysheet.utils;

import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.DbImpl;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Level db 支持 String key
 */
public class StringDb extends DbImpl {

    public StringDb(Options options, File databaseDir) throws IOException {
        super(options, databaseDir);
    }

    public byte[] get(String key) throws DBException {
        return super.get(key.getBytes(StandardCharsets.UTF_8));
    }

    public void put(String key, byte[] value) throws DBException {
        super.put(key.getBytes(StandardCharsets.UTF_8), value);
    }

    public void put(String key, Serializable obj){
        try(ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(out)){
            objOut.writeObject(obj);
            objOut.flush();
            byte[] bytes = out.toByteArray();
            put(key,bytes);
        } catch (Exception e) {
            throw new DBException(e);
        }
    }

    public Object getObject(String key){
        byte[] bytes = get(key);
        if(null == bytes) return null;
        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes);
             ObjectInputStream objIn = new ObjectInputStream(in)){
            return objIn.readObject();
        } catch (Exception e) {
            throw new DBException(e);
        }
    }

    public static class DBException extends RuntimeException{
        public DBException(Throwable e){
            super(e);
        }
        public DBException(String msg){
            super(msg);
        }
    }
}
