package cn.ichiva.luckysheet.common;

import java.lang.reflect.Array;

/**
 * 系统相关
 */
public class SystemHelper {

    /**
     * 获取系统换行符
     * @return
     */
    public static  String getLineEnd(){
        return System.getProperty("line.separator");
    }

    /**
     * 获取临时目录
     * @return
     */
    public static String getTempDir(){
        return System.getProperty("java.io.tmpdir");
    }

    public static  <T> T[] expansion(T[] arr,int nLength){
        T[] nArr = (T[]) Array.newInstance(arr[0].getClass(), nLength);
        System.arraycopy(arr,0,nArr,0,arr.length);
        return nArr;
    }
}
