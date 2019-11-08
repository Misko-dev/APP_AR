package cl.app.autismo_rancagua.Utilidades.TimeTableView.model;

import java.io.Serializable;
import java.util.List;


public class Schedule implements Serializable{
	private String name;
	private String room;
	private String teacher;
	private int estado;
	private List<Integer> weekList;
	private int start;
	private int step;
	private int day;
	private int colorRandom = 0;

	public Schedule(String name, String room, String teacher,int estado,
					List<Integer> weekList, int start, int step, int day,
					int colorRandom, String id) {
		super();
		this.name = name;
		this.room = room;
		this.teacher = teacher;
		this.estado = estado;
		this.weekList = weekList;
		this.start = start;
		this.step = step;
		this.day = day;
		this.colorRandom = colorRandom;
	}
	
	public Schedule(String name, String room, String teacher,
					List<Integer> weekList, int start, int step, int day,
					int colorRandom) {
		super();
		this.name = name;
		this.room = room;
		this.teacher = teacher;
		this.weekList = weekList;
		this.start = start;
		this.step = step;
		this.day = day;
		this.colorRandom = colorRandom;
	}

	public int getEstado() {
		return estado;
	}

	public void setEstado(int estado) {
		this.estado = estado;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public String getTeacher() {
		return teacher;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}

	public void setWeekList(List<Integer> weekList) {
		this.weekList = weekList;
	}
	
	public List<Integer> getWeekList() {
		return weekList;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getColorRandom() {
		return colorRandom;
	}

	public void setColorRandom(int colorRandom) {
		this.colorRandom = colorRandom;
	}
	public Schedule() {
		super();
	}

}
