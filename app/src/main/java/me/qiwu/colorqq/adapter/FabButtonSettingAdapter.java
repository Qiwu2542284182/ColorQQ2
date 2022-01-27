package me.qiwu.colorqq.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import me.qiwu.colorqq.R;
import me.qiwu.colorqq.bean.FabInfo;
import me.qiwu.colorqq.util.FileUtil;

public class FabButtonSettingAdapter extends BaseItemDraggableAdapter {

    public FabButtonSettingAdapter(int layoutResId, List<FabInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Object item) {
        FabInfo fabInfo = (FabInfo)item;
        helper.setText(R.id.list_title,fabInfo.title);
        helper.setImageBitmap(R.id.list_img, FileUtil.getFabIcon(fabInfo.icon));
    }

}
