# AdapterBinding 介绍(通过数据驱动UI)
基于Databinding打造的高复用、简洁、高效的列表适配器.. 专注于业务, 减少重复代码, 不用再写rv.setAdapter()

## 包含以下几种解决方案.
* 普通列表
* 分组列表
* 加载更多

### 普通列表支持以下类型
* 单一item类型
* 带头部和脚部类型
*  多种item类型

### 分组列表支持以下类型
* 带组头组尾
* 不带组头或不带组尾
* 头、子项、尾支持多种类型
* 子项支持多种类型
* 子项为Grid

## Download
```groovy
implementation 'me.tatarka.bindingcollectionadapter2:bindingcollectionadapter:3.1.1'
implementation 'me.tatarka.bindingcollectionadapter2:bindingcollectionadapter-recyclerview:3.1.1'
```
## 效果展示
![](https://github.com/luoxiong94/adapter-databinding/blob/master/pic/merge.png?raw=true)

## UML类图
![](https://github.com/luoxiong94/adapter-databinding/blob/master/pic/System.png?raw=true)
## 原理讲解
写适配器的时候，通常需要实现adapter和viewholder，首先我们要明白adapter里面各个方法的调用顺序。下面方法都是由RecyclerView自动去调用的。
## 1. 首先调用getItemCount()，让Rv知道1个列表该展示多少个item
## 2. 调用getItemViewType(int position)，Rv要知道每个位置的item对应的类型是什么.默认是0 , 即只有一种类型
## 3. 调用 onCreateViewHolder(ViewGroup parent, int viewType) 根据每种类型获取不同的item布局
## 4. 调用 onBindViewHolder(BaseViewHolder holder, int position)  我们滑动下列表,Rv就会调用该方法

如果要实现多类型item, 大部分第3方库通过map或item的某个参数来管理每种类型,,比如:
```
      override fun getItemCount(): Int {
            return listSize + headSize + footSize
      }
     override fun getItemViewType(position: Int): Int {
            if (xxx) {
                return type_1
            }else if(xxx){
                return type_2
            }else{
                return type_3
            }
      }
```
# 在介绍该库的实现原理之前,先简单描述些重要类的作用
## XmlItemBinding (单个xml布局对象)
每个item对应的布局id
每个item的数据对应的BR变量id, 默认是BR.item
每个item布局对应1个XmlItemBinding对象

## OnItemBind (接口)
Adapter执行getItemViewType()方法, 会回调该onItemBind() 函数,用来设置每个Item的布局id和变量id.
每个item布局对应1个OnItemBind对象

## OnItemBindClass (管理多类型item的对象)
只包含2个list
itemBindingClassList:  每种item对应数据对象的class文件
itemBindingList: 每种item对应的 OnItemBind 对象










