package me.lx.sample.group.entity;

import androidx.databinding.ObservableArrayList;

/**
 * 组数据的实体类
 */
public class GroupEntity {

    private String header;
    private String footer;
    private ObservableArrayList<ChildEntity> children;

    public GroupEntity(String header, String footer, ObservableArrayList<ChildEntity> children) {
        this.header = header;
        this.footer = footer;
        this.children = children;
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

    public ObservableArrayList<ChildEntity> getChildren() {
        return children;
    }

    public void setChildren(ObservableArrayList<ChildEntity> children) {
        this.children = children;
    }
}
