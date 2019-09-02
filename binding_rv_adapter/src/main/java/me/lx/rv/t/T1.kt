package me.lx.rv.t

/**
 * author: luoXiong
 * e-mail: 382060748@qq.com
 * date: 2019/9/2 11:45
 * version: 1.0
 * desc:
 */
class T1 : AbstractCollection<String>() {
    override val size: Int
        get() = 32


//    override fun size(): Int{
//        val a=1
//        return 22
//    }

    override fun iterator(): Iterator<String> {
        return null!!
    }
}
