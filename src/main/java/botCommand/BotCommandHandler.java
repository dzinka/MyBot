package botCommand;

import org.example.Bot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

public class BotCommandHandler {
    private Map<String, BotCommand> commandMap;

    public BotCommandHandler() {
        commandMap = new HashMap<>();
        commandMap.put("/start", new StartCommand());
        commandMap.put("/help", new HelpCommand());
        commandMap.put("/create_task", new CreateTaskCommand());
        commandMap.put("/edit_task", new EditTaskCommand());
        commandMap.put("/my_task", new MyTaskCommand());
        commandMap.put("/delete_task", new DeleteTaskCommand());
    }

    public void processCommand(Update update, Bot bot) {
        String command = update.getMessage().getText();
        BotCommand botCommand = commandMap.get(command);
        if (botCommand != null) {
            botCommand.execute(update, bot);
        } else {
            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId().toString());
            message.setText("Неизвестная команда. Введите /help для получения справки.");
            try {
                bot.execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}