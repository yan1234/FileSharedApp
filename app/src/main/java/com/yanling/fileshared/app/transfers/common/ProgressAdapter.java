package com.yanling.fileshared.app.transfers.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yanling.fileshared.R;
import com.yanling.android.view.progress.NumberProgressView;
import com.yanling.fileshared.framework.ui.icon.ResourceManager;

import java.util.List;

/**
 * 进度展示列表适配器
 * @author yanling
 */
public class ProgressAdapter extends BaseAdapter{


    //定义上下文
    private Context mContext;
    //定义进度消息列表
    private List<ProgressEntity> list;

    public ProgressAdapter(Context context, List<ProgressEntity> list){
        this.mContext = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list!=null?list.size():0;
    }

    @Override
    public Object getItem(int i) {
        return list==null ? null:list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_progress_list, null);
            viewHolder.icon = (ImageView)convertView.findViewById(R.id.item_progress_list_icon);
            viewHolder.title = (TextView)convertView.findViewById(R.id.item_progress_list_title);
            viewHolder.progressView = (NumberProgressView)convertView.findViewById(R.id.item_progress_list_progress);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        if (list.get(i).getIcon() != null){
            viewHolder.icon.setImageBitmap(list.get(i).getIcon());
        }else{
            //通过文件类型设置标题
            viewHolder.icon.setImageResource(ResourceManager.getIconType(mContext, list.get(i).getTitle()));
        }
        viewHolder.title.setText(list.get(i).getTitle());
        int progress = (int)(100 * list.get(i).getDownloadSize() / list.get(i).getTotalSize());
        viewHolder.progressView.setProgress(progress);
        return convertView;
    }

    class ViewHolder{
        public ImageView icon;
        public TextView title;
        public NumberProgressView progressView;
    }

    public List<ProgressEntity> getList() {
        return list;
    }

    public void setList(List<ProgressEntity> list) {
        this.list = list;
    }
}
