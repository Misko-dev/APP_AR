package cl.app.autismo_rancagua.Utilidades.TimeTableView;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cl.app.autismo_rancagua.R;
import cl.app.autismo_rancagua.Utilidades.TimeTableView.listener.ISchedule;
import cl.app.autismo_rancagua.Utilidades.TimeTableView.listener.OnDateBuildAapter;
import cl.app.autismo_rancagua.Utilidades.TimeTableView.listener.OnScrollViewBuildAdapter;
import cl.app.autismo_rancagua.Utilidades.TimeTableView.listener.OnWeekChangedAdapter;
import cl.app.autismo_rancagua.Utilidades.TimeTableView.model.Schedule;
import cl.app.autismo_rancagua.Utilidades.TimeTableView.model.ScheduleEnable;
import cl.app.autismo_rancagua.Utilidades.TimeTableView.model.ScheduleManager;
import cl.app.autismo_rancagua.Utilidades.TimeTableView.model.ScheduleSupport;
import cl.app.autismo_rancagua.Utilidades.TimeTableView.utils.ScreenUtils;


/**
 * 课程表控件
 *
 * @author Administrator
 * 
 */
public class TimetableView extends LinearLayout{

	private static final String TAG = "TimetableView";

	// 存储一周内的每天的课程数据以及每天的Layout
	private ArrayList[] data = new ArrayList[7];
	private LinearLayout[] panels = new LinearLayout[7];
	private LinearLayout weekPanel;

	// 当前周
	private int curWeek = 1;
	private ScheduleManager scheduleManager;
	private Context context;
	private String curTerm = "Semestre de primavera senior";

	// 课程数据源
	private List<Schedule> dataSource = null;

	LayoutInflater inflater;

	//根布局、日期栏容器
	private LinearLayout containerLayout;
	private LinearLayout dateLayout;

	//监听
	private ISchedule.OnWeekChangedListener onWeekChangedListener;
	private ISchedule.OnScrollViewBuildListener onScrollViewBuildListener;
	private ISchedule.OnDateBuildListener onDateBuildListener;

	public TimetableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		inflater = LayoutInflater.from(context);

		initAttr(attrs);
		initView(context);
	}

	private void initAttr(AttributeSet attrs) {
		TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.TimetableView);
		int curWeek=ta.getInteger(R.styleable.TimetableView_cur_week,1);
		String curTerm=ta.getString(R.styleable.TimetableView_cur_term);

		String source=ta.getString(R.styleable.TimetableView_source);
		String data=ta.getString(R.styleable.TimetableView_data);
		final String onWeekChanged=ta.getString(R.styleable.TimetableView_onWeekChanged);
		final String onItemClicked=ta.getString(R.styleable.TimetableView_onItemClicked);

		int defMarTop= (int) context.getResources().getDimension(R.dimen.weekItemMarTop);
		int defMarLeft= (int) context.getResources().getDimension(R.dimen.weekItemMarLeft);
		int defItemHeight= (int) context.getResources().getDimension(R.dimen.weekItemHeight);

		int marTop= (int) ta.getDimension(R.styleable.TimetableView_mar_top,defMarTop);
		int marLeft= (int) ta.getDimension(R.styleable.TimetableView_mar_left,defMarLeft);
		int itemHeight= (int) ta.getDimension(R.styleable.TimetableView_item_height,defItemHeight);
		int thisWeekCorner= (int) ta.getDimension(R.styleable.TimetableView_thisweek_corner,8);
		int nonWeekCorner= (int) ta.getDimension(R.styleable.TimetableView_nonweek_corner,8);
		int maxSlideItem=ta.getInteger(R.styleable.TimetableView_max_slide_item,12);
		boolean isShowNotWeek=ta.getBoolean(R.styleable.TimetableView_show_notcurweek,true);

		setCurWeek(curWeek);
		setCurTerm(curTerm);

		Object sourceResult=getInvokeResult(source);
		if(sourceResult!=null){
			setSource((List<? extends ScheduleEnable>) sourceResult);
		}

		Object dataResult=getInvokeResult(data);
		if(dataResult!=null){
			setData((List<Schedule>) dataResult);
		}

		if(onWeekChanged!=null){
			setOnWeekChangedListener(new ISchedule.OnWeekChangedListener() {
				@Override
				public void onWeekChanged(int curWeek) {
					invokeMethodByParams(onWeekChanged,new Class[]{int.class},new Object[]{curWeek});
				}
			});
		}

		if(onItemClicked!=null){
			getScheduleManager().setOnItemClickListener(new ISchedule.OnItemClickListener() {
				@Override
				public void onItemClick(View v, List<Schedule> scheduleList) {
					invokeMethodByParams(onItemClicked,new Class[]{View.class,ArrayList.class},
							new Object[]{v,scheduleList});
				}
			});
		}


		getScheduleManager().setMarTop(marTop);
		getScheduleManager().setMarLeft(marLeft);
		getScheduleManager().setItemHeight(itemHeight);
		getScheduleManager().setThisWeekCorner(thisWeekCorner);
		getScheduleManager().setNonThisWeekCorner(nonWeekCorner);
		getScheduleManager().setMaxSlideItem(maxSlideItem);
		getScheduleManager().setShowNotCurWeek(isShowNotWeek);

	}

	/**
	 * Método de llamada de reflexión
	 * * @param methodName nombre del método
	 * * @param types El tipo de la clase de parámetro
	 * * Valor del parámetro @param objs
	 */
	public void invokeMethodByParams(String methodName,Class<?>[] types,Object[] objs){
		if(methodName==null) return;
		Class<?> contextClass=context.getClass();
		try {
			Method method=contextClass.getMethod(methodName,types);
			method.invoke(contextClass.newInstance(),objs);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
		return;
	}


	/**
	 * Use el método de llamada de reflexión para obtener el valor de retorno
	 * * @param methodName nombre del método
	 * * @return
	 */
	public Object getInvokeResult(String methodName){
		if(methodName==null) return null;
		Class<?> contextClass=context.getClass();
		try {
			Method method=contextClass.getMethod(methodName);
			Object obj=method.invoke(contextClass.newInstance());
			return obj;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 设置日期栏构建监听器
	 * @param onDateBuildListener
	 * @return
	 */
	public TimetableView setOnDateBuildListener(ISchedule.OnDateBuildListener onDateBuildListener) {
		this.onDateBuildListener = onDateBuildListener;
		return this;
	}

	/**
	 * 获取日期栏构建监听器
	 * @return
	 */
	public ISchedule.OnDateBuildListener getOnDateBuildListener() {
		if(onDateBuildListener==null) onDateBuildListener=new OnDateBuildAapter();
		return onDateBuildListener;
	}

	/**
	 * 获取周次改变监听器
	 * @return
	 */
	public ISchedule.OnWeekChangedListener getOnWeekChangedListener() {
		if(onWeekChangedListener==null) onWeekChangedListener=new OnWeekChangedAdapter();
		return onWeekChangedListener;
	}

	/**
	 * 设置周次改变监听器
	 * @param onWeekChangedListener
	 * @return
	 */
	public TimetableView setOnWeekChangedListener(ISchedule.OnWeekChangedListener onWeekChangedListener) {
		this.onWeekChangedListener = onWeekChangedListener;
		return this;
	}

	/**
	 * Establecer semana actual
	 * * @param curWeek semana actual
	 * * @return
	 */
	public TimetableView setCurWeek(int curWeek) {
		if(curWeek<1) this.curWeek=1;
		else if(curWeek>25) this.curWeek=25;
		else this.curWeek = curWeek;
		onBind(curWeek);
		return this;
	}

	/**
	 * Establecer la hora de inicio para calcular la semana actual
	 * * @param startTime Una cadena que satisface el modo "aaaa-MM-dd HH: mm: ss"
	 * * @return
	 */
	public TimetableView setCurWeek(String startTime) {
		int week = ScheduleSupport.timeTransfrom(startTime);
		if (week == -1)
			setCurWeek(1);
		else
			setCurWeek(week);
		onBind(week);
		return this;
	}

	/**
	 * Obtén la semana actual
	 * @return
	 */
	public int getCurWeek() {
		return curWeek;
	}

	/**
	 * Establecer el semestre actual
	 * * @param curTerm
	 * * @return
	 */
	public TimetableView setCurTerm(String curTerm) {
		this.curTerm = curTerm;
		return this;
	}

	/**
	 * 获取当前学期
	 * @return
	 */
	public String getCurTerm() {
		return curTerm;
	}

	/**
	 * 获取数据源
	 * @return
	 */
	public List<Schedule> getDataSource() {
		return dataSource;
	}

	/**
	 * 设置数据源
	 * @param dataSource
	 * @return
	 */
	public TimetableView setData(List<Schedule> dataSource) {
		this.dataSource=ScheduleSupport.getColorReflect(dataSource);
		return this;
	}

	/**
	 * 设置数据源
	 * @param dataSource
	 * @return
	 */
	public TimetableView setSource(List<? extends ScheduleEnable> dataSource) {
		setData(ScheduleSupport.transform(dataSource));
		return this;
	}

	/**
	 * 获取ScheduleManager对象
	 * @return
	 */
	public ScheduleManager getScheduleManager() {
		if(scheduleManager==null) scheduleManager=new ScheduleManager(context);
		return scheduleManager;
	}

	/**
	 * 设置滚动布局构建监听器
	 * @param onScrollViewBuildListener
	 * @return
	 */
	public TimetableView setOnScrollViewBuildListener(ISchedule.OnScrollViewBuildListener onScrollViewBuildListener) {
		this.onScrollViewBuildListener = onScrollViewBuildListener;
		return this;
	}

	/**
	 * 周次改变时的回调
	 */
	private void onBind(int cur){
		getOnWeekChangedListener().onWeekChanged(cur);
	}


	/**
	 * 获取滚动布局构建监听器
	 * @return
	 */
	public ISchedule.OnScrollViewBuildListener getOnScrollViewBuildListener() {
		if(onScrollViewBuildListener==null) onScrollViewBuildListener=new OnScrollViewBuildAdapter();
		return onScrollViewBuildListener;
	}

	public void initView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.timetable_layout, this);
		containerLayout=findViewById(R.id.id_container);
		dateLayout=findViewById(R.id.id_datelayout);
	}

	/**
	 * 等同于showView()
	 *
	 * @see TimetableView#showView()
	 */
	public void updateView(){
		showView();
	}

	/**
	 * 将日期栏设为隐藏状态
	 * @return
	 */
	public TimetableView hideDateView(){
		dateLayout.setVisibility(GONE);
		return this;
	}

	/**
	 * 将日期栏设为可见状态
	 * @return
	 */
	public TimetableView showDateView(){
		dateLayout.setVisibility(VISIBLE);
		return this;
	}

	/**
	 * 更新日期栏
	 */
	public void updateDateView(){
		dateLayout.removeAllViews();
		float perWidth= ScreenUtils.getWidthInPx(context)/11.5f;
		int height=context.getResources().getDimensionPixelSize(R.dimen.headHeight);

		//日期栏
		View[] views=getOnDateBuildListener().getDateViews(inflater,perWidth,height);
		for(View v:views){
			if(v!=null){
				dateLayout.addView(v);
			}
		}

		getOnDateBuildListener().onUpdateDate();
		getOnDateBuildListener().onHighLight();
	}

	/**
	 * 侧边栏更新
	 */
	public void updateSlideView(){
		getScheduleManager().newSlideView(weekPanel);
	}

	/**
	 * 绘制课程表
	 */
	public void showView() {
		if(dataSource==null) return;
		Log.d(TAG, "showTimetableView: ");

		//Implementar el reemplazo de ScrollView
		if(findViewById(R.id.id_scrollview)==null){
			View view=getOnScrollViewBuildListener().getScrollView(inflater);
			containerLayout.addView(view);
			//Inicializar
			weekPanel=findViewById(R.id.weekPanel_0);
			for (int i = 0; i < panels.length; i++) {
				panels[i] = (LinearLayout) this.findViewById(R.id.weekPanel_1 + i);
				data[i] = new ArrayList<>();
			}
		}

		//Fecha de actualización
		updateDateView();
		updateSlideView();

		//Datos divididos
		for(int i=0;i<7;i++){
			data[i].clear();
		}
		for (int i = 0; i < dataSource.size(); i++) {
			Schedule bean = dataSource.get(i);
			if (bean.getDay() != -1)
				data[bean.getDay() - 1].add(bean);
		}

		//Ordenar
		ScheduleSupport.sortList(data);

		//Curso de llenado
		for (int i = 0; i < panels.length; i++) {
			panels[i].removeAllViews();
			getScheduleManager().addToLayout(panels[i], data[i], curWeek);
		}

		//Interruptor semanal para garantizar la visualización del ángulo cuando se superpone
		changeWeek(getCurWeek(),false);
	}

	/**
	 * 周次切换
	 * @param week 周次
	 * @param isCurWeek 是否强制设置为本周
	 */
	public void changeWeek(int week, boolean isCurWeek) {
		if(isCurWeek) changeWeekForce(week);
		else changeWeekOnly(week);
	}

	/**
	 * 仅仅切换周次，不修改当前周
	 * @param week
	 */
	public void changeWeekOnly(int week) {
		getScheduleManager().changeWeek(panels, data, week);
		onBind(week);
	}

	/**
	 * 切换周次且修改为当前周
	 * @param week
	 */
	public void changeWeekForce(int week) {
		getScheduleManager().changeWeek(panels, data, week);
		setCurWeek(week);
	}
}
