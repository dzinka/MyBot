package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.telegram.telegrambots.meta.api.objects.Update;
import user.User;
import user.UserTask;
import org.hibernate.criterion.Restrictions;
import org.hibernate.Criteria;

import java.util.HashMap;
import java.util.List;

public class DBconnection {
    private SessionFactory sessionFactory;
    private Configuration configuration;
    public DBconnection(){
        configuration = new Configuration().configure();
        sessionFactory = configuration.buildSessionFactory();
    }
    public void closeSessionFactory() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
    public void addUser(Update update){
        Long userId = update.getMessage().getFrom().getId();
        User existingUser = getUserById(userId);
        if (existingUser == null) {
            try (Session session = sessionFactory.openSession()) {
                Transaction transaction = session.beginTransaction();
                User user = new User();
                user.setUserId(userId);
                user.setUserName(update.getMessage().getFrom().getUserName());
                user.setChatId(update.getMessage().getChatId().toString());
                user.setUserState("START");
                session.save(user);
                transaction.commit();
            }
        }
    }
    public User getUserById(Long userId) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(User.class, userId);
        }
    }
    public void addTask(Update update, String name, String userName){
        UserTask userTask = new UserTask();
        userTask.setCreatorId(update.getMessage().getFrom().getId());
        userTask.setResponsibleId(userIdToNmae(userName));
        userTask.setTaskId(name);
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(userTask);
            transaction.commit();
        }
    }
    public void editState(Long userId, String state){
        User user = getUserById(userId);
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            user.setUserState(state);
            session.update(user);
            transaction.commit();
        }
    }
    public Long userIdToNmae(String userName){
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT user.userId FROM User user WHERE user.userName = :userName";
            Query query = session.createQuery(hql);
            query.setParameter("userName", userName);
            Long userId = (Long) query.uniqueResult();
            return userId;
        }
    }
    public List<String> nameTask(Long userId){
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT task.taskId FROM UserTask task WHERE task.responsibleId = :userId";
            Query query = session.createQuery(hql);
            query.setParameter("userId", userId);
            List<String> taskNames = query.list();

            return taskNames;
        }
    }
    public List<String> nameTaskCreator(Long userId){
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT task.taskId FROM UserTask task WHERE task.creatorId = :userId";
            Query query = session.createQuery(hql);
            query.setParameter("userId", userId);
            List<String> taskNames = query.list();

            return taskNames;
        }
    }
    public List<UserTask> getTasksByUserId(Long userId) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM UserTask WHERE creatorId = :userId";
            Query query = session.createQuery(hql);
            query.setParameter("userId", userId);
            List<UserTask> tasks = query.list();
            return tasks;
        }
    }
    public void updateTask(Long userId, String taskId, String action, String newDescription, Bot bot){
        HashMap<String, String> act = new HashMap<>();
        act.put("дедлайн", "dedline");
        act.put("описание", "descriptione");
        act.put("выполняющий", "responsibleId");
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            String hql = "UPDATE UserTask t SET t." + act.get(action) + " = :newDescription WHERE t.creatorId = :creatorId AND t.taskId = :taskId";
            Query query = session.createQuery(hql);
            query.setParameter("newDescription", newDescription);
            query.setParameter("creatorId", userId);
            query.setParameter("taskId", taskId);
            query.executeUpdate();
            transaction.commit();
        }
        bot.sendMessage("Изменения успешено добавлены", getUserById(userId).getChatId());
    }
    public void deleteTaskByTaskName(String taskName) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Criteria criteria = session.createCriteria(UserTask.class);
            criteria.add(Restrictions.eq("taskId", taskName));
            UserTask task = (UserTask) criteria.uniqueResult();
            session.delete(task);
            session.getTransaction().commit();
        }

    }
    public String infoTask(Long userId, String taskId){
        try (Session session = sessionFactory.openSession()) {
            Criteria criteria = session.createCriteria(UserTask.class);
            criteria.add(Restrictions.eq("responsibleId", userId));
            criteria.add(Restrictions.eq("taskId", taskId));
            UserTask task = (UserTask) criteria.uniqueResult();
            String info = "Описание задачи: " + task.getDescriptione() + "\n" + "Дедлайн задачи: " + task.getDedline();
            return info;
        }
    }
}
