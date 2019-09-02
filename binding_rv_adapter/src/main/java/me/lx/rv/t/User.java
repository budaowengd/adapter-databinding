package me.lx.rv.t;

/**
 * author: luoXiong
 * e-mail: 382060748@qq.com
 * date: 2019/9/2 11:40
 * version: 1.0
 * desc:
 */
abstract public class User extends ParentUser{
    public int size = 0;

    @Override
    public int size() {
        return 32;
    }
}
