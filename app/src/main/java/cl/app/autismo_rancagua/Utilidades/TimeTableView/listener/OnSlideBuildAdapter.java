package cl.app.autismo_rancagua.Utilidades.TimeTableView.listener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import cl.app.autismo_rancagua.R;


/**
 * La implementación predeterminada del generador de compilación de la barra lateral
 */

public class OnSlideBuildAdapter implements ISchedule.OnSlideBuildListener {
    @Override
    public void setBackground(LinearLayout layout) {
    }

    @Override
    public int getSlideItemSize() {
        return 12;
    }

    @Override
    public View onBuildSlideItem(int pos, LayoutInflater inflater,
                                 int itemHeight,int marTop) {
        View view=inflater.inflate(R.layout.item_slide_default,null,false);
        TextView textView=view.findViewById(R.id.item_slide_textview);

        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                itemHeight);
        lp.setMargins(0,marTop,0,0);
        textView.setLayoutParams(lp);
        /*textView.setText((pos+1)+"");*/

        if (pos + 8 < 12){
            textView.setText((pos+8)+":00\nAM");
        }else{
            textView.setText((pos+8)+":00\nPM");
        }

        return view;
    }
}
