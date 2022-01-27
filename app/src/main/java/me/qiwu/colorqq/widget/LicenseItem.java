package me.qiwu.colorqq.widget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

public class LicenseItem extends ArrowItem {
    public LicenseItem(Context context) {
        super(context);
        init();
    }

    public LicenseItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        if (!TextUtils.isEmpty(getSummary())){
            setSummaryMaxLine();
            String url = getSummary().toString();
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
            });
        }
    }
}
