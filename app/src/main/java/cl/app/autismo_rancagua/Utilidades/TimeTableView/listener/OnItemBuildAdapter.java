package cl.app.autismo_rancagua.Utilidades.TimeTableView.listener;

import android.graphics.drawable.GradientDrawable;
import android.widget.FrameLayout;
import android.widget.TextView;



import cl.app.autismo_rancagua.Utilidades.TimeTableView.model.Schedule;


/**
 *
 * La implementación predeterminada del elemento de escucha de compilación.
 */

public class OnItemBuildAdapter implements ISchedule.OnItemBuildListener {
    @Override
    public String getItemText(Schedule schedule, boolean isThisWeek) {
        if(isThisWeek){
            return schedule.getName()+"\nSala "+schedule.getRoom();
        }

        return schedule.getName();
    }

    @Override
    public boolean interceptItemBuild(Schedule schedule) {
        return false;
    }

    @Override
    public void onItemUpdate(FrameLayout layout, TextView textView, TextView countTextView, Schedule schedule, GradientDrawable gd) {

    }
}
