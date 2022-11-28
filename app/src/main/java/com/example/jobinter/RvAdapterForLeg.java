package com.example.jobinter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.util.ArrayList;

class RvAdapterForLeg extends RecyclerView.Adapter<RvAdapterForLeg.myViewHolder>{
    private final RvInterface rvInterface;
    Context context;
    ArrayList<String> data;
    public RvAdapterForLeg(RvInterface rvInterface, ArrayList<String> data, Context context)
    {
        this.rvInterface = rvInterface;
        this.context=context;

        this.data=data;
    }
    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_item_for_leg,parent,false);
        return new myViewHolder(view,rvInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int pos) {
        //holder.bt.setBackgroundColor(Color.parseColor(data.get(pos).split(":")[0]));
        holder.txt.setText(data.get(pos).split(":")[1]);
        holder.bt.setBackgroundColor(Color.parseColor(data.get(pos).split(":")[0]));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public  static class myViewHolder extends RecyclerView.ViewHolder{
        Button bt;
        TextView txt;
        public  myViewHolder(@NonNull View itemView,RvInterface rvInterface) {
            super(itemView);
            bt= itemView.findViewById(R.id.bt1);
            txt= itemView.findViewById(R.id.text_color);


        }
    }
}
