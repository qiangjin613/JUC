package com.juc.practice.atomic;

import java.util.concurrent.atomic.AtomicReference;

class User {

    private String username;

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                '}';
    }
}

public class AtomicReferenceTest {
    public static void main(String[] args) {
        User z3 = new User("z3");
        User li4 = new User("li4");

        AtomicReference<User> atomicReference = new AtomicReference<>();
        atomicReference.set(z3);
        System.out.println(atomicReference.compareAndSet(z3, li4) + " " + atomicReference.get());
        System.out.println(atomicReference.compareAndSet(z3, li4) + " " + atomicReference.get());
    }
}
/*
使用上与 AtomicXxx 差不不大，只不过这里是允许 Object 类型的数据。
 */
