package botCommand;

import org.example.Bot;
import org.example.DBconnection;
import org.example.SendCallback;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

public class DeleteTaskCommand implements BotCommand {
    @Override
    public void execute(Update update, Bot bot) {
        DBconnection db = new DBconnection();
        List<String> taskNames = db.nameTaskCreator(update.getMessage().getFrom().getId());
        ArrayList<String> task = new ArrayList<String>();
        for (String i : taskNames){
            task.add("DELETE " + i);
        }
        SendCallback sendCallback = new SendCallback();
        sendCallback.execute(update, bot, task, "Выберите задачу");
    }
}
