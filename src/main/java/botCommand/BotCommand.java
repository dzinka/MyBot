package botCommand;

import org.example.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface BotCommand {
    void execute(Update update, Bot bot);
}
