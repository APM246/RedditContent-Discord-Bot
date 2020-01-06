package me.name;

import java.io.*;
import java.util.Properties;

public class ConfigReader
{
    public static String retrieveBotToken() throws IOException
    {
        Properties properties = new Properties();
        InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream("bot_token.config");
        properties.load(inputStream);
        return properties.getProperty("bot_token");
    }

    public static void main(String[] args) throws Exception
    {
        ConfigReader.retrieveBotToken();
    }
}
