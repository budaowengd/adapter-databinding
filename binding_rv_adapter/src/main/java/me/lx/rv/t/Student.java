package me.lx.rv.t;

/**
 * author: luoXiong
 * e-mail: 382060748@qq.com
 * date: 2019/9/2 11:09
 * version: 1.0
 * desc:
 */
public class Student extends Person {


    public Student() {
        super();
    }

    public Student(int age) {
        super(1);
    }

    public Student(String name) {
        super(name);
    }

    public Student(String name, int age) {
        super(age, name);
    }

//    public Student() {
//        super();
//    }
//
//    public Student(int age) {
//        super(1);
//    }
//
//    public Student(String name) {
//        super(name);
//    }
//
//    public Student(String name, int age) {
//        super(age, name);
//    }


    @Override
    void eat() {

    }
}
