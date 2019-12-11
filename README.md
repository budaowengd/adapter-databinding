# AdapterBinding 介绍
## 1、通过数据驱动UI (让开发者只需管理数据源)
## 2、高复用、简洁、高效
基于Databinding打造的高复用、简洁、高效的列表适配器.. 让开发者专注于业务, 减少写重复 rv.setAdapter(),  adapter.notify()等代码,  除了Databinding 和 RecyclerView 外无引用任何第3方库
### ps :基本上每个项目都会有列表,使用该库写1个列表,只需要你定义布局,定义点击事件,定义数据源List,5分钟搞定,剩下的全部交给该库来实现.极大的提高了效率,

## 包含以下几种解决方案.
* # 普通列表
* # 加载更多
* # 分组列表


### 普通列表支持以下类型
* # 单一item类型
* # 带头部和脚部类型
* # 多种item类型

### 分组列表支持以下类型
* # 带组头组尾
* # 不带组头或不带组尾
* # 头、子项、尾支持多种类型
* # 子项支持多种类型
* # 子项为Grid

## 集成方式
在项目的根目录下的 build.gradle添加
```groovy
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
```groovy
  implementation 'com.github.luoxiong94:adapter-databinding:1.0'
```
## 效果展示
![](https://github.com/luoxiong94/adapter-databinding/blob/master/pic/merge.png?raw=true)
![](https://github.com/luoxiong94/adapter-databinding/blob/master/pic/加载更多.png?raw=true)

![](https://github.com/luoxiong94/adapter-databinding/blob/master/pic/分组的列表.jpg?raw=true)![](https://github.com/luoxiong94/adapter-databinding/blob/master/pic/不带组头的列表.jpg?raw=true)
![](https://github.com/luoxiong94/adapter-databinding/blob/master/pic/不带组尾的列表.jpg?raw=true)![](https://github.com/luoxiong94/adapter-databinding/blob/master/pic/头、尾和子项都支持多种类型的列表.jpg?raw=true)
![](https://github.com/luoxiong94/adapter-databinding/blob/master/pic/多种子项类型的列表.jpg?raw=true)![](https://github.com/luoxiong94/adapter-databinding/blob/master/pic/可展开收起的列表.jpg?raw=true)
![](https://github.com/luoxiong94/adapter-databinding/blob/master/pic/子项为Grid的列表.jpg?raw=true)![](https://github.com/luoxiong94/adapter-databinding/blob/master/pic/子项为Grid的列表(各组子项的Span不同).jpg?raw=true)
## 1、普通列表使用方式
### 单一类型
在model中定义
```java
val adapter = BindingRecyclerViewAdapter<SingleItemVo>()

val simpleItemBinding = itemBindingOf<SingleItemVo>(R.layout.item_single)

val singleItems = ObservableArrayList<SingleItemVo>().apply {
        for (i in 0 until 3) {
            add(SingleItemVo(i))
        }
    }
```
在xml中引用
```xml
   <androidx.recyclerview.widget.RecyclerView
            app:rv_adapter="@{viewModel.adapter}"
            app:rv_itemBinding="@{viewModel.simpleItemBinding}"
            app:rv_items="@{viewModel.singleItems}"
            app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager" />
```
在Fragment中设置
```java
class FragmentSingleRecyclerView : Fragment() {
    override fun onCreateView(  ): View? {
        return FragmentSingleRecyclerviewBinding.inflate(inflater, container, false).apply {
            setViewModel(viewModel)
        }.root
    }
}
```
### 带头部和脚部类型
在model中定义
```java
val multiAdapter = BindingRecyclerViewAdapter<Any>()

// 定义布局的方式1
val headerFooterItemBinding = itemBindingOf<Any>(object : OnItemBind<Any> {
        override fun onItemBind(itemBinding: XmlItemBinding<*>, position: Int, item: Any) {
            when (item::class) {
                HeaderVo::class -> itemBinding.set(R.layout.item_header)
                SingleItemVo::class -> itemBinding.set(R.layout.item_single, itemClickEvent)
                FooterVo::class -> itemBinding.set(R.layout.item_footer)
            }
        }
    })
// 定义布局的方式2
 val headerFooterItemBinding = OnItemBindClass<Any>().apply {
        map<HeaderVo>(R.layout.item_header, itemClickEvent)
        map<SingleItemVo>(R.layout.item_single)
        map<FooterVo>(R.layout.item_footer)
    }

val headerFooterItems = MergeObservableList<Any>()
        .insertItem(HeaderVo("Header"))
        .insertList(singleItems)
        .insertItem(FooterVo("Footer11"))
        .insertItem(FooterVo("Footer22"))
```
在xml中引用
```xml
   <androidx.recyclerview.widget.RecyclerView
            app:rv_adapter="@{viewModel.multiAdapter}"
            app:rv_itemBinding="@{viewModel.headerFooterItemBinding}"
            app:rv_items="@{viewModel.headerFooterItems}"
            app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager" />
```
### 多种item类型
在model中定义
```java
val multiAdapter = BindingRecyclerViewAdapter<Any>()

val multiItemBinding = OnItemBindClass<Any>().apply {
        map<SingleItemVo>(R.layout.item_single, itemClickEvent)
        map<Type1Vo>(R.layout.item_type_1)
        map<Type2Vo>(R.layout.item_type_2, BR.item)
    }

  val multiItems = MergeObservableList<Any>()
        .insertItem(Type1Vo("type1-0"))
        .insertItem(Type2Vo("type2-0"))
        .insertList(singleItems)
        .insertItem(Type1Vo("type1-1"))
        .insertItem(Type2Vo("type2-1"))
```
在xml中引用
```xml
   <androidx.recyclerview.widget.RecyclerView
            app:rv_adapter="@{viewModel.multiAdapter}"
            app:rv_itemBinding="@{viewModel.multiItemBinding}"
            app:rv_items="@{viewModel.multiItems}"
            app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager" />
```
# 注意: 数据源items 属于ObservableList, 所以更新items的时候, 列表会自动刷新UI,  你想要自己手动调用adapter.notify()刷新, items对象使用ArrayList即可

## UML类图
![](https://github.com/luoxiong94/adapter-databinding/blob/master/pic/System.png?raw=true)
## XmlItemBinding (单个xml布局对象)
- 每个item对应的布局id
- 每个item的数据对应的BR变量id, 默认是BR.item
- 每个item布局对应1个XmlItemBinding对象

## OnItemBind (接口)
- Adapter执行getItemViewType()方法, 会回调该onItemBind() 函数,用来设置每个Item的布局id和变量id.
- 每个item布局对应1个OnItemBind对象

## OnItemBindClass (管理多类型item的对象)
- itemBindingClassList:  每种item对应数据对象的class文件
- itemBindingList: 每种item对应的 OnItemBind 对象

## BindingRecyclerViewAdapter 核心适配器
- 根据每种item的布局来实现多类型列表, 简单方便
- 监听items数据源的变化,自动调用notifyDataChanged, 让开发者只专注于业务
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
##  该库的实现方案是:
数据源大小和列表Iitem数量一一对应,,统一管理数据,,Item的每种布局对应一种类型, 不用手动管理ViewType
```
   override fun getItemCount(): Int {
        return items.size
    }
   override fun getItemViewType(position: Int): Int {
        return xmlItemBinding.getLayoutRes()
    }
```

## 2、加载更多使用方式

在model中定义
```java
val adapter = BindingRecyclerViewAdapter<SingleItemVo>()

val simpleItemBinding = itemBindingOf<SingleItemVo>(R.layout.item_single)

val singleItems = ObservableArrayList<SingleItemVo>().apply {
        for (i in 0 until 3) {
            add(SingleItemVo(i))
        }
    }
var isNoMoreData = ObservableBoolean()  // 加载更多后,没有更多数据的标识
var isLoadMoreFailOb = ObservableBoolean() // 加载更多是否请求失败的标识
val loadMoreListener = object : LoadMoreAdapter.LoadMoreListener {  // 加载更多监听
        override fun onLoadingMore() {
            // 模拟数据
            Handler().postDelayed({
                for (i in 0 until 3) {
                    singleItems.add(SingleItemVo(i))
                }
            }, 500)
        }

       // 根据此标识决定是否显示没有更多数据
        override fun isShowNoMoreDataOb(): ObservableBoolean {
            return isNoMoreData
        }

       // 根据此标识决定是否显示加载失败的布局,点击失败布局,默认触发loadingMore回调
        override fun isShowLoadMoreFailOb(): ObservableBoolean {
            return isLoadMoreFailOb
        }
    }
```
在xml中引用
```xml
   <androidx.recyclerview.widget.RecyclerView
            app:rv_adapter="@{viewModel.adapter}"
            app:rv_itemBinding="@{viewModel.simpleItemBinding}"
            app:rv_items="@{viewModel.singleItems}"
            app:rv_loadmore_listener="@{viewModel.loadMoreListener}"
            app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager" />
```
支持自定义底部加载布局,使用方式
```java
 LoadMoreAdapter.DEFAULT_FOOTER_PATH="me.lx.CustomLoadMoreFooter"
```
```java
class CustomLoadMoreFooter : AbstractLoadMoreFooter() {
    private lateinit var mTvText: TextView

    override fun getLayoutRes(): Int {
        return R.layout.load_more_custom_layout
    }

    override fun onCreate(footerView: View) {
        mTvText = footerView.findViewById(R.id.tvText)
    }

    override fun loading() {
        mTvText.text = "正在加载中…"
    }

    override fun noMoreData() {
        mTvText.text = "没有更多数据了"
    }

    override fun loadFailed() {
        mTvText.text = "加载失败,点击重试"
    }
}
```

## 3、分组列表
在xml中引用
```xml
 <androidx.recyclerview.widget.RecyclerView
            app:rv_group_adapter="@{viewModel.groupAdapter}"
            app:rv_group_items="@{viewModel.groupList}"
            app:rv_group_chick_child_listener="@{viewModel.clickChildEvent}"
            app:rv_group_chick_header_listener="@{viewModel.clickHeaderEvent}"
            app:rv_group_chick_footer_listener="@{viewModel.clickFooterEvent}"
            app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager" />
```
在model中定义
```java
val groupAdapter = GroupedListAdapter()

val groupList = ObservableArrayList<YourBean>()

val clickChildEvent = object : BaseFun1ClickGroupListener<ChildEntity>() {
        override fun clickGroupItem(item: ChildEntity) {
            Toast.makeText(MyApp.sContext, item.child, Toast.LENGTH_LONG).show()
        }
}
```
这样就可以实现1个分组的列表了, , 注意: 数据源使用ObservableArrayList ,全程不需要你手动刷新adapter ,
你更改数据源, 列表会自动刷新.

## 对于使用上有任何疑问或优化建议等，欢迎加入QQ群讨论交流技术问题。
交流群： 597802495
个人QQ: 382060748







