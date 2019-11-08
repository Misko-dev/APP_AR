package cl.app.autismo_rancagua.Utilidades.TimeTableView.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;



import java.util.ArrayList;
import java.util.List;

import cl.app.autismo_rancagua.R;
import cl.app.autismo_rancagua.Utilidades.TimeTableView.listener.IWeekView;
import cl.app.autismo_rancagua.Utilidades.TimeTableView.listener.OnWeekItemClickedAdapter;
import cl.app.autismo_rancagua.Utilidades.TimeTableView.listener.OnWeekLeftClickedAdapter;
import cl.app.autismo_rancagua.Utilidades.TimeTableView.model.Schedule;
import cl.app.autismo_rancagua.Utilidades.TimeTableView.model.ScheduleEnable;
import cl.app.autismo_rancagua.Utilidades.TimeTableView.model.ScheduleSupport;

/**
 * 控件实现的周次选择View
 */

public class WeekView extends LinearLayout {

    private static final String TAG = "WeekView";
    LayoutInflater mInflate;

    //周次的容器
    LinearLayout container;

    //跟布局
    LinearLayout root;

    //左侧按钮
    LinearLayout leftlayout;

    //数据
    private List<Schedule> dataSource;

    //布局保存
    private List<LinearLayout> layouts;
    private List<TextView> textViews;

    //当前周
    private int curWeek=1;
    private int preIndex=1;


    // ¿Cuántos artículos?
    private int itemCount = 20;

    private IWeekView.OnWeekItemClickedListener onWeekItemClickedListener;
    private IWeekView.OnWeekLeftClickedListener onWeekLeftClickedListener;

    public WeekView(Context context) {
        this(context, null);
    }

    /**
     * 获取Item点击监听
     * @return
     */
    public IWeekView.OnWeekItemClickedListener getOnWeekItemClickedListener() {
        if(onWeekItemClickedListener==null) onWeekItemClickedListener=new OnWeekItemClickedAdapter();
        return onWeekItemClickedListener;
    }

    /**
     * 设置Item点击监听
     * @param onWeekItemClickedListener
     * @return
     */
    public WeekView setOnWeekItemClickedListener(IWeekView.OnWeekItemClickedListener onWeekItemClickedListener) {
        this.onWeekItemClickedListener = onWeekItemClickedListener;
        return this;
    }

    /**
     * 获取左侧按钮点击监听
     * @return
     */
    public IWeekView.OnWeekLeftClickedListener getOnWeekLeftClickedListener() {
        if(onWeekLeftClickedListener==null) onWeekLeftClickedListener=new OnWeekLeftClickedAdapter();
        return onWeekLeftClickedListener;
    }

    /**
     * 设置左侧按钮点击监听
     * @param onWeekLeftClickedListener
     * @return
     */
    public WeekView setOnWeekLeftClickedListener(IWeekView.OnWeekLeftClickedListener onWeekLeftClickedListener) {
        this.onWeekLeftClickedListener = onWeekLeftClickedListener;
        return this;
    }

    /**
     * 设置当前周
     * @param curWeek
     * @return
     */
    public WeekView setCurWeek(int curWeek) {
        if(curWeek<1) curWeek=1;
        this.curWeek = curWeek;
        return this;
    }

    /**
     * 设置项数
     * @param count
     * @return
     */
    public WeekView setItemCount(int count) {
        if (count <= 0 || count > 25) return this;
        this.itemCount = count;
        return this;
    }

    /**
     * 设置数据源
     * @param list
     * @return
     */
    public WeekView setSource(List<? extends ScheduleEnable> list) {
        setData(ScheduleSupport.transform(list));
        return this;
    }

    /**
     * 设置数据源
     * @param scheduleList
     * @return
     */
    public WeekView setData(List<Schedule> scheduleList) {
        if (scheduleList == null) return null;
        this.dataSource = scheduleList;
        return this;
    }

    /**
     * 获取数据源
     * @return
     */
    public List<Schedule> getDataSource() {
        if (dataSource == null) dataSource = new ArrayList<>();
        return dataSource;
    }

    public WeekView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflate = LayoutInflater.from(context);
        initView();
    }

    private void initView() {
        mInflate.inflate(R.layout.view_weekview, this);
        container = findViewById(R.id.id_weekview_container);
        root=findViewById(R.id.id_root);
        leftlayout=findViewById(R.id.id_weekview_leftlayout);
    }

    /**
     * 初次构建时调用，显示周次选择布局
     */
    public void showView() {
        Log.d(TAG, "showView: ");
        isShow(true);
        container.removeAllViews();
        layouts=new ArrayList<>();
        textViews=new ArrayList<>();

        leftlayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnWeekLeftClickedListener().onWeekLeftClicked();
            }
        });

        for (int i = 1; i <= itemCount; i++) {
            final int tmp=i;
            View view = mInflate.inflate(R.layout.item_weekview, null);
            final LinearLayout perLayout=view.findViewById(R.id.id_perweekview_layout);
            TextView weekText = view.findViewById(R.id.id_weektext);
            TextView bottomText = view.findViewById(R.id.id_weektext_bottom);

            weekText.setText("Primero" + i + "Semana");
            if(i==curWeek) bottomText.setText("(Esta semana)");
            PerWeekView perWeekView = view.findViewById(R.id.id_perweekview);
            perWeekView.setData(getDataSource(), i);
            perLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    resetBackground();
                    preIndex=tmp;
                    perLayout.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.weekview_white));
                    getOnWeekItemClickedListener().onWeekClicked(tmp);
                }
            });

            layouts.add(perLayout);
            textViews.add(bottomText);
            container.addView(view);
        }
        if(curWeek>0&&curWeek<=layouts.size()){
            layouts.get(curWeek-1).setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.weekview_thisweek));
        }
    }

    /**
     * Después de cambiar la semana actual, puede llamar a este método para corregir el texto en la parte inferior.
     *      * @return
     */
    public WeekView updateView(){
        if(layouts==null||layouts.size()==0) return this;
        if(textViews==null||textViews.size()==0) return this;

        for(int i=0;i<layouts.size();i++){
            if(curWeek-1==i) {
                textViews.get(i).setText("(Esta semana)");
            }
            else{
                textViews.get(i).setText("");
            }
            layouts.get(i).setBackgroundColor(getContext().getResources().getColor(R.color.app_course_chooseweek_bg));
        }

        if(curWeek>0&&curWeek<=layouts.size()){
            layouts.get(curWeek-1).setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.weekview_thisweek));
        }
        return this;
    }

    /**
     * 重置背景色
     */
    public void resetBackground(){
        layouts.get(preIndex-1).setBackgroundColor(getContext().getResources().getColor(R.color.app_course_chooseweek_bg));
        layouts.get(curWeek-1).setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.weekview_thisweek));
    }

    /**
     * 设置控件的可见性
     * @param isShow true:显示，false:隐藏
     */
    public WeekView isShow(boolean isShow){
        if(isShow){
            root.setVisibility(VISIBLE);
        }else{
            root.setVisibility(GONE);
        }
        return this;
    }

    /**
     * 判断该控件是否显示
     * @return
     */
    public boolean isShowing(){
        if(getVisibility()==GONE) return false;
        return true;
    }
}
