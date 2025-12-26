package io.github.hateud;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Bot extends TelegramLongPollingBot {

    //    public Map<Long, User> users = new HashMap<>();
    public Map<Long, Integer> messages;
    public Integer msgId;
    private final MsgHandler handler;
    private final DataBase db;
    public Bot(DataBase db) throws SQLException {
        this.handler = new MsgHandler(this, db);
        this.db = db;
        messages = db.getMsgs();
    }


    @Override
    public String getBotUsername() { return "johnsdrugbot"; }

    @Override
    public String getBotToken() { return ""; }

    @Override
    public void onRegister() {
        super.onRegister();
    }


    @Override
    public void onUpdateReceived(Update update) {

        try {
            handler.update(update);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(Long userId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(userId);
        message.setText(text);
        Message msg = execute(message);
        msgId = msg.getMessageId();
        db.addMsg(userId, msgId);
    }

    public void editMsg(Long userId, String text){
        messages = db.getMsgs();
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(userId);
        editMessage.setMessageId(messages.get(userId));
        editMessage.setText(text);
        System.out.println(messages.get(userId));
        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }
    public void deleteMsg(Long userId, int msgId){
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setMessageId(msgId);
        deleteMessage.setChatId(userId);
        try {
            execute(deleteMessage);

        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendFiles(Long userId) throws TelegramApiException {
        String baseDir = System.getProperty("user.dir");
        String scriptPath = baseDir + "\\app\\resources\\scripts\\main.exe";
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(userId);
        sendDocument.setDocument(new InputFile(new File("start.bat")));
        execute(sendDocument);
        sendDocument.setDocument(new InputFile(new File(scriptPath)));
        execute(sendDocument);
    }
//    public void addUser(Long userId){
//        users.putIfAbsent(userId, new User(userId));
//    }
}


