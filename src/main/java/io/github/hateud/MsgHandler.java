package io.github.hateud;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MsgHandler {
    private final Map<Long, String> userStates = new HashMap<>();
    private final  Map<Long, String> tempData = new HashMap<>();
    private final Bot bot;
    private final DataBase db;
    String[] parts;

    public MsgHandler(Bot Bot, DataBase db) {
        this.db = db;
        this.bot = Bot;
    }

    public void update(Update update) throws TelegramApiException, IOException {

        if (update.hasMessage()) {
            String text = update.getMessage().getText();
            long userId = update.getMessage().getChatId();
            bot.deleteMsg(userId, update.getMessage().getMessageId());
            if (text.startsWith("/")) handleCommand(userId, text);
            else{
                switch (userStates.getOrDefault(userId, "0")) {
                    case "Api":
                        parts = text.split(",");
                        db.setApi(userId, Long.parseLong(parts[0]), parts[1]);
                        bot.editMsg(userId, "Вы зарегистрировали api! Введите /config для настрйки конфига.");
                        break;
                    case "channels":
                        parts = text.split(",");
                        String channels = ListConverter.toStr(parts);
                        tempData.put(userId, channels);
                        bot.editMsg(userId, "Введите триггер-слова через запятую \nНапример: дом,база,гора");
                        userStates.put(userId, "triggers");
                        break;

                    case "triggers":
                        parts = text.split(",");
                        String triggers = ListConverter.toStr(parts);
                        db.setConfig(userId, tempData.get(userId), triggers);
                        bot.sendMessage(userId, "Вы загрузили конфигурацию! Для запуска скрипта введите /sp");
                        break;
                }
            }
        }
    }

    public void handleCommand(Long userId, String text) throws TelegramApiException, IOException {

        switch (text) {
            case "/start":
                bot.sendMessage(userId, "Для использования бота необходимо заполнить форму.\nТакие поля, как apiId и ApiHash можно получить по ссылке https://my.telegram.org/auth \n После получиния этих данных напишите комманду /reg (вводится один раз для регистрации api) \nЕсли вам необходимо поменять конфиг, те каналы и треггер-слова пропишите /config. \nЕсли все настроено, то получите дальнейшие инструкции командой /sp");
                userStates.put(userId, "0");
                break;
            case "/reg":
                if (db.getUsersList().contains(userId)) {
                    bot.editMsg(userId, "Вы уже зарегистрировали api,\n введите /config для настройки конфига или /sp для дальнейших инструкций.");
                    break;
                }
                else {
                    db.addUser(userId);
                    bot.editMsg(userId, "Введите следующие поля через запятую ApiId, ApiHash:\nНапример: 123456789,sahjdhgafawgaifgawbfuyabs ");
                    userStates.put(userId, "Api");
                    break;
                }
            case "/config":
                userStates.put(userId, "channels");
                bot.editMsg(userId, "Введите каналы через запятую(ссылка на канал без t.me/) \n Например: news,qwe,asd");
                break;
            case "/sp":
                bot.editMsg(userId, "Вы получили файлы для запуска. Если у вас есть уже main.exe вы можете его не заменять. \n ❗❗❗❗❗❗❗❗❗\nПосле скачивания расположите файлы в одной папке.\n❗❗❗❗❗❗❗❗❗\nОткройте start.bat, после этого скрипт будет запущен. \nВ первый запуск необходимо пройти процесс авторизации. Нужно ввести номер телефона аккаунта с корого будут читаться сообщения, а после код, который придет на аккаунт. \nНа этом процесс настройки будет закончен. Скрипт будет работать пока запущено приложение.");
                BatCreator.createBat(userId, db);
                bot.sendFiles(userId);
                BatCreator.deleteBat();
                break;
            case "/clear":
                db.clearData(userId);
                bot.editMsg(userId, "Вы очистили базу данных. Начните новый диалог командой /start");
                break;
            default:
                break;

        }
    }
}