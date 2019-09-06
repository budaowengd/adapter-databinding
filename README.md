# AdapterBinding 介绍
基于Databinding打造的高复用、简洁、高效的列表适配器.. 专注于业务, 减少重复代码,. <b>强烈建议读者继续往下观看,</b>
包含以下几种解决方案.
* 普通列表
* 分组列表
* 加载更多

#### 普通列表支持以下类型
* 单一item类型
* 带头部和脚部类型
*  多种item类型

#### 分组列表支持以下类型
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

## 原理讲解
写适配器的时候，通常需要实现adapter和viewholder，首先我们要明白adapter里面各个方法的调用顺序。
#####1. 首先调用getItemCount()，获取recyclerview里的item数量
#####2. 调用getItemViewType(int position)，该方法返回一个int值作为onCreateViewHolder中的viewtype参数
#####3. 调用 onCreateViewHolder(ViewGroup parent, int viewType)
#####4. 调用 onBindViewHolder(BaseViewHolder holder, int position)

如果要实现多类型item, 大部分第3方库采用的方案是:
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


















