package me.lx.sample.group.model


import androidx.databinding.ObservableArrayList
import me.lx.sample.group.entity.*

/**
 * Depiction:
 * Author: teach
 * Date: 2017/3/20 15:51
 */
class GroupModel {
    companion object {
        /**
         * 获取组列表数据
         *
         * @param groupCount    组数量
         * @param childrenCount 每个组里的子项数量
         * @return
         */
        fun getTwoGroupGroupOb(groupCount: Int, childrenCount: Int = 2): ObservableArrayList<TwoLevelGroupEntity> {
            var a1: TwoLevelChildEntity? = null
            var a2: ChildChildEntity? = null
            val groups = ObservableArrayList<TwoLevelGroupEntity>()
            for (groupIndex in 0 until groupCount) {
                val childList = ObservableArrayList<TwoLevelChildEntity>()
                for (childIndex in 0 until childrenCount) {
                    childList.add(getTwoLevelChildEntity(groupIndex,childIndex,  childrenCount))
                }
                groups.add(getTwoLevelGroupEntity(groupIndex, childList))
            }
            return groups
        }

        fun getTwoLevelGroupEntity(groupIndex: Int, childList: ObservableArrayList<TwoLevelChildEntity>): TwoLevelGroupEntity {
            val group = TwoLevelGroupEntity()
            group.headerText = "第" + (groupIndex + 1) + "组头部"
            group.footerText = "第" + (groupIndex + 1) + "组尾部"
            group.childGroupList.addAll(childList)
            return group

        }

        fun getTwoLevelChildEntity( groupIndex: Int, childIndex: Int,childCount: Int): TwoLevelChildEntity {
            var childrenCount = childCount
            if (groupIndex % 2 != 0) {
                childrenCount++
            }
            val twoLevelChild = TwoLevelChildEntity()
            twoLevelChild.childText = "第" + (groupIndex + 1) + "组" + "第" + (childIndex + 1) + "个Child"
            for (i in 0 until childrenCount) {
                val childChild = ChildChildEntity()
                childChild.childChildText = "第" + (groupIndex + 1) + "组" + "第" + (childIndex + 1) + "个Child" + "第" + (i + 1) + "项"
                twoLevelChild.childChildList.add(childChild)

            }
            return twoLevelChild
        }

        /**
         * 获取组列表数据
         *
         * @param groupCount    组数量
         * @param childrenCount 每个组里的子项数量
         * @return
         */
        fun getGroupOb(groupCount: Int, childrenCount: Int): ObservableArrayList<GroupEntity> {
            val groups = ObservableArrayList<GroupEntity>()
            for (i in 0 until groupCount) {
                val childList = ObservableArrayList<ChildEntity>()
                for (j in 0 until childrenCount) {
                    childList.add(getChildEntity(i, j))
                }
                groups.add(
                    getGroupEntity(i, childList)
                )
            }
            return groups
        }

        /**
         * 获取组列表数据
         *
         * @param groupCount    组数量
         * @param childrenCount 每个组里的子项数量
         * @return
         */
        fun getGroups(groupCount: Int, childrenCount: Int): ObservableArrayList<GroupEntity> {
            val groups = ObservableArrayList<GroupEntity>()
            for (i in 0 until groupCount) {
                val childList = ObservableArrayList<ChildEntity>()
                for (j in 0 until childrenCount) {
                    childList.add(getChildEntity(i, j))
                }
                groups.add(getGroupEntity(i, childList))


            }
            return groups
        }

        fun getGroupEntity(groupIndex: Int, childList: ObservableArrayList<ChildEntity>): GroupEntity {
            return GroupEntity(
                "第" + (groupIndex + 1) + "组头部",
                "第" + (groupIndex + 1) + "组尾部", childList
            )
        }

        /**
         * 获取可展开收起的组列表数据(默认展开)
         *
         * @param groupCount    组数量
         * @param childrenCount 每个组里的子项数量
         * @return
         */
        fun getExpandableGroups(groupCount: Int, childrenCount: Int): ObservableArrayList<ExpandableGroupEntity> {
            val groups = ObservableArrayList<ExpandableGroupEntity>()
            for (i in 0 until groupCount) {
                val childList = ObservableArrayList<ChildEntity>()
                for (j in 0 until childrenCount) {
                    childList.add(getChildEntity(i, j))
                }
                groups.add(
                    ExpandableGroupEntity(
                        "第" + (i + 1) + "组头部",
                        "第" + (i + 1) + "组尾部", true, childList
                    )
                )
            }
            return groups
        }

        fun getChildEntity(groupIndex: Int, groupChildIndex: Int) =
            ChildEntity("第" + (groupIndex + 1) + "组第" + (groupChildIndex + 1) + "项")
    }

}
