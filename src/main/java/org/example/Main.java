package org.example;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;


public class Main {
    public static Map<String, Object> readConfig(String fileName) {
        try {
            InputStream inputStream = new FileInputStream(new File("config.yml"));

            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(inputStream);
            return data;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        var config = readConfig("config.yml");
        String botName = config.get("bot_name").toString();
        String botToken = config.get("bot_token").toString();
        System.out.println(botName + botToken);
        TelegramBotsApi telegramBotsApi = null;
        try {
            telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new Bot(botName, botToken));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}