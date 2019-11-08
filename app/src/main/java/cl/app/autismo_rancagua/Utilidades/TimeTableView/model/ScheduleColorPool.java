package cl.app.autismo_rancagua.Utilidades.TimeTableView.model;

import android.content.Context;
import android.graphics.Color;



import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cl.app.autismo_rancagua.R;

/**
 * 颜色池，管理课程项可挑选的颜色
 */

public class ScheduleColorPool {

    Context context;

    //El color de fondo del curso no está en esta semana.
    private int uselessColor;

    public ScheduleColorPool(Context context){
        this.context=context;
        setUselessColor(Color.parseColor("#E6E6E6"));
        reset();
    }

    //
    //Use colecciones para mantener grupos de colores
    private List<Integer> colorPool;

    /**
     * Obtenga el color del curso que no es de esta semana
     * @return
     */
    public int getUselessColor() {
        return uselessColor;
    }

    /**
     * Establecer el color del curso que no sea esta semana
     *      * @param uselessColor no es el color del curso de esta semana
     *      * @return
     */
    public ScheduleColorPool setUselessColor(int uselessColor) {
        this.uselessColor = uselessColor;
        return this;
    }

    /**
     * 得到颜色池的实例，即List集合
     * @return
     */
    public List<Integer> getPoolInstance() {
        if(colorPool==null) colorPool=new ArrayList<>();
        return colorPool;
    }

    /**
     * 根据索引获取颜色，索引越界默认返回 Color.GRAY
     * @param i 索引
     * @return
     */
    public int getColor(int i){
        if(i<0||i>=size()) return Color.GRAY;
        return colorPool.get(i);
    }

    /**
     * 使用模运算根据索引从颜色池中获取颜色,
     * 如果i<0，转换为正数,
     * 否则：重新计算索引j=i mod size
     * @param i 索引
     * @return 颜色
     */
    public int getColorAuto(int i){
        if(i<0) return getColorAuto(-i);
        return getColor(i%size());
    }

    /**
     * 将指定集合中的颜色加入到颜色池中
     * @param ownColorPool 集合
     * @return
     */
    public ScheduleColorPool addAll(Collection<? extends Integer> ownColorPool){
        getPoolInstance().addAll(ownColorPool);
        return this;
    }

    /**
     * 颜色池的大小
     * @return
     */
    public int size(){
        if(getPoolInstance()==null) return 0;
        return getPoolInstance().size();
    }

    /**
     * 清空颜色池，清空默认颜色
     * @return
     */
    public ScheduleColorPool clear(){
        getPoolInstance().clear();
        return this;
    }

    /**
     * 在颜色池中添加一些自定义的颜色
     * @param colorIds 多个颜色
     * @return
     */
    public ScheduleColorPool add(int... colorIds){
        if(colorIds!=null){
            for(int i=0;i<colorIds.length;i++){
                colorPool.add(colorIds[i]);
            }
        }
        return this;
    }

    /**
     * 重置，先在池子里添加一些默认的课程项颜色
     * @return
     */
    public ScheduleColorPool reset(){
        int[] colors=new int[]{
                R.color.color_1,R.color.color_2, R.color.color_3,R.color.color_4,
                R.color.color_5,R.color.color_6,R.color.color_7,R.color.color_8,
                R.color.color_9,R.color.color_10,R.color.color_11,R.color.color_31,
                R.color.color_32,R.color.color_33,R.color.color_34,R.color.color_35
        };

        clear();

        for(int i=0;i<colors.length;i++){
            add(context.getResources().getColor(colors[i]));
        }
        return this;
    }
}
