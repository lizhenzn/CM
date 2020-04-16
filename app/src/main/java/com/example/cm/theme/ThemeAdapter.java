package com.example.cm.theme;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cm.R;
import com.example.cm.util.MessageManager;

import java.util.List;

public class ThemeAdapter extends BaseAdapter {
    private List<ThemeColor> list;
    private Context context;
    private Toolbar toolbar;
    public ThemeAdapter(Context context,Toolbar toolbar,List<ThemeColor> list){
        this.context=context;
        this.toolbar=toolbar;
        this.list=list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.theme_item, null);
            viewHolder.colorLL=view.findViewById(R.id.theme_LL);
            viewHolder.colorTV = (TextView) view.findViewById(R.id.theme_item_tv);
            viewHolder.colorIV=(ImageView)view.findViewById(R.id.theme_item_iv);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.colorTV.setText(this.list.get(position).getColorName());
        viewHolder.colorIV.setBackgroundColor(Color.parseColor(list.get(position).getColorStr()));
        viewHolder.colorLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String backStr=list.get(position).getColorStr();
                MessageManager.getEditor().putString("currentBackStr",backStr);
                MessageManager.getEditor().commit();//提交
                ThemeColor.backColorStr=backStr;
                ThemeColor.changed=true;
                ThemeColor.setTheme((Activity) context,toolbar);
            }
        });

        return view;

    }
    class ViewHolder{
        LinearLayout colorLL;
        ImageView colorIV;
        TextView colorTV;
    }
}
