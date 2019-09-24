package me.lx.sample.group.adapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import me.lx.rv.group.BaseViewHolder;
import me.lx.rv.group.GroupedRecyclerViewAdapter;
import me.lx.sample.R;
import me.lx.sample.group.entity.ChildEntity;
import me.lx.sample.group.entity.GroupEntity;

/**
 * 这是普通的分组Adapter 每一个组都有头部、尾部和子项。
 */
public class GroupedListAdapter extends GroupedRecyclerViewAdapter<GroupEntity,ChildEntity> {

//    public GroupedListAdapter(@NotNull List<GroupEntity> dataList) {
//        super(dataList);
//    }

    @NotNull
    @Override
    public List<ChildEntity> getChildrenList(GroupEntity entity) {
        return entity.getChildren();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<ChildEntity> children = getItems().get(groupPosition).getChildren();
        return children.size();
    }

    @Override
    public boolean hasHeader(int groupPosition) {
        return getItems().get(groupPosition).getChildren().size() > 0;
    }

    @Override
    public boolean hasFooter(int groupPosition) {
        return getItems().get(groupPosition).getChildren().size()>0;
    }

    @Override
    public int getHeaderLayout(int viewType) {
        return R.layout.adapter_header;
    }

    @Override
    public int getFooterLayout(int viewType) {
        return R.layout.adapter_footer;
    }

    @Override
    public int getChildLayout(int viewType) {
        return R.layout.adapter_child;
    }

    @Override
    public void onBindHeaderViewHolder(BaseViewHolder holder, int groupPosition) {
        GroupEntity entity = getItems().get(groupPosition);
       // System.out.println("onBindHeaderViewHolder()....22222...groupPosition="+groupPosition+"  text="+entity.getHeader());
        holder.setText(R.id.tv_header, entity.getHeader());
    }

    @Override
    public void onBindFooterViewHolder(BaseViewHolder holder, int groupPosition) {
        GroupEntity entity = getItems().get(groupPosition);
        holder.setText(R.id.tv_footer, entity.getFooter());
    }

    @Override
    public void onBindChildViewHolder(BaseViewHolder holder, int groupPosition, int childPosition) {
        ChildEntity entity = getItems().get(groupPosition).getChildren().get(childPosition);
        holder.setText(R.id.tv_child, entity.getChild());
    }


}
