package com.yanling.fileshared.app.transfers;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;

import com.yanling.fileshared.R;

/**
 * 传输类别选择弹框
 * @author yanling
 * @date 2017-01-06
 */
public class TransferSelectDialog extends Dialog{
    public TransferSelectDialog(Context context) {
        super(context);
    }

    public TransferSelectDialog(Context context, int theme) {
        super(context, theme);
    }

    protected TransferSelectDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder{

        private Context mContext;


        public Builder(Context context){
            this.mContext = context;
        }

        public TransferSelectDialog create() {
            LayoutInflater inflater = (LayoutInflater)mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final TransferSelectDialog dialog = new TransferSelectDialog(mContext);
            View layout = inflater.inflate(R.layout.fragment_other, null);
            dialog.addContentView(layout, new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.FILL_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
            dialog.setContentView(layout);
            return dialog;
        }

    }
}
