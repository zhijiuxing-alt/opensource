package com.oceanbase.opensource;

import com.oceanbase.Utils.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.util.Date;

@Controller
public class FileModifyTime {

    @Value("${OBTimePath}")
    private String OBTimePath;

    @RequestMapping("OBTimePath")
    @ResponseBody
    public Date getOBFileModifyTime(@RequestParam("path") String path){
        return getModifyTimeByPath(OBTimePath,path);
    }

    private static Date getModifyTimeByPath(String OBTimePath, String path) {
        File file = new File(OBTimePath);
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                if (tempStr.contains(path)){
                    tempStr = tempStr.substring(0,tempStr.lastIndexOf("0800")+4);
                    return DateUtils.parseTimeZone(tempStr);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {

        String path = "/Users/lwk/oceanbase/docs/time.txt";
        Date date = getModifyTimeByPath(path,"docs-cn/1.about-oceanbase-database/2.noun-interpretation");

        System.out.println(date);
    }

}
