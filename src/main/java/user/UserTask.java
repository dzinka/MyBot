package user;

import javax.persistence.*;

@Entity
@Table(name = "usertask")
public class UserTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Long creatorId;
    private String taskId;
    private Long responsibleId;
    private String descriptione;
    private String dedline;
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
    public void setTaskId(String taskId) {this.taskId = taskId;}
    public void setResponsibleId(Long responsibleId) { this.responsibleId = responsibleId; }
    public void setDescriptione(String descriptione) {
        this.descriptione = descriptione;
    }
    public void setDedline(String dedline) {
        this.dedline = dedline;
    }
    public String getDescriptione() {
        return this.descriptione;
    }
    public String getDedline() {
        return this.dedline;
    }
}
