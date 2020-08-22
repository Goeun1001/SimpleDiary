package com.example.admin.androidsqlite.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.admin.androidsqlite.R;
import com.example.admin.androidsqlite.model.Memo;

import java.util.List;

/**
 * Created by admin on 19-04-2018.
 */

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.MyViewHolder>{


    private Context context;
    private List<Memo> memoList;

    public MemoAdapter(Context context, List<Memo> memoList) {
        this.context = context;
        this.memoList = memoList;
    }

    public void removeItem(int adapterPosition) {
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView textmemo;
        public TextView textdate;
        public ImageView imageView;
        public RelativeLayout  viewForeground;


        public MyViewHolder(View itemView) {
            super(itemView);

            imageView=itemView.findViewById(R.id.imageView);
            textmemo=itemView.findViewById(R.id.textmemo);
            textdate=itemView.findViewById(R.id.textdate);
            viewForeground=itemView.findViewById(R.id.view_foreground);
        }
    }




    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());

        View itemView = inflater
                .inflate(R.layout.memo_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

       Memo memo=memoList.get(position);
       holder.textdate.setText(memo.getDate());
       holder.textmemo.setText(memo.getEvent());
       Drawable drawable = context.getResources().getDrawable(R.drawable.smile);
       holder.imageView.setImageDrawable(drawable);


    }


    @Override
    public int getItemCount() {
        return memoList.size();
    }
}
