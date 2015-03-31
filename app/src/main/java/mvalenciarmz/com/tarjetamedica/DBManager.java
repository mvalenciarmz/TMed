package mvalenciarmz.com.tarjetamedica;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/*
En ésta clase pondremos todos los métodos que usaremos para interactuar con la base de datos
*/
public class DBManager {

    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    // Para abrir la base de datos
    public DBManager open() throws SQLException {

//        System.out.println("Entramos ya a pedir el select");

        //context.deleteDatabase("MVALENCIARMZ_TMED.DB");

        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    // Para cerrar la base de datos
    public void close() {
        dbHelper.close();
    }

    /*
    **************************************************************************************************************************
    **************************************************************************************************************************
    * PERSONAS *
    **************************************************************************************************************************
    **************************************************************************************************************************
     */

    // Para insertar una nueva persona en la base de datos
    public void insertPersona(String nombre, String paterno, String materno, String sexo, String numSegSocial, String unidadMedica, String horario, String consultorio, String fechaNacimiento, String curp, String tipoSangre, String observaciones ) {

        ContentValues contentValue = new ContentValues();

        // Asignamos a los campos su valor
        contentValue.put( "nombre", nombre );
        contentValue.put( "paterno", paterno );
        contentValue.put( "materno", materno );
        contentValue.put( "sexo", sexo );
        contentValue.put( "numSegSocial", numSegSocial );
        contentValue.put( "unidadMedica", unidadMedica );
        contentValue.put( "horario", horario );
        contentValue.put( "consultorio", consultorio );
        contentValue.put( "fechaNacimiento", fechaNacimiento );
        contentValue.put( "curp", curp );
        contentValue.put( "tipoSangre", tipoSangre );
        contentValue.put( "observaciones", observaciones );

        // Insertamos
        database.insert( "personas", null, contentValue);

    }

    // Obtenemos todas las personas registradas
    public Cursor selectPersonas() {

        String[] columns = new String[] { "id", "nombre", "paterno", "materno", "sexo", "numSegSocial", "unidadMedica", "horario", "consultorio","fechaNacimiento", "curp", "tipoSangre", "observaciones" };
        Cursor cursor = database.query("personas", columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }


    // Para insertar un nuevo evento en la base de datos
    public void insertEvento(String id, String servicio, String fecha, String hora, String evento, String lugar, String observaciones ) {

        int idServicio;

        // Obtenemos el id del servicio en base a su descripcion
        idServicio = dameIdServicio( servicio );

        ContentValues contentValue = new ContentValues();

        // Asignamos a los campos su valor
        contentValue.put( "id", id );
        contentValue.put( "idServicio", idServicio );
        contentValue.put( "fechaEvento", fecha );
        contentValue.put( "horaEvento", hora );
        contentValue.put( "evento", evento );
        contentValue.put( "lugar", lugar );
        contentValue.put( "status", "PENDIENTE");
        contentValue.put( "comentario", observaciones );

        // Insertamos
        database.insert( "eventos", null, contentValue);

    }


    // Regresamos el id de un servicio basándonos en su nombre
    public int dameIdServicio( String nombre ) {

        int nid = 0;

        Cursor cursor = database.rawQuery("SELECT idServicio FROM servicios WHERE TRIM( nombreServicio ) = ?", new String[] {nombre});
        if( cursor.moveToFirst() ){
            do {
                nid = cursor.getInt(0);
            } while( cursor.moveToNext() );
        }
        return nid;
    }




    // Regresamos el catálogo de servicios para rellenar un combo (spiner)
    public List<String> dameTodosServicios(){

        List<String> labels = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT nombreServicio FROM servicios ORDER BY nombreServicio;";

        Cursor cursor = database.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // Cerramos la conexión
        cursor.close();

        // regresamos los servicios enformato de lista
        return labels;

    }


    // Obtenemos todos los eventos que tengan un status PENDIENTE
    public Cursor selectEventos( String idPersona ) {

        //String[] columns = new String[] { "id", "idServicio", "nombreServicio", "fechaEvento", "horaEvento", "evento" };
        //Cursor cursor = database.query("eventos", columns, null, null, null, null, null);
        Cursor cursor = database.rawQuery("SELECT id, idServicio, nombreServicio, fechaEvento, horaEvento, evento FROM eventos INNER JOIN servicios USING( idServicio ) WHERE id = ?", new String[] {idPersona});
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    // Obtenemos todo el registro de un evento dado por su id
    public Cursor selectEventoActual( String idEvento ) {

        //String[] columns = new String[] { "id", "idServicio", "nombreServicio", "fechaEvento", "horaEvento", "evento" };
        //Cursor cursor = database.query("eventos", columns, null, null, null, null, null);
        Cursor cursor = database.rawQuery("SELECT id, idServicio, nombreServicio, fechaEvento, horaEvento, evento FROM eventos INNER JOIN servicios USING( idServicio ) WHERE id = ?", new String[] {idPersona});
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }



    // Regresamos la información de una persona, por su nombre
    public int existePersona( String nombre ) {

        int nVeces = 0;

        Cursor cursor = database.rawQuery("SELECT COUNT(*) AS total FROM personas WHERE TRIM( nombre ) = ?", new String[] {nombre});
        if( cursor.moveToFirst() ){
            do {
                nVeces = cursor.getInt(0);
            } while( cursor.moveToNext() );
        }
        return nVeces;
    }


    // Actualizamos un registro en la tabla personas
    public int updatePersonas(int id, String nombre, String paterno, String materno, String sexo, String numSegSocial, String unidadMedica, String horario, String consultorio, String fechaNacimiento, String curp, String tipoSangre, String observaciones ) {
        ContentValues contentValues = new ContentValues();

        // Asignamos a los campos su valor
        contentValues.put( "nombre", nombre );
        contentValues.put( "paterno", paterno );
        contentValues.put( "materno", materno );
        contentValues.put( "sexo", sexo );
        contentValues.put( "numSegSocial", numSegSocial );
        contentValues.put( "unidadMedica", unidadMedica );
        contentValues.put( "horario", horario );
        contentValues.put( "consultorio", consultorio );
        contentValues.put( "fechaNacimiento", fechaNacimiento );
        contentValues.put( "curp", curp );
        contentValues.put( "tipoSangre", tipoSangre );
        contentValues.put( "observaciones", observaciones );

        // Actualizamos
        int i = database.update("personas", contentValues, "id = " + id, null);
        System.out.println( i );   // Nomás para saber qué regresa i
        return i;

    }

    // Eliminamos una persona de la tabla
    public void deletePersonas(long id) {
        database.delete( "personas", "id =" + id, null);
    }


}
