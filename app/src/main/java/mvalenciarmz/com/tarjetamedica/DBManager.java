package mvalenciarmz.com.tarjetamedica;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

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
