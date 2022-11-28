package com.example.jobinter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.util.ArrayList;

class RvAdapter extends RecyclerView.Adapter<RvAdapter.myViewHolder>{
    private final RvInterface rvInterface;
    Context context;
    ArrayList<String> data;
    public RvAdapter(RvInterface rvInterface, ArrayList<String> data,Context context)
    {
        this.rvInterface = rvInterface;
        this.context=context;

        this.data=data;
    }
    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_item,parent,false);
        return new myViewHolder(view,rvInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int pos) {
        holder.bt.setText(data.get(pos));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public  static class myViewHolder extends RecyclerView.ViewHolder{
        Button bt;
        public  myViewHolder(@NonNull View itemView,RvInterface rvInterface) {
            super(itemView);
            bt= itemView.findViewById(R.id.bt1);
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(rvInterface!=null)
                    {
                        int pos =getAdapterPosition();
                        if(pos!= RecyclerView.NO_POSITION)
                        {
                            try {
                                rvInterface.onItemSelected(pos);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });

        }
    }
}
