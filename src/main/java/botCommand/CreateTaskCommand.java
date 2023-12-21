package botCommand;

import org.example.Bot;
import org.example.DBconnection;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import user.User;

public class CreateTaskCommand implements BotCommand {
    @Override
    public void execute(Update update, Bot bot) {
        Long userId = update.getMessage().getFrom().getId();
        DBconnection db = new DBconnection();
        User user = db.getUserById(userId);
        db.editState(userId, "NAME");
        SendMessage message = new SendMessage();
        message.setChatId(user.getChatId());
        message.setText("Введите название задачи" );
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
