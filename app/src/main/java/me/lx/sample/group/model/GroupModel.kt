package me.lx.sample.group.model


import androidx.databinding.ObservableArrayList
import me.lx.sample.group.entity.*
import me.lx.sample.group.entity.TwoLevelGroupEntity
import me.lx.sample.group.entity.TwoLevelGroupEntity.CgEntity
import me.lx.sample.group.entity.TwoLevelGroupEntity.CgEntity.CcEntity

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
            var a1: CgEntity? = null
            var a2: CcEntity? = null
            val groups = ObservableArrayList<TwoLevelGroupEntity>()
            for (groupPosition in 0 until groupCount) {
                val childList = ObservableArrayList<CgEntity>()
                for (childIndex in 0 until childrenCount) {
                    childList.add(getChildGroupEntity(groupPosition, childIndex, childrenCount))
                }
                groups.add(getTwoLevelGroupEntity(groupPosition, childList))
            }
            return groups
        }

        fun getTwoLevelGroupEntity(groupPosition: Int, childList: ObservableArrayList<CgEntity>): TwoLevelGroupEntity {
            val group = TwoLevelGroupEntity()
            group.headerText = "第" + (groupPosition + 1) + "组头部"
            group.footerText = "第" + (groupPosition + 1) + "组尾部"
            group.childGroupList.addAll(childList)
            return group

        }

        fun getChildGroupEntity(groupPosition: Int, childIndex: Int, childCount: Int): CgEntity {
            var childrenCount = childCount
            if (groupPosition % 2 != 0) {
                childrenCount++
            }
            val cg = CgEntity()
            cg.cgIndexInGroup = childIndex
            cg.cgHeaderText = "第" + (groupPosition + 1) + "组" + "第" + (childIndex + 1) + "个Header"
            cg.cgFooterText = "第" + (groupPosition + 1) + "组" + "第" + (childIndex + 1) + "个Footer"
            for (i in 0 until childrenCount) {
                cg.childChildList.add(createCc(groupPosition, childIndex, i))
            }
            return cg
        }

        fun createCc(groupPosition: Int, cgIndexInGroup: Int, ccIndexInCg: Int): CcEntity {
            val cc = CcEntity()
            cc.childChildText = "第" + (groupPosition + 1) + "组" + "第" + (cgIndexInGroup + 1) + "个Header" + "第" + (ccIndexInCg + 1) + "项"
            return cc
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
        fun getGroupList(groupCount: Int, childrenCount: Int): ObservableArrayList<GroupEntity> {
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

        fun getGroupEntity(groupPosition: Int, childList: ObservableArrayList<ChildEntity>): GroupEntity {
            return GroupEntity(
                "第" + (groupPosition + 1) + "组头部",
                "第" + (groupPosition + 1) + "组尾部", childList
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

        fun getChildEntity(groupPosition: Int, groupChildIndex: Int) =
            ChildEntity("第" + (groupPosition + 1) + "组第" + (groupChildIndex + 1) + "项")
    }

}
