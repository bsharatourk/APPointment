package com.example.appointment.Interface;

import java.util.List;

public interface IAllSalonLoadListener {

    void onAllSalonLoadSuccess(List<String> areaNameList);
    void onAllSalonLoadFailure(String message);

}
