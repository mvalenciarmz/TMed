package mvalenciarmz.com.tarjetamedica;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Nombre de la base de datos
    static final String DB_NAME = "MVALENCIARMZ_TMED.DB";

    // Versión de la base de datos
    static final int DB_VERSION = 1;

    // Consultas para crear las tablas
    private static final String CREATE_TABLE_PERSONAS = "CREATE TABLE personas( " +
            "id                 INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nombre             VARCHAR(20), " +
            "paterno            VARCHAR(20), " +
            "materno            VARCHAR(20), " +
            "sexo               CHAR(1), " +
            "numSegSocial       VARCHAR(25), " +
            "unidadMedica       VARCHAR(10), " +
            "horario            VARCHAR(10), " +
            "consultorio        VARCHAR(10), " +
            "fechaNacimiento    DATE, " +
            "curp               VARCHAR(25), " +
            "tipoSangre         VARCHAR(10), " +
            "observaciones      TEXT);";

    private static final String CREATE_TABLE_SERVICIOS = "CREATE TABLE servicios( " +
            "idServicio         INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nombreServicio     VARCHAR(30) );";


    private static final String CREATE_TABLE_EVENTOS = "CREATE TABLE eventos( " +
            "id                 INTEGER NOT NULL, " +
            "idServicio         INTEGER NOT NULL, " +
            "fechaEvento        DATE NOT NULL, " +
            "horaEvento         TIME NOT NULL, " +
            "evento             VARCHAR(30), " +
            "resultadoEvento    VARCHAR(30), " +
            "lugar              VARCHAR(50), " +
            "status             VARCHAR(10), " +
            "comentario         TEXT, " +
            "PRIMARY KEY (id, idServicio, fechaEvento, horaEvento) );";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    // En el onCreate creamos la tabla - sólo la crea si no existe -
    public void onCreate(SQLiteDatabase db) {

        db.execSQL( CREATE_TABLE_PERSONAS );
        System.out.println("creamos personas");
        db.execSQL( CREATE_TABLE_SERVICIOS );
        System.out.println("creamos servicios");
        db.execSQL( CREATE_TABLE_EVENTOS );
        System.out.println("creamos eventos");

        // Insertamos los servicios que registraremos
        db.execSQL("INSERT INTO servicios( nombreServicio ) VALUES( 'CITAS' )" );
        db.execSQL("INSERT INTO servicios( nombreServicio ) VALUES( 'VACUNAS' )" );
        db.execSQL("INSERT INTO servicios( nombreServicio ) VALUES( 'PREVENCIÓN' )" );   // Identificación Oportuna de Enfermedades

        db.execSQL("INSERT INTO servicios( nombreServicio ) VALUES( 'GLUCOSA' )" );
        db.execSQL("INSERT INTO servicios( nombreServicio ) VALUES( 'PRESIÓN ARTERIAL' )" );

        db.execSQL("INSERT INTO servicios( nombreServicio ) VALUES( 'ESTATURA' )" );
        db.execSQL("INSERT INTO servicios( nombreServicio ) VALUES( 'PESO' )" );
        db.execSQL("INSERT INTO servicios( nombreServicio ) VALUES( 'CINTURA' )" );

        //System.out.println("Entramos a crear la base de datos");

        /*
        // Datos de prueba al iniciar el proyecto
        db.execSQL("INSERT INTO personas( nombre, paterno, materno, numSegSocial, unidadMedica, horario, consultorio, fechaNacimiento, curp, tipoSangre, observaciones ) VALUES( 'MARCOS ANTONIO', 'VALENCIA', 'RAMÍREZ', 'NUM-SEG-SOCIAL', '35', 'MATUTINO', '9', '10/04/1973', 'AQUI-VA-LA-CURP', 'ORH+', 'GUAPO EL MUCHACHO :D' )" );
        db.execSQL("INSERT INTO personas( nombre, paterno, materno, numSegSocial, unidadMedica, horario, consultorio, fechaNacimiento, curp, tipoSangre, observaciones ) VALUES( 'SARA', 'GARCIA', 'MARTÍNEZ', 'NUM-SEG-SOCIAL', '35', 'MATUTINO', '9', '10/04/1973', 'AQUI-VA-LA-CURP', 'ORH+', 'GUAPO EL MUCHACHO :D' )" );
        db.execSQL("INSERT INTO personas( nombre, paterno, materno, numSegSocial, unidadMedica, horario, consultorio, fechaNacimiento, curp, tipoSangre, observaciones ) VALUES( 'VICTOR GABRIEL', 'VALENCIA', 'GARCIA', 'NUM-SEG-SOCIAL', '35', 'MATUTINO', '9', '10/04/1973', 'AQUI-VA-LA-CURP', 'ORH+', 'GUAPO EL MUCHACHO :D' )" );
        db.execSQL("INSERT INTO personas( nombre, paterno, materno, numSegSocial, unidadMedica, horario, consultorio, fechaNacimiento, curp, tipoSangre, observaciones ) VALUES( 'JOSE ALBERTO', 'VALENCIA', 'GARCIA', 'NUM-SEG-SOCIAL', '35', 'MATUTINO', '9', '10/04/1973', 'AQUI-VA-LA-CURP', 'ORH+', 'GUAPO EL MUCHACHO :D' )" );
        db.execSQL("INSERT INTO personas( nombre, paterno, materno, numSegSocial, unidadMedica, horario, consultorio, fechaNacimiento, curp, tipoSangre, observaciones ) VALUES( 'ERIKA VANESA', 'VALENCIA', 'GARCIA', 'NUM-SEG-SOCIAL', '35', 'MATUTINO', '9', '10/04/1973', 'AQUI-VA-LA-CURP', 'ORH+', 'GUAPO EL MUCHACHO :D' )" );
        db.execSQL("INSERT INTO personas( nombre, paterno, materno, numSegSocial, unidadMedica, horario, consultorio, fechaNacimiento, curp, tipoSangre, observaciones ) VALUES( 'CARLOS DANIEL', 'VALENCIA', 'GARCIA', 'NUM-SEG-SOCIAL', '35', 'MATUTINO', '9', '10/04/1973', 'AQUI-VA-LA-CURP', 'ORH+', 'GUAPO EL MUCHACHO :D' )" );
        */

    }

    @Override
    // Este método sospecho es para actualizaciones, habría que ver en qué casos y cómo se utiliza
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS personas");
        onCreate(db);
    }




}