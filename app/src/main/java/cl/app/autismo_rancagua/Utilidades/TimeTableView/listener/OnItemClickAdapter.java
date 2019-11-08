package cl.app.autismo_rancagua.Utilidades.TimeTableView.listener;

import android.util.Log;
import android.view.View;


import java.util.List;

import cl.app.autismo_rancagua.Utilidades.TimeTableView.model.Schedule;

/**
 * Item点击的默认实现.
 */

public class OnItemClickAdapter implements ISchedule.OnItemClickListener {
    private static final String TAG = "OnItemClickAdapter";
    @Override
    public void onItemClick(View v, List<Schedule> scheduleList) {
        Log.e("ERROR", "onItemClick: ");
    }
}
