package com.hdfc.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hdfc.caretaker.DependentDetailPersonal;
import com.hdfc.caretaker.R;
import com.hdfc.models.DependentModel;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Admin on 27-06-2016.
 */
public class DependentListViewAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private Context contxt;
    private ArrayList data;

    public DependentListViewAdapter(Context ctxt, ArrayList d) {
        contxt = ctxt;
        data = d;
        inflater = (LayoutInflater) contxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        DependentModel editDependentModel = (DependentModel) data.get(position);

        if (convertView == null) {
            vi = inflater.inflate(R.layout.dependent_list_item, null);

            holder = new ViewHolder();
            holder.linearlayoutview = (LinearLayout) vi.findViewById(R.id.linearlayoutClient);
            holder.textName = (TextView) vi.findViewById(R.id.txtViewName);
            holder.textRelation = (TextView) vi.findViewById(R.id.txtViewRelation);
            holder.image = (ImageView) vi.findViewById(R.id.imgdependent);
            holder.address = (TextView) vi.findViewById(R.id.txtViewAdd);
            holder.age = (TextView) vi.findViewById(R.id.txtViewClient_age);

            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        String strName = editDependentModel.getStrName();

        if (strName != null && strName.length() > 20)
            strName = editDependentModel.getStrName().substring(0, 18) + "..";

        String strAddress = editDependentModel.getStrAddress();

        if (strAddress != null && strAddress.length() > 20)
            strAddress = editDependentModel.getStrAddress().substring(0, 18) + "..";

        holder.textName.setText(strName);
        holder.address.setText(strAddress);
        holder.age.setText(editDependentModel.getStrAge());
        holder.textRelation.setText(editDependentModel.getStrRelation());

        try {


            Glide.with(contxt)
                    .load(editDependentModel.getStrImageUrl())
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(contxt))
                    .placeholder(R.drawable.person_icon)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.image);

        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.linearlayoutview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putBoolean("editflag", true);
                bundle.putInt("childposition", position);
                Intent intent = new Intent(contxt, DependentDetailPersonal.class);
                intent.putExtras(bundle);
                contxt.startActivity(intent);
            }
        });

        return vi;
    }

    public static class ViewHolder {
        public ImageView image;
        public TextView address;
        public TextView age;
        TextView textName;
        TextView textRelation;
        LinearLayout linearlayoutview;
    }
}
