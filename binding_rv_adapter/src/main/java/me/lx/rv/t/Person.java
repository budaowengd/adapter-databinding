package me.lx.rv.t;

/**
 * author: luoXiong
 * e-mail: 382060748@qq.com
 * date: 2019/9/2 11:11
 * version: 1.0
 * desc:
 */
abstract public class Person {
    abstract void eat();

    private int age;
    private String name;

    protected Person() {
    }

    public Person(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public Person(int age) {
        this.age = age;
    }

    public Person(String name) {
        this.name = name;
    }
}
