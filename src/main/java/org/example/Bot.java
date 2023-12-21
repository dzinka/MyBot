package org.example;

import botCommand.BotCommandHandler;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import user.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    final String botName;
    final String botToken;
    private HashMap<String, String> task;

    public DBconnection db;

    public Bot(String botName, String botToken) {
        this.botName = botName;
        this.botToken = botToken;
        this.task = new HashMap<>();
        this.db = new DBconnection();
        setMyCommands();
    }
    @Override
    public String getBotUsername() {
        return this.botName;
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            db.addUser(update);

            Long userId = update.getMessage().getFrom().getId();
            User user = db.getUserById(userId);
            if (user.getUserState().equals("START")) {
                BotCommandHandler botcommand = new BotCommandHandler();
                botcommand.processCommand(update, this);
            } else if (user.getUserState().equals("NAME")) {
                task.put("NAME", update.getMessage().getText());
                db.editState(userId, "NICK");
                sendMessage("Введите ник ответственного", user.getChatId());
            }else if (user.getUserState().equals("NICK")) {
                db.addTask(update, task.get("NAME"), update.getMessage().getText());
                task = new HashMap<>();
                db.editState(userId, "START");
                sendMessage("Задача успешно добавлена", user.getChatId());
            }else if (user.getUserState().equals("EDITTASK")) {
                db.editState(userId, "START");
                db.updateTask(userId, task.get("TASK").split(" ")[1], task.get("ACTION").split(" ")[1], update.getMessage().getText(), this);
            }
        }
        if (update.hasCallbackQuery()){
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();
            Long userId = callbackQuery.getFrom().getId();
            if (data.startsWith("TASK")){
                task.put("TASK", data);
                ArrayList<String> action = new ArrayList<>(Arrays.asList("ACTION дедлайн", "ACTION описание", "ACTION статус", "ACTION выполняющий"));
                SendCallback send = new SendCallback();
                send.execute(update, this, action, "выберите действие");
            }
            if (data.startsWith("ACTION")){
                task.put("ACTION", data);
                db.editState(userId, "EDITTASK");
                sendMessage("Введите новые данные", callbackQuery.getMessage().getChatId().toString());
            }
            if (data.startsWith("MYTASK")){
                String message = db.infoTask(userId, data.split(" ")[1]);
                sendMessage(message, callbackQuery.getMessage().getChatId().toString());
            }
            if (data.startsWith("DELETE")){
                System.out.println(data.split(" ")[1]);
                db.deleteTaskByTaskName(data.split(" ")[1]);
                sendMessage("Задача успешно удалена", callbackQuery.getMessage().getChatId().toString());
            }
        }
    }
    public void sendMessage(String text, String chatId){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void setMyCommands() {
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "начать"));
        listOfCommands.add(new BotCommand("/help", "помощь"));
        listOfCommands.add(new BotCommand("/create_task", "создать задачи"));
        listOfCommands.add(new BotCommand("/edit_task", "редактировать задачу"));
        listOfCommands.add(new BotCommand("/my_task", "мои задачи"));
        listOfCommands.add(new BotCommand("/delete_task", "удалить задачу"));
        SetMyCommands setMyCommands = new SetMyCommands();
        setMyCommands.setCommands(listOfCommands);

        try {
            execute(setMyCommands);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}