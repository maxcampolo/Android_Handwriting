package com.maxcampolo.fingerpaint;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
 
public class DrawingListAdapter extends BaseAdapter {
    private Context mContext;
    ArrayList<Bitmap> bmpArray = new ArrayList<Bitmap>();
 
    // Constructor
    public DrawingListAdapter(Context c, ArrayList<Bitmap> bitmaps){
        mContext = c;
        bmpArray = bitmaps;
    }
 
    @Override
    public int getCount() {
        return bmpArray.size();
    }
 
    @Override
    public Object getItem(int position) {
        return bmpArray.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return 0;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);
        imageView.setImageBitmap(bmpArray.get(position));
        imageView.setBackgroundColor(Color.GRAY);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
        return imageView;
    }
 
}
