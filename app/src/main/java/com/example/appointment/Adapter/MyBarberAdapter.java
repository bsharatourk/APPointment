package com.example.appointment.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appointment.Common.Common;
import com.example.appointment.Interface.IRecyckerItemSelectedListener;
import com.example.appointment.Model.Barber;
import com.example.appointment.R;

import java.util.ArrayList;
import java.util.List;

public class MyBarberAdapter extends RecyclerView.Adapter<MyBarberAdapter.MyViewHolder> {

    Context context;
    List<Barber> barberList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public MyBarberAdapter(Context context, List<Barber> barberList) {
        this.context = context;
        this.barberList = barberList;
        cardViewList = new ArrayList<>();
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_barber,viewGroup,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.txt_barber_name.setText(barberList.get(i).getName());
        myViewHolder.ratingBar.setRating((float)barberList.get(i).getRating());
        if (!cardViewList.contains(myViewHolder.card_barber)){
            cardViewList.add(myViewHolder.card_barber);
        }
        myViewHolder.setiRecyckerItemSelectedListener(new IRecyckerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int pos) {
                for (CardView cardView:cardViewList){
                    cardView.setCardBackgroundColor(context.getResources()
                            .getColor(android.R.color.white));
                }

                Common.currentBarber =MyBarberAdapter.this.barberList.get(i);
                //Set background for choice
                myViewHolder.card_barber.setCardBackgroundColor(
                        context.getResources()
                        .getColor(android.R.color.holo_orange_dark)
                );
                //Sending local broadcast to enable button next
                Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_BARBER_SELECTED,barberList.get(pos));
                intent.putExtra(Common.KEY_STEP,1);
                localBroadcastManager.sendBroadcast(intent);


            }
        });
    }

    @Override
    public int getItemCount() {
        return barberList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_barber_name;
        RatingBar ratingBar;
        CardView card_barber;

        IRecyckerItemSelectedListener iRecyckerItemSelectedListener;

         public void setiRecyckerItemSelectedListener(IRecyckerItemSelectedListener iRecyckerItemSelectedListener) {
            this.iRecyckerItemSelectedListener = iRecyckerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            card_barber = (CardView)itemView.findViewById(R.id.card_barber);
            txt_barber_name = (TextView)itemView.findViewById(R.id.txt_barber_name);
            ratingBar = (RatingBar)itemView.findViewById(R.id.rtb_barber);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyckerItemSelectedListener.onItemSelectedListener(v,getAdapterPosition());
        }
    }
}
