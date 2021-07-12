package com.example.appointment.Interface;

import com.example.appointment.Model.Salon;

import java.util.List;

public interface IBranchLoadListener {

    void onBranchLoadSuccess(List<Salon> salonList);
    void onBranchLoadFailed(String message);

}
