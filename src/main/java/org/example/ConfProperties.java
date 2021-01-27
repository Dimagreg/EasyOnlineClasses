package org.example;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

// NOT USED IN THE BUILD
public class ConfProperties {
    protected static FileInputStream fileInputStream;
    protected static Properties PROPERTIES;

    static{
        try{
            fileInputStream = new FileInputStream("resources/conf.properties"); //build path
            //fileInputStream = new FileInputStream("src\\main\\resources\\conf.properties"); //test path

            PROPERTIES = new Properties();
            PROPERTIES.load(fileInputStream);
        }catch (IOException ex){
            ex.printStackTrace();
        }
        finally {
            if(fileInputStream != null)
                try{
                    fileInputStream.close();
                }catch (IOException ex){
                    ex.printStackTrace();
                }
        }
    }

    public static String getProperty(String key){
        return PROPERTIES.getProperty(key);
    }

}

