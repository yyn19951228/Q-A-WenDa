package com.nowcoder.model;

import org.springframework.stereotype.Component;


// current login user information
@Component
public class HostHolder {
    // because all the user might be called by many users
    // ThreadLocal means local thread, store every user for every thread
    // implement by Map<ThreadID, User> basically
    private static ThreadLocal<User> users = new ThreadLocal<>();

    public User getUser() {
        return users.get();
    }

    public void setUser(User user) {
        users.set(user);
    }

    public void clear() {
        users.remove();
    }
}
