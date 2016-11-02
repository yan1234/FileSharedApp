package com.example.filesharedapp.app.fragment.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.filesharedapp.R;
import com.example.filesharedapp.framework.cache.ImageLoaderManager;
import com.example.filesharedapp.framework.media.MediaManager;
import com.example.filesharedapp.framework.media.entity.BaseFileInfo;
import com.example.filesharedapp.framework.media.entity.PhotoInfo;
import com.example.filesharedapp.framework.ui.base.BasicAdapter;
import com.example.filesharedapp.framework.ui.gridview.PhotoGridView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;


import java.util.ArrayList;
import java.util.List;

/**
 * 图片列表适配器
 * Created by yanling on 2015/10/31.
 */
public class PhotoAdapter extends BasicAdapter {

    //上下文
    private Context mContext;
    //图片信息列表
    private List<PhotoInfo> photos;
    //按目录整理图片
    private List<ListItem> listItems = new ArrayList<ListItem>();

    /**
     *
     * @param context,上下文
     * @param list， 图片详情列表
     * @param bucketNames，图片目录列表
     */
    public PhotoAdapter(Context context, List<PhotoInfo> list, List<String> bucketNames, ArrayList<BaseFileInfo> selectList){
        this.mContext = context;
        this.photos = list;
        this.selectList = selectList;
        //根据目录名拆分图片列表
        int k=0;
        for (int i=0; i<bucketNames.size(); i++){
            //定义每一个item实体
            ListItem item = new ListItem();
            for (; k<photos.size(); k++){
                //判断当前的目录名是否一致
                if (bucketNames.get(i).equals(photos.get(k).getBucketName())){
                    item.list.add(photos.get(k));
                }else{
                    //跳出循环，下一次从当前位置接着遍历
                    break;
                }
            }
            //添加到List中
            item.bucketName = bucketNames.get(i);
            listItems.add(item);
        }
    }

    @Override
    public int getCount() {
        return listItems == null ? 0:listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listItems == null ? null:listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null){
            viewHolder = new ViewHolder();
            //载入布局
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_photo_list, null);
            viewHolder.title = (TextView)convertView.findViewById(R.id.item_photo_list_title);
            viewHolder.gridView = (PhotoGridView)convertView.findViewById(R.id.item_photo_list_gridview);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        //载入标题
        viewHolder.title.setText(listItems.get(position).bucketName);
        //载入图片选项的adapter
        ItemAdapter adapter = new ItemAdapter(listItems.get(position).list);
        viewHolder.gridView.setAdapter(adapter);
        //给gridView添加item长按事件
        viewHolder.gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int index, long id) {
                //判断该项是否已经包含在选中的列表中
                if (selectList.contains(listItems.get(position).list.get(index))){
                    //移除该项
                    selectList.remove(listItems.get(position).list.get(index));
                    //设置背景为还原
                    view.setBackgroundColor(mContext.getResources().getColor(R.color.long_click_cancel));
                }else{
                    //添加该项
                    selectList.add(listItems.get(position).list.get(index));
                    //设置背景
                    view.setBackgroundColor(mContext.getResources().getColor(R.color.long_click_selected));
                }
                return true;
            }
        });
        return convertView;
    }

    private class ViewHolder{
        public TextView title;
        public PhotoGridView gridView;
    }

    private class ItemAdapter extends BaseAdapter{

        //定义item数据
        public List<PhotoInfo> items;
        //定义item布局的宽度
        private int layout_width = 0;

        public ItemAdapter(List<PhotoInfo> list){
            this.items = list;
            //计算各个空间的宽度
            getMeasureWidth(mContext.getResources().getDisplayMetrics().widthPixels);
        }

        /**
         * 计算各个控件的尺寸
         */
        private void getMeasureWidth(int screenWidth){
            /*//这里判断下手机的屏幕是否超过1080dp
            //默认是0.8
            float weight = 0.8f;
            if (screenWidth >= 1080){
                weight = 0.6f;
            }*/
            //每一个item等分成4份，然后减去两边的padding，得到布局的宽度
            layout_width = screenWidth / 4 - 2 * 5;

        }

        @Override
        public int getCount() {
            return items == null ? 0:items.size();
        }

        @Override
        public Object getItem(int position) {
            return items == null ? null:items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            VHolder vHolder = null;
            if (convertView == null){
                vHolder = new VHolder();
                //载入图片item布局
                convertView = LayoutInflater.from(mContext)
                        .inflate(R.layout.item_photo_list_gridview, null);
                vHolder.layout = (LinearLayout)convertView.findViewById(R.id.item_photo_list_gridview_layout);
                vHolder.image = (ImageView)convertView.findViewById(R.id.item_photo_list_gridview_image);
                convertView.setTag(vHolder);
            }else{
                vHolder = (VHolder)convertView.getTag();
            }
            //显示图片
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(layout_width, layout_width);
            vHolder.layout.setLayoutParams(params);
            //根据id查询到对应的图片
            //Bitmap bitmap = MediaManager.getInstance(mContext).getImages(items.get(position).getId());
            //vHolder.image.setImageBitmap(bitmap);
            /*ImageLoader.getInstance().displayImage(
                    "file://" + items.get(position).getPath(),
                    vHolder.image,
                    ImageLoaderManager.getOption(mContext));*/
            final ImageView img = vHolder.image;
            //通过ImageLoader缓存图片进行加载
            ImageLoader.getInstance().loadImage(
                    "file://" + items.get(position).getPath(),
                    ImageLoaderManager.getOption(mContext),
                    new SimpleImageLoadingListener(){
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);
                            //加载图片
                            img.setImageBitmap(loadedImage);
                        }
                    });
            //判断当前item是否被选中
            if (selectList.contains(items.get(position))){
                //设置当前item背景
                vHolder.layout.setBackgroundColor(mContext.getResources().getColor(R.color.long_click_selected));
            }
            return convertView;
        }

        private class VHolder{
            //背景布局
            public LinearLayout layout;
            //图片
            public ImageView image;
        }
    }

    /**
     * 对应于每一个目录的图片列表
     */
    private class ListItem{
        //目录名
        public String bucketName;
        //该目录下的图片集合
        public List<PhotoInfo> list = new ArrayList<PhotoInfo>();
    }
}
