package me.lx.rv

/**
 *  author: luoXiong
 *  e-mail: 382060748@qq.com
 *  date: 2020/6/5 15:03
 *  version: 1.0
 *  desc: 让 BaseRvRefreshAndRepoModel 去实现该接口,
 *  Manager 实现 RvBindListener 接口,通过mModel去调用
 */
interface RvBindModel<D> : RvBindListener<D> {
}