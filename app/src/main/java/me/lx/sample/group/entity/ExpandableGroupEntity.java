package me.lx.sample.group.entity;

import java.util.ArrayList;

/**
 * 可展开收起的组数据的实体类 它比GroupEntity只是多了一个boolean类型的isExpand，用来表示展开和收起的状态。
 */
public class ExpandableGroupEntity {

    private String header;
    private String footer;
    private ArrayList<ChildEntity> childList;
    private boolean isExpand;

    public ExpandableGroupEntity(String header, String footer, boolean isExpand,
                                 ArrayList<ChildEntity> childList) {
        this.header = header;
        this.footer = footer;
        this.isExpand = isExpand;
        this.childList = childList;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    public ArrayList<ChildEntity> getChildList() {
        return childList;
    }

    public void setChildren(ArrayList<ChildEntity> childList) {
        this.childList = childList;
    }
}
