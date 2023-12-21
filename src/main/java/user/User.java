package user;
import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    private Long userId;
    private String userName;
    private String chatId;
    private String userState;
    public void setUserState(String userState) { this.userState = userState; }
    public void setUserId(Long userId) {this.userId = userId;}
    public void setUserName(String userName) {this.userName = userName;}
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
    public String getChatId() { return this.chatId;}
    public String getUserName() {
        return this.userName;
    }
    public String getUserState() { return userState;}

}
