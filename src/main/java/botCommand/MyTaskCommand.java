package botCommand;

import org.example.Bot;
import org.example.SendCallback;
import org.telegram.telegrambots.meta.api.objects.Update;
import user.User;
import user.UserTask;

import java.util.ArrayList;
import java.util.List;

public class MyTaskCommand implements BotCommand {
    @Override
    public void execute(Update update, Bot bot) {
        Long userId = update.getMessage().getFrom().getId();
        List<String> taskNames = bot.db.nameTask(userId);
        ArrayList<String> task = new ArrayList<String>();
        for (String i : taskNames){
            task.add("MYTASK " + i);
        }
        SendCallback sendCallback = new SendCallback();
        sendCallback.execute(update, bot, task, "Выберите задачу");
    }
}
