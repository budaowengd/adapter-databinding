'''
https://github.com/evant/binding-collection-adapter
1个item布局对应:
    1个 OnItemBind
    1个 ItemBinding

OnItemBindModel
    管理1个绑定回调对象,和每个布局对应的变量id
    管理1个item对应的Vo对象class文件

OnItemBindClass : OnItemBind
    管理多个绑定回调对象,和每个布局对应的变量id
    管理多个item对应的Vo对象class文件

OnItemBind
    绑定回调对象,在Adapter的getItemViewType()调用

ItemBinding
    布局信息对象

OnItemBindModel : OnItemBind
    绑定单个item布局

ItemBindingModel
    item模型对象,让itemVo实现该接口
