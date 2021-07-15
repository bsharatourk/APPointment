package com.example.appointment.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appointment.Common.Common;
import com.example.appointment.Model.TimeSlot;
import com.example.appointment.R;

import java.util.ArrayList;
import java.util.List;

public class MyTimeSlotAdapter extends RecyclerView.Adapter<MyTimeSlotAdapter.MyViewHolder> {

    Context context;
    List<TimeSlot> timeSlotsList;

    public MyTimeSlotAdapter(Context context) {
        this.context = context;
        this.timeSlotsList = new ArrayList<>();
    }

    public MyTimeSlotAdapter(Context context, List<TimeSlot> timeSlotsList) {
        this.context = context;
        this.timeSlotsList = timeSlotsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_time_slot,viewGroup,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.txt_time_slot.setText(new StringBuilder(Common.convertTimeSlotToString(i)).toString());
        // if all positions are available , show list
        if(timeSlotsList.size() == 0){
            myViewHolder.card_time_slot.setCardBackgroundColor(context.getResources()
                    .getColor(android.R.color.white));
            myViewHolder.txt_time_slot_description.setText("Available");
            myViewHolder.txt_time_slot_description.setTextColor(context.getResources()
            .getColor(android.R.color.black));
            myViewHolder.txt_time_slot.setTextColor(context.getResources()
            .getColor(android.R.color.black));
        }else //if all slots is full ...
        {
            for (TimeSlot slotValue:timeSlotsList) {
                // loop all time slot from server and set different color
                int slot = Integer.parseInt(slotValue.getSlot().toString());
                if(slot == i ){
                    myViewHolder.card_time_slot.setCardBackgroundColor(context.getResources()
                    .getColor(android.R.color.darker_gray));
                    myViewHolder.txt_time_slot_description.setText("Full");
                    myViewHolder.txt_time_slot_description.setTextColor(context.getResources()
                            .getColor(android.R.color.white));
                    myViewHolder.txt_time_slot.setTextColor(context.getResources()
                            .getColor(android.R.color.white));
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return Common.TIME_SLOT_TOTAL;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_time_slot,txt_time_slot_description;
        CardView card_time_slot;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            card_time_slot = (CardView)itemView.findViewById(R.id.card_time_slot);
            txt_time_slot = (TextView)itemView.findViewById(R.id.txt_time_slot);
            txt_time_slot_description = (TextView)itemView.findViewById(R.id.txt_time_slot_description);
        }
    }
}
