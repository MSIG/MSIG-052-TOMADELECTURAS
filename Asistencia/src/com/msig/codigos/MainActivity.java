package com.msig.codigos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.msig.services.HttpHandlerFunction;

public class MainActivity extends Activity implements OnClickListener {
	String tipoOperacion;
	SimpleDateFormat df1 = new SimpleDateFormat("hh:mm");
	SimpleDateFormat df2 = new SimpleDateFormat("d/MM/yy HH:mm:ss");
	SimpleDateFormat df3 = new SimpleDateFormat("kk:mm");
	SimpleDateFormat df4 = new SimpleDateFormat("dd:hh:mm");
	private String URL = "http://www.csaproduccionagricola.com/Labores/services/execute.php";
	private String function = "fn_insertar_marcaje";
	datosDB objEnviar;
	String respuestaServidor="";
	String id_programaGeneral;
	int error = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        View entrada = findViewById(R.id.btn_capturar);
        entrada.setOnClickListener(this);
        
        View verDatos = findViewById(R.id.btn_ver);
        verDatos.setOnClickListener(this);
        
        View salida = findViewById(R.id.btn_modificar);
        salida.setOnClickListener(this);
        
        View eliminar = findViewById(R.id.btn_eliminar);
        eliminar.setOnClickListener(this);
        
        View enviar = findViewById(R.id.btn_enviar);
        enviar.setOnClickListener(this);
       
    }
	@Override
	public void onClick(View v) {
		TextView idprograma = (TextView) findViewById(R.id.txt_id_programa);
		TextView idtrato = (TextView) findViewById(R.id.txtTrato);
		id_programaGeneral = idprograma.getText().toString() + "" + idtrato.getText().toString();
		if(v.getId() == findViewById(R.id.btn_capturar).getId()){
			if (id_programaGeneral.equals("00")) {
				Toast.makeText(getApplicationContext(),"Ingrese el la actividad y el trato", Toast.LENGTH_LONG).show();
			} else {
				tipoOperacion = "insert";
				Intent intent = new Intent("com.google.zxing.client.android.SCAN");
				intent.putExtra("SCAN_FORMATS","CODE_39,CODE_93,CODE_128,DATA_MATRIX,ITF,CODABAR,EAN_13,EAN_8,UPC_A,QR_CODE");
				startActivityForResult(intent, 0);
			}
		}else{
			if(v.getId() == findViewById(R.id.btn_ver).getId()){
				Intent verDatos = new Intent(this, Ver.class);
				startActivity(verDatos);
			}else{
				if(v.getId() == findViewById(R.id.btn_modificar).getId()){
					if(id_programaGeneral.equals("00")){
						Toast.makeText(getApplicationContext(), "Ingrese el la actividad y el trato", Toast.LENGTH_LONG).show();
					} else {
						tipoOperacion = "update";
						Intent intent = new Intent("com.google.zxing.client.android.SCAN");
						intent.putExtra("SCAN_FORMATS", "CODE_39,CODE_93,CODE_128,DATA_MATRIX,ITF,CODABAR,EAN_13,EAN_8,UPC_A,QR_CODE");					
						startActivityForResult(intent, 0);
					}
				}else{
					if(v.getId() == findViewById(R.id.btn_eliminar).getId()){
						confirmarEliminar();
					}else{
						if(v.getId() == findViewById(R.id.btn_enviar).getId()){
							confirmarEnviar();
						}
					}
				}
			}
		}
		// TODO Auto-generated method stub
	}
	/**
	 * Security check for elimination
	 */
	public void confirmarEliminar(){
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		dlg.setTitle("Importante");
		dlg.setMessage("Eliminar la base de datos local? ");
		dlg.setCancelable(true);
		dlg.setNegativeButton("Cancelar",new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dlg, int id) {
				dlg.cancel();
			}
		});
	
		dlg.setPositiveButton("Confirmar",new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dlg, int id) {
				eliminarLocal();
			}
		});
		dlg.show();
	}
	/**
	 * Security check for elimination
	 */
	public void confirmarEnviar(){
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		dlg.setTitle("Importante");
		dlg.setMessage("Esta seguro de almacenar esta información? no se enviarán" +
				" los datos que no tengan marcada la salida");
		dlg.setCancelable(true);
		dlg.setNegativeButton("Cancelar",new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dlg, int id) {
				dlg.cancel();
			}
		});
	
		dlg.setPositiveButton("Confirmar",new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dlg, int id) {
				enviarServidor();
			}
		});
		dlg.show();
	}
	/**
	 * Delete data local
	 */
	public void eliminarLocal(){
		try {
			AdminDb admin = new AdminDb(this, "codigos", null, 1);
			SQLiteDatabase bd=admin.getWritableDatabase();			   
			int cantidad = bd.delete("reg_personas", null,null);
		    bd.close();
		} catch (Exception e) {
		}		
	}
	/**
	 * Result Driver Scanner
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
	       	 if (resultCode == RESULT_OK) {
	       		String codReaded = data.getStringExtra("SCAN_RESULT");
	       		if("insert".equals(tipoOperacion)){
	       			guardar(codReaded);
	       		}else{
	       			if("update".equals(tipoOperacion)){
	       				modificarLocal(codReaded,id_programaGeneral);
	       			}
	       		}
	       	 }else{	       		
       			System.out.println("tipo de oparacion "+ tipoOperacion);
	       	 }
       	}
	}
	/**
	 * Update data base local
	 * @param codigo
	 */
	public void modificarLocal(String codigo, String id_programa){
		try {
			AdminDb admin = new AdminDb(this, "codigos", null, 1);
			SQLiteDatabase bd = admin.getWritableDatabase();
			String[] args = new String[]{codigo, id_programa};
			Cursor fila = bd.rawQuery("select f_ingreso from reg_personas where codigo = ? and id_programa = ? and horas = '0'",args);
			if(fila.getCount() > 0) {
				String obj = null;
				if (fila.moveToFirst()) {
					do {
						obj = new String(fila.getString(0));
					} while (fila.moveToNext());
				}
				try {
					Date ingreso = df2.parse(obj);
					Date actual = new Date();
					long tiempo = ((actual.getTime() - ingreso.getTime()));
					long minutostotales = tiempo / 60000;
					long horas = minutostotales / 60;
					long restominutos = minutostotales%60;
					String horastr= horas+":"+restominutos;
					String hora_S = String.valueOf(horas);
					String min_S = String.valueOf(restominutos);
					if(horas <= 9){
						hora_S = "0"+ horas;
					}
					if(restominutos <= 9){
						min_S = "0"+ restominutos;
					}
					horastr = hora_S+":"+min_S;
					
					ContentValues valores = new ContentValues();
					valores.put("f_salida", df2.format(new Date()));
					valores.put("horas", horastr);
					//valores.put("horas", String.valueOf(hora+":"+minuto));					
					String[] args2 = new String[]{codigo};
					bd.update("reg_personas",valores,"codigo = ?", args2);					
				} catch (ParseException e) {
					System.out.println("error ********** "+ e);
					e.printStackTrace();
				}				
				bd.close();
			}else{
				Toast.makeText(getApplicationContext(), "Codigo no registrado : "+codigo, Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Erro al modificar : "+codigo, Toast.LENGTH_LONG).show();
		}		
	}
	/**
	 * Saving data base local
	 * @param dato
	 */
	public void guardar(String dato){
		try {
			datosDB obj = new datosDB();
			obj.setCodigo(dato);
			obj.setF_ingreso(df2.format(new Date()));
			obj.setF_salida(null);
			obj.setHoras("0");
			
			AdminDb admin = new AdminDb(this, "codigos", null, 1);
			SQLiteDatabase bd = admin.getWritableDatabase();
			ContentValues registro = new ContentValues();			
			registro.put("codigo",obj.getCodigo());
			registro.put("id_programa", id_programaGeneral);
			registro.put("f_ingreso", obj.getF_ingreso());
			registro.put("f_salida",obj.getF_salida());
			registro.put("horas",obj.getHoras());
			bd.insert("reg_personas", null, registro);
			bd.close();
		} catch (Exception e) {
			System.out.println("ERROR : "+e);
		}
	}
	/**
	 * Send to the server
	 */
	public void enviarServidor(){
		AdminDb admin = new AdminDb(this,"codigos", null, 1);
		SQLiteDatabase bd = admin.getWritableDatabase();
		Cursor fila = bd.rawQuery("select codigo, id_programa, f_ingreso, f_salida, horas from reg_personas order by codigo",null);
		Integer numero_datos = fila.getCount();
		if (numero_datos > 0) {
			if (fila.moveToFirst()) {
				do {
					datosDB o;
					o = new datosDB(fila.getString(0), fila.getString(1), fila.getString(2), fila.getString(3),fila.getString(4));
					enviar(o);
					o = null;
				} while (fila.moveToNext());
				if(error == 0){
					Toast.makeText(getApplicationContext(),"Enviados Satisfactoriamente!", Toast.LENGTH_LONG).show();
				}
				error = 0;
			}			
		}else{
			Toast msg = Toast.makeText(getApplicationContext(), "No hay datos registrados", Toast.LENGTH_SHORT);
			msg.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
			msg.show();
		}
	}
	
	public void eliminarEnviado(String codigo, String id_programa) {
		try {
			AdminDb admin = new AdminDb(this, "codigos", null, 1);
			SQLiteDatabase bd=admin.getWritableDatabase();
			bd.execSQL("delete from reg_personas where codigo = '"+codigo+"' and id_programa = '"+id_programa+"'");
			bd.close();
			//Toast.makeText(getApplicationContext(), "Registro Eliminado "+ codigo, Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "ERROR "+e, Toast.LENGTH_LONG).show();
		}		
	}
	
	
	public void enviar(datosDB objEnviar){
		Toast.makeText(getApplicationContext(), objEnviar.getHoras(), Toast.LENGTH_LONG);
		//se quito la condicion de salida para tomar solo la entrada de cada persona
		//if(objEnviar.getF_salida() != null && !objEnviar.getHoras().equals("0")){
			try {
				String values = objEnviar.getId_programa()+",'"+objEnviar.getCodigo()+"','"+objEnviar.getF_ingreso()+"','"+objEnviar.getF_salida()+"','"+objEnviar.getHoras()+"'";
				try {
					HttpHandlerFunction service = new HttpHandlerFunction();
					String response = service.post(URL, function, values);
					String respuesta_servidor = response.trim();
					if(respuesta_servidor.equals("OK")){
						eliminarEnviado(objEnviar.getCodigo(), objEnviar.getId_programa());
					} else {
						error = 1;
						Toast.makeText(getApplicationContext(),"Error: "+ response, Toast.LENGTH_LONG).show();
					}										
				} catch (Exception error) {
					Toast msg = Toast.makeText(getApplicationContext(), "Error al enviar datos", Toast.LENGTH_SHORT);
					msg.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
					msg.show();
				}
			} catch (Exception error) {
				Toast msg = Toast.makeText(getApplicationContext(), "Error al ejecutar web service", Toast.LENGTH_SHORT);
				msg.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
				msg.show();
			}
		//}else{
			//error = 1;
		//}
	}
	// Override method **********************************************
	
	@Override
    protected void onSaveInstanceState(Bundle outState){
    	super.onSaveInstanceState(outState);
    	outState.putString("OPERACION", tipoOperacion);
    	outState.putString("PROGRAMA", id_programaGeneral);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
    	super.onRestoreInstanceState(savedInstanceState);
    	tipoOperacion = savedInstanceState.getString("OPERACION");
    	id_programaGeneral = savedInstanceState.getString("PROGRAMA");
    	TextView idprograma = (TextView) findViewById(R.id.txt_id_programa);
    	idprograma.setText(id_programaGeneral);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
