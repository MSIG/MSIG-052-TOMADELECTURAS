package com.msig.codigos;

public class datosDB {
	private String id_programa;
	private String codigo;
	private String f_ingreso;
	private String f_salida;
	private String horas;
	
	
	public datosDB() {
		super();
	}

	public datosDB(String codigo, String id_programa, String f_ingreso, String f_salida,
			String horas) {
		super();
		this.codigo = codigo;
		this.f_ingreso = f_ingreso;
		this.f_salida = f_salida;
		this.horas = horas;
		this.id_programa = id_programa;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getF_ingreso() {
		return f_ingreso;
	}

	public void setF_ingreso(String f_ingreso) {
		this.f_ingreso = f_ingreso;
	}

	public String getF_salida() {
		return f_salida;
	}

	public void setF_salida(String f_salida) {
		this.f_salida = f_salida;
	}

	public String getHoras() {
		return horas;
	}

	public void setHoras(String horas) {
		this.horas = horas;
	}

	public String getId_programa() {
		return id_programa;
	}

	public void setId_programa(String id_programa) {
		this.id_programa = id_programa;
	}
	
}
