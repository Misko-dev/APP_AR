package cl.app.autismo_rancagua.Utilidades.TimeTableView.listener;


import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cl.app.autismo_rancagua.R;
import cl.app.autismo_rancagua.Utilidades.TimeTableView.model.ScheduleSupport;

/**
 * 日期栏的构建过程.
 */

public class OnDateBuildAapter implements ISchedule.OnDateBuildListener {

    private static final String TAG = "OnDateBuildAapter";

    //第一个：月份，之后7个表示周一至周日
    TextView[] textViews=new TextView[8];
    LinearLayout[] layouts=new LinearLayout[8];

    @Override
    public View[] getDateViews(LayoutInflater mInflate,float perWidth,int height) {
        View[] views=new View[8];
        View first=mInflate.inflate(R.layout.item_dateview_first,null,false);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams((int) perWidth,height);
        List<String> weekDays = ScheduleSupport.getWeekDate();

        //月份设置
        textViews[0]=first.findViewById(R.id.id_week_month);
        layouts[0]=null;
        views[0]=first;
        int month=Integer.parseInt(weekDays.get(0));
        first.setLayoutParams(lp);

        String mes = "";
        if(month == 1){
            mes = "ENE";
        }else if(month == 2){
            mes = "FEB";
        }else if(month == 3){
            mes = "MAR";
        }else if(month == 4){
            mes = "ABR";
        }else if(month == 5){
            mes = "MAY";
        }else if(month == 6){
            mes = "JUN";
        }else if(month == 7){
            mes = "JUL";
        }else if(month == 8){
            mes = "AGO";
        }else if(month == 9){
            mes = "SEP";
        }else if(month == 10){
            mes = "OCT";
        }else if(month == 11){
            mes = "NOV";
        }else if(month == 12){
            mes = "DIC";
        }
        textViews[0].setText(mes);

        //星期设置
        lp=new LinearLayout.LayoutParams((int)(perWidth*1.5),height);
        String[] dateArray=new String[]{"","LUN","MAR","MIE","JUE","VIE","SAB","DOM"};
        for(int i=1;i<8;i++){
            View v=mInflate.inflate(R.layout.item_dateview,null,false);
            TextView dayTextView=v.findViewById(R.id.id_week_day);
            textViews[i]=v.findViewById(R.id.id_week_date);
            layouts[i]=v.findViewById(R.id.id_week_layout);
            views[i]=v;
            layouts[i].setLayoutParams(lp);
            dayTextView.setText(dateArray[i]);
            textViews[i].setText(weekDays.get(i));
        }
        return views;
    }

    @Override
    public void onHighLight() {
        /*//Inicializar color de fondo
        int color=Color.parseColor("#F4F8F8");
        for(int i=1;i<8;i++){
            layouts[i].setBackgroundColor(color);
        }*/

        //Destacar
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if(dayOfWeek -1 == 0){
            dayOfWeek=7;
        }else{
            dayOfWeek = dayOfWeek - 1;
        }
        Log.e("DIA",Integer.toString(dayOfWeek));
        layouts[dayOfWeek].setBackgroundColor(Color.parseColor("#0F124D"));
    }

    @Override
    public void onUpdateDate() {
        if(textViews==null||textViews.length<8) return;

        List<String> weekDays = ScheduleSupport.getWeekDate();
        int month=Integer.parseInt(weekDays.get(0));

        String mes = "";
        if(month == 1){
            mes = "ENE";
        }else if(month == 2){
            mes = "FEB";
        }else if(month == 3){
            mes = "MAR";
        }else if(month == 4){
            mes = "ABR";
        }else if(month == 5){
            mes = "MAY";
        }else if(month == 6){
            mes = "JUN";
        }else if(month == 7){
            mes = "JUL";
        }else if(month == 8){
            mes = "AGO";
        }else if(month == 9){
            mes = "SEP";
        }else if(month == 10){
            mes = "OCT";
        }else if(month == 11){
            mes = "NOV";
        }else if(month == 12){
            mes = "DIC";
        }
        textViews[0].setText(mes);

        for(int i=1;i<8;i++){
            textViews[i].setText(weekDays.get(i));
        }
    }
}
