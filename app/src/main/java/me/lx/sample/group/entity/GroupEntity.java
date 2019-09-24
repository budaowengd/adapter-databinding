package me.lx.sample.group.entity;

import androidx.databinding.ObservableArrayList;

/**
 * 组数据的实体类
 */
public class GroupEntity {

    private String header;
    private String footer;
    private ObservableArrayList<ChildEntity> childList;

    public GroupEntity(String header, String footer, ObservableArrayList<ChildEntity> childList) {
        this.header = header;
        this.footer = footer;
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

    public ObservableArrayList<ChildEntity> getChildList() {
        return childList;
    }

    public void setChildren(ObservableArrayList<ChildEntity> childList) {
        this.childList = childList;
    }
}
