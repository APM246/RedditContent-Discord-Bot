package me.name;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader
{
    public static String retrieveBotToken() throws IOException
    {
        Properties properties = new Properties();
        String filename = "bot_token.config";
        FileInputStream inputStream = new FileInputStream(filename);
        properties.load(inputStream);
        System.out.println(properties.getProperty("bot_token"));
        return properties.getProperty("bot_token");
    }

    public static void main(String[] args) throws Exception
    {
        ConfigReader.retrieveBotToken();
    }
}
