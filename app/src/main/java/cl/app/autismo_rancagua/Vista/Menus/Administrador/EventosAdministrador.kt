package cl.app.autismo_rancagua.Vista.Menus.Administrador

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import cl.app.autismo_rancagua.Api.ApiClient
import cl.app.autismo_rancagua.Api.Eventos.ApiEVE
import cl.app.autismo_rancagua.Modelo.Evento.Evento
import cl.app.autismo_rancagua.R
import cl.app.autismo_rancagua.Utilidades.CalendarView.CalendarDayView
import cl.app.autismo_rancagua.Utilidades.CalendarView.CalendarMonthAdapter
import cl.app.autismo_rancagua.Utilidades.CalendarView.CalendarYearAdapter
import cl.app.autismo_rancagua.Utilidades.CalendarView.setTextColorResource
import cl.app.autismo_rancagua.Vista.Mantenedores.Evento.MEvento
import cl.app.autismo_rancagua.Vista.Menus.Administrador.EventosAdministrador.companion.año_maximo
import cl.app.autismo_rancagua.Vista.Menus.Administrador.EventosAdministrador.companion.año_minimo
import cl.app.autismo_rancagua.Vista.Menus.Administrador.EventosAdministrador.companion.dia_actual
import cl.app.autismo_rancagua.Vista.Menus.Administrador.EventosAdministrador.companion.fecha_actual
import cl.app.autismo_rancagua.Vista.Menus.Administrador.EventosAdministrador.companion.fecha_color
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import kotlinx.android.synthetic.main.eventos_activity.*
import kotlinx.android.synthetic.main.calendar_day.view.*
import kotlinx.android.synthetic.main.calendario_view.calendar_search
import kotlinx.android.synthetic.main.dias_semana.layout_legend
import kotlinx.android.synthetic.main.calendario_view.layout_list_calendar
import kotlinx.android.synthetic.main.calendario_view.list_calendar
import kotlinx.android.synthetic.main.calendario_view.*
import kotlinx.android.synthetic.main.toolbar_volver.*
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
import org.threeten.bp.temporal.WeekFields
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class EventosAdministrador : AppCompatActivity() {

    var apiEVE: ApiEVE? = null

    /*ADAPTADOR*/
    private var adaptadorHorarioTaller: AdaptadorHorarioTaller? = null
    private var fecha_inicial: LocalDate? = null
    private var fecha_termino: LocalDate? = null
    private var mCalendarScrollDate: YearMonth? = null
    private var mIsMonthSelectionActive = false
    private var mIsYearSelectionActive = false
    private var mIsTodaySelectionActive = false
    private val formato1 = DateTimeFormatter.ofPattern("EEEE dd")
    @SuppressLint("SimpleDateFormat")
    private val formato2 = SimpleDateFormat("yyyy-MM-dd")

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eventos_activity)
        /*LINEA NECESARIA PARA CARGAR LAS FECHAS*/
        AndroidThreeTen.init(this)

        apiEVE = ApiClient.retrofit!!.create(ApiEVE::class.java)
        txt_buscar.visibility = View.GONE
        configuracion_inicial()
        listeners()
        listener_botones()


        /*val primer_dia_mes_actual = dia_actual.with(firstDayOfMonth())
        val ultimo_dia_mes_actual = dia_actual.with(lastDayOfMonth())*/
        /* getTodos(primer_dia_mes_actual.toString(),ultimo_dia_mes_actual.toString())*/


        getTodos("2019-01-01", "2050-12-31")
        /* getEventosxFecha(dia_actual.toString())*/
        /*getEventosxRango(dia_actual.toString(),dia_actual.toString())*/

        /*AQUI PINTO LOS DIAS Y ENTREGO LA LISTA CON LOS COLORES*/
        calendar_search.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                with(container) {
                    this.day = day
                    calendarDayView.text = day.date.dayOfMonth.toString()
                    calendarDayView.background = null
                    calendarDayView.showCircle = false


                    val select = fecha_inicial == day.date && fecha_termino == null
                            || (fecha_inicial != null && fecha_termino != null && day.date >= fecha_inicial && day.date <= fecha_termino)

                    calendarDayView.isCurrentDay = day.date == dia_actual
                    calendarDayView.isCurrentMonth = day.owner == DayOwner.THIS_MONTH
                    calendarDayView.isSelected = select

                    if (calendarDayView.isCurrentDay) {
                        selectToday(select && fecha_termino == null)
                    }



                    fecha_color[day.date]?.let { colors ->
                        calendarDayView.showCircle = true
                        calendarDayView.setColors(colors)
                    }
                }
            }
        }


        btn_volver_tbv.setOnClickListener {
            finish()
        }


    }

    private fun listener_botones() {

        btn_registrar_evento.setOnClickListener {
            startActivity(Intent(this@EventosAdministrador, MEvento::class.java))
        }


        text_today.setOnClickListener {
            if (fecha_termino == null && fecha_inicial == dia_actual) {
                fecha_inicial = null
                selectToday(false)
                calendar_search.notifyCalendarChanged()

            } else {
                fecha_inicial = dia_actual
                fecha_termino = null
                selectToday(true)
                calendar_search.notifyCalendarChanged()
                scrollToMonth(YearMonth.now())

            }
        }

        text_month.setOnClickListener {
            if (mIsMonthSelectionActive) {
                hideMonthSelection()
            } else {
                hideYearSelection()
                showMonthSelection()
            }
        }
    }

    private fun configuracion_inicial() {

        val dias_de_la_semana = obtener_dias_de_la_semana()
        for (i in 0 until layout_legend.childCount) {
            val textView = layout_legend.getChildAt(i) as TextView
            textView.text =
                dias_de_la_semana[i].getDisplayName(TextStyle.SHORT, Locale.getDefault()).replace(".","")
        }

        calendar_search.setup(año_minimo, año_maximo, dias_de_la_semana.first())

        exThreeSelectedDateText.text = formato1.format(dia_actual)


        val savedScrollDate = mCalendarScrollDate
        if (savedScrollDate != null) {
            scrollToMonth(savedScrollDate)
        } else {
            scrollToMonth(fecha_actual)
        }
    }

    private fun listeners() {
        calendar_search.monthScrollListener = { calendarMonth ->
            mCalendarScrollDate = calendarMonth.yearMonth
            updateCalendarHeader(calendarMonth.yearMonth)
            layout_list_calendar.setOnTouchListener { _, _ -> true }
            selectToday(mIsTodaySelectionActive)
            if (mIsMonthSelectionActive) {
                showMonthSelection()
            } else if (mIsYearSelectionActive) {
                showYearSelection(/*item*/)
            }
        }
    }

    /*BOTON HOY,DIA ACTUAL*/
    private fun selectToday(select: Boolean) {
        mIsTodaySelectionActive = select
        if (select) {
            text_today.setTextColorResource(R.color.white)
            text_today.setBackgroundColor(resources.getColor(R.color.colorAccent))
        } else {
            text_today.setTextColor(resources.getColor(R.color.blanco))
            text_today.setBackgroundColor(resources.getColor(R.color.colorAccent))
        }
    }

    /*ENTREGA EL AÑO Y EL MES,2019-07*/
    private fun updateCalendarHeader(yearMonth: YearMonth) {
        text_month.text = DateTimeFormatter.ofPattern("MMMM")
            .format(yearMonth)
            .capitalize()
        text_year.text = yearMonth.year.toString()
    }

    /*ENTREGA EL AÑO Y EL MES ACTUAL,2019-07*/
    private fun scrollToMonth(yearMonth: YearMonth) {
        mCalendarScrollDate = yearMonth
        calendar_search.scrollToMonth(yearMonth)
        updateCalendarHeader(yearMonth)
    }

    /*ME DEVUELVE EL MES QUE SELECCIONE*/
    private fun showMonthSelection() {
        mIsMonthSelectionActive = true
        text_month.setTextColorResource(R.color.white)
        text_month.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
        val adapter =
            CalendarMonthAdapter(object :
                CalendarMonthAdapter.OnMonthListInteractionListener {
                override fun onMonthClick(month: Month) {
                    mIsMonthSelectionActive = false
                    hideMonthSelection()
                    mCalendarScrollDate?.let {
                        scrollToMonth(
                            YearMonth.of(
                                it.year,
                                month.value
                            )
                        )
                    }
                }
            })
        layout_list_calendar.visibility = View.VISIBLE
        list_calendar.layoutManager =
            GridLayoutManager(this, 4, GridLayoutManager.HORIZONTAL, false)
        list_calendar.adapter = adapter
        adapter.showList(Month.values().toList())
    }

    private fun hideMonthSelection() {
        mIsMonthSelectionActive = false
        layout_list_calendar.visibility = View.GONE
        text_month.setTextColor(resources.getColor(R.color.blanco))
        text_month.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
    }

    private fun showYearSelection(/*item: SearchItem*/) {
        mIsYearSelectionActive = true
        text_year.setTextColorResource(R.color.white)
        text_year.setBackgroundColor(resources.getColor(R.color.colorAccent))

        val adapter =
            CalendarYearAdapter(object :
                CalendarYearAdapter.OnYearListInteractionListener {
                override fun onYearClick(year: Year) {
                    hideYearSelection()
                    mCalendarScrollDate?.let {
                        scrollToMonth(
                            YearMonth.of(
                                year.value,
                                it.monthValue
                            )
                        )
                    }
                }
            })
        layout_list_calendar.visibility = View.VISIBLE
        list_calendar.layoutManager =
            GridLayoutManager(this, 4, GridLayoutManager.HORIZONTAL, false)
        list_calendar.adapter = adapter
    }

    private fun hideYearSelection() {
        mIsYearSelectionActive = false
        layout_list_calendar.visibility = View.GONE
        text_year.setTextColor(resources.getColor(R.color.blanco))
        text_year.setBackgroundColor(resources.getColor(R.color.colorPrimary))
    }

    fun obtener_dias_de_la_semana(): Array<DayOfWeek> {
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        var daysOfWeek = DayOfWeek.values()
        // Order `daysOfWeek` array so that firstDayOfWeek is at index 0.
        if (firstDayOfWeek != DayOfWeek.MONDAY) {
            val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
            val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
            daysOfWeek = rhs + lhs
        }
        return daysOfWeek
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    /*DEVUELVE LAS FECHAS SEGUN LO QUE SELECCIONE EN EL CALENDARIO*/
    inner class DayViewContainer(view: View) : ViewContainer(view) {
        lateinit var day: CalendarDay // Will be set when this container is bound.
        val calendarDayView: CalendarDayView = view.text_calendar_day

        init {
            view.setOnClickListener {
                val date = day.date
                if (fecha_inicial != null) {
                    if (date < fecha_inicial || fecha_termino != null) { /*DEVUELVE LA PRIMERA FECHA QUE SELECCIONO*/
                        fecha_inicial = date
                        fecha_termino = null
                        exThreeSelectedDateText.text = formato1.format(fecha_inicial)
                        /*  obtener(fecha_inicial.toString())*/
                        /* getEventosxFecha(fecha_inicial.toString())*/
                        getEventos(fecha_inicial.toString(), fecha_inicial.toString())

                    } else if (date != fecha_inicial) {   /*DEVUELVE LAS FECHAS POR RANGO*/
                        fecha_termino = date
                        val primera_fecha = formato1.format(fecha_inicial)
                        val segunda_fecha = formato1.format(fecha_termino)
                        exThreeSelectedDateText.text =
                            ("Desde el $primera_fecha, hasta el $segunda_fecha")
                        getEventos(fecha_inicial.toString(), fecha_termino.toString())


                    } else if (date == fecha_inicial) {
                        fecha_inicial = null
                        if(adaptadorHorarioTaller != null){
                            adaptadorHorarioTaller!!.limpiar()
                        }

                    }
                } else {
                    fecha_inicial = date  /* DEVUELVE LA PRIMERA FECHA*/
                    exThreeSelectedDateText.text = formato1.format(fecha_inicial)
                   /* getEventosxFecha(fecha_inicial.toString())*/
                    getEventos(fecha_inicial.toString(), fecha_inicial.toString())
                }
                calendar_search.notifyCalendarChanged()

            }
        }
    }


    object companion {
        val dia_actual = LocalDate.now()
        val fecha_actual = YearMonth.now()
        val año_minimo = YearMonth.of(2019, Month.JANUARY)
        val año_maximo = YearMonth.of(2050, Month.DECEMBER)

        var talleres: ArrayList<Evento> = arrayListOf()
        var fecha_color = mutableMapOf<LocalDate, List<Int>>()
    }


    fun getTodos(fecha1: String, fecha2: String) {
        val date1 = LocalDate.parse(fecha1).toString()
        val date2 = LocalDate.parse(fecha2).toString()


        val call: Call<ArrayList<Evento>> = apiEVE!!.getEventosXFECHAS(date1,date2)
        call.enqueue(object : Callback<ArrayList<Evento>> {
            @SuppressLint("Range", "SimpleDateFormat")
            override fun onResponse(call: Call<ArrayList<Evento>>, response: Response<ArrayList<Evento>>) {
                val eventos = response.body()!!

                fecha_color.clear()
                for (item in eventos) {
                    val fecha = LocalDate.parse(SimpleDateFormat("yyyy-MM-dd").format(item.FECHA))
                    val color = Color.parseColor(item.COLOR)
                    val contains = fecha_color.keys.any { key ->
                        key == fecha
                    }
                    if (contains) {
                        fecha_color[fecha] = fecha_color[fecha]!!.plus(listOf(color))
                    } else {
                        fecha_color[fecha] = listOf(color)
                    }
                }

                text_today.performClick()

            }

            override fun onFailure(call: Call<ArrayList<Evento>>, t: Throwable) {
                Log.e("ERROR","Error : $t")

            }
        })
    }


    fun getEventos(fecha_inicial: String, fecha_termino: String) {
        val date1 = LocalDate.parse(fecha_inicial).toString()
        val date2 = LocalDate.parse(fecha_termino).toString()



        val call: Call<ArrayList<Evento>> = apiEVE!!.getEventosXFECHAS(date1,date2)
        call.enqueue(object : Callback<ArrayList<Evento>> {
            override fun onResponse(call: Call<ArrayList<Evento>>, response: Response<ArrayList<Evento>>) {
                val eventos = response.body()!!

                adaptadorHorarioTaller =
                    AdaptadorHorarioTaller(
                        eventos.distinct().toMutableList(),
                        applicationContext
                    )

                val mLayoutManager = LinearLayoutManager(applicationContext)
                lista_evento.layoutManager = mLayoutManager
                lista_evento.itemAnimator = DefaultItemAnimator()
                lista_evento.adapter = adaptadorHorarioTaller
                adaptadorHorarioTaller!!.notifyDataSetChanged()

            }


            override fun onFailure(call: Call<ArrayList<Evento>>, t: Throwable) {
                Log.e("ERROR","Error : $t")

            }
        })
    }


    override fun onResume() {
        super.onResume()
        getTodos("2019-01-01", "2050-12-31")
    }
}
