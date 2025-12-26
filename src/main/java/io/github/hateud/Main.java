package io.github.hateud;


import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main{
    public static void main(String[] args) throws Exception{

        System.out.println("Бот запущен!");
        DataBase db = new DataBase();
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new Bot(db));

    }

}