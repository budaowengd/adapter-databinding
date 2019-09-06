'''
https://github.com/evant/binding-collection-adapter
1个item布局对应:
    1个 OnItemBind
    1个 XmlItemBinding

OnItemBindModel
    管理1个绑定回调对象,和每个布局对应的变量id
    管理1个item对应的Vo对象class文件

OnItemBindClass : OnItemBind
    管理多个绑定回调对象,和每个布局对应的变量id
    管理多个item对应的Vo对象class文件

OnItemBind
    绑定回调对象,在Adapter的getItemViewType()调用

XmlItemBinding
    布局信息对象

OnItemBindModel : OnItemBind
    绑定单个item布局

ItemBindingModel
    item模型对象,让itemVo实现该接口

https://www.cnblogs.com/aademeng/articles/10511741.html
实现recyclerview的时候，通常需要实现adapter跟viewholder，首先我们要明白adapter里面各个方法的调用顺序。
1. 首先调用getItemCount()，作为recyclerview里的item数量
2. 调用getItemViewType(int position)，该方法返回一个int值作为onCreateViewHolder中的viewtype参数
3. 调用onCreateViewHolder(ViewGroup parent, int viewType)
4. 调用onBindViewHolder(BaseViewHolder holder, int position)