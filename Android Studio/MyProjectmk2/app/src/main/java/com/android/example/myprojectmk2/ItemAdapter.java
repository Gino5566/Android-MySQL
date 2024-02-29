package com.android.example.myprojectmk2;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

public class ItemAdapter extends ArrayAdapter<ClassItem> {
    private Context context;

    public ItemAdapter(@NonNull Context context, int resource,@NonNull List<ClassItem> objects) {
        super(context, resource,objects);
        this.context=context;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ClassItem classItem=getItem(position);//取得ClassItem
        //設置UI
        View view=LayoutInflater.from(getContext()).inflate(R.layout.listview_item,parent,false);

        ImageView itemImage =view.findViewById(R.id.image);
        TextView itemName =view.findViewById(R.id.name);

        ImageRequest imageRequest = new ImageRequest(classItem.getImageID(),
                response -> {
                    itemImage.setImageBitmap(response);
                },
                0,
                0,
                ImageView.ScaleType.CENTER_INSIDE,
                null,
                error -> {
                    //發生錯誤時的處理
                    Log.e("Volley", "Error loading image: " + error.getMessage());
                });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(imageRequest);

        itemName.setText(classItem.getName());
        return view;
    }
}
