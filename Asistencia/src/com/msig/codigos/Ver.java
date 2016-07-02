package com.msig.codigos;


import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.Gravity;
import android.view.Menu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class Ver extends Activity{
	TableLayout table;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ver);
		table = (TableLayout)findViewById(R.id.table_salida);	
		mostrarDatos();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ver, menu);
		return true;
	}
	
	/**
	 * **************************************************************
	 */
	public void mostrarDatos(){
		AdminDb admin = new AdminDb(this,"codigos", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();
		Cursor fila = bd.rawQuery("select codigo,id_programa, f_ingreso, f_salida, horas from reg_personas order by codigo",null);
		Integer numero_datos = fila.getCount();
		table.removeAllViews();
		if (numero_datos > 0) {
			if (fila.moveToFirst()) {
				do {
					datosDB obj;
					obj = new datosDB(fila.getString(0), fila.getString(1), fila.getString(2), fila.getString(3),fila.getString(4));				
					mostrarRegistros(obj);
				} while (fila.moveToNext());
			}
			
		}else{
			Toast msg = Toast.makeText(getApplicationContext(), "No hay datos guardados", Toast.LENGTH_SHORT);
			msg.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
			msg.show();
		}
	}

	private void mostrarRegistros(datosDB obj){
		TableRow currentRow = new TableRow(getBaseContext());
		TableLayout.LayoutParams params = new TableLayout.LayoutParams(70, TableLayout.LayoutParams.WRAP_CONTENT); 
		
		TextView programa = new TextView(getBaseContext());
		programa.setText(obj.getId_programa());
		programa.setTextSize(8);
		programa.setTextColor(Color.BLACK);
		programa.setPadding(5,5,5,5);
		
		
		TextView codigo = new TextView(getBaseContext());
		codigo.setText(obj.getCodigo());
		codigo.setTextSize(8);
		codigo.setTextColor(Color.BLACK);
		codigo.setPadding(5,5,5,5);
		
		TextView hora_inicial = new TextView(getBaseContext());
		hora_inicial.setText(obj.getF_ingreso());
		hora_inicial.setTextSize(8);
		hora_inicial.setTextColor(Color.BLACK);
		hora_inicial.setPadding(5,5,5,5);
		
		TextView hora_final = new TextView(getBaseContext());
		hora_final.setText(obj.getF_salida());
		hora_final.setTextSize(8);
		hora_final.setTextColor(Color.BLACK);
		hora_final.setPadding(5,5,5,5);
		
		TextView totalh = new TextView(getBaseContext());
		totalh.setText(obj.getHoras());
		totalh.setTextSize(8);
		totalh.setTextColor(Color.BLACK);
		totalh.setPadding(5,5,5,5);
		
		currentRow.setLayoutParams(params);
		currentRow.addView(programa);
		currentRow.addView(codigo);
		currentRow.addView(hora_inicial);
		currentRow.addView(hora_final);
		currentRow.addView(totalh);
		table.addView(currentRow);
	}
}
