package com.slj.meet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

public class ExcelAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    Context context;
    List<List<String>> datas;
    List<Integer> checkes;

    public ExcelAdapter(Context context, List<List<String>> datas, List<Integer> checkes) {
        this.datas = datas;
        this.context = context;
        this.checkes = checkes;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        List<String> data = datas.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item, null);
            viewHolder.v1 = convertView.findViewById(R.id.tv1);
            viewHolder.v2 = convertView.findViewById(R.id.tv2);
            viewHolder.v3 = convertView.findViewById(R.id.tv3);
            viewHolder.v4 = convertView.findViewById(R.id.tv4);
            viewHolder.v5 = convertView.findViewById(R.id.tv5);
            viewHolder.v6 = convertView.findViewById(R.id.tv6);
            viewHolder.v7 = convertView.findViewById(R.id.tv7);
            viewHolder.v8 = convertView.findViewById(R.id.tv8);
            viewHolder.rg = convertView.findViewById(R.id.rg);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (position % 2 == 0) {
            viewHolder.v1.setBackgroundResource(R.drawable.border1);
            viewHolder.v2.setBackgroundResource(R.drawable.border1);
            viewHolder.v3.setBackgroundResource(R.drawable.border1);
            viewHolder.v4.setBackgroundResource(R.drawable.border1);
            viewHolder.v5.setBackgroundResource(R.drawable.border1);
            viewHolder.v6.setBackgroundResource(R.drawable.border1);
            viewHolder.v7.setBackgroundResource(R.drawable.border1);
            viewHolder.v8.setBackgroundResource(R.drawable.border1);
            viewHolder.rg.setBackgroundResource(R.drawable.border1);
        } else {
            viewHolder.v1.setBackgroundResource(R.drawable.border);
            viewHolder.v2.setBackgroundResource(R.drawable.border);
            viewHolder.v3.setBackgroundResource(R.drawable.border);
            viewHolder.v4.setBackgroundResource(R.drawable.border);
            viewHolder.v5.setBackgroundResource(R.drawable.border);
            viewHolder.v6.setBackgroundResource(R.drawable.border);
            viewHolder.v7.setBackgroundResource(R.drawable.border);
            viewHolder.v8.setBackgroundResource(R.drawable.border);
            viewHolder.rg.setBackgroundResource(R.drawable.border);
        }
        viewHolder.v1.setText(data.get(0));
        viewHolder.v2.setText(data.get(1));
        viewHolder.v3.setText(data.get(2));
        viewHolder.v4.setText(data.get(3));
        viewHolder.v5.setText(data.get(4));
        viewHolder.v6.setText(data.get(5));
        viewHolder.v7.setText(data.get(6));
        viewHolder.v8.setText(data.get(7));

        viewHolder.rg.setTag(position);

        viewHolder.rg.setTag(position);
        viewHolder.rg.check(checkes.get(position));
        viewHolder.rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkes.set((Integer) group.getTag(), checkedId);
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView v1;
        TextView v2;
        TextView v3;
        TextView v4;
        TextView v5;
        TextView v6;
        TextView v7;
        TextView v8;
        RadioGroup rg;
    }
}
