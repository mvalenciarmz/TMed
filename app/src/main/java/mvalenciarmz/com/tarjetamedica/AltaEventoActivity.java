package mvalenciarmz.com.tarjetamedica;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class AltaEventoActivity extends ActionBarActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    // Para saber si estamos en un cambio (UPDATE) o en una alta (INSERT)
    private String idEvento;

    // Para el control que permite seleccionar una fecha
    private Calendar calendar;
    private DateFormat dateFormat;
    private Button btnFecha;

    // Formato de la hora
    private static final String TIME_PATTERN = "HH:mm";
    private SimpleDateFormat timeFormat;
    private Button btnHora;

    // Para la interacción con la base de datos
    private DBManager dbManager;

    // Para el spinner de servicios
    Spinner spnServicios;

    // Para obtener el valor de cada campo
    private Spinner  txtServicio;
    private EditText txtEvento;
    private EditText txtLugar;
    private EditText txtObservaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_evento);

        // Obtenemos el id del evento actual ( si es una alta, valdrá 999999 )
        TMed miApp = ( (TMed)getApplicationContext() );
        idEvento  = miApp.getIdEventoActual();


        // Para el control que permite seleccionar una fecha
        calendar = Calendar.getInstance();
        dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());

        // Para el control de la hora
        timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());

        // Ubicamos el spinner de servicios en la forma
        spnServicios = (Spinner) findViewById(R.id.servicio);

        // Obtenemos la información de cada campo en la forma
        txtServicio = (Spinner) findViewById(R.id.servicio);
        btnFecha    = (Button) findViewById(R.id.fecha);
        btnHora     = (Button) findViewById(R.id.hora);
        txtEvento   = (EditText) findViewById(R.id.evento);
        txtLugar    = (EditText) findViewById(R.id.lugar);
        txtObservaciones = (EditText) findViewById(R.id.observaciones);

        // Para la interacción con la base de datos
        dbManager = new DBManager(this);
        dbManager.open();

        // Cargamos todos los servicios al combo
        cargaServicios();

        // Si trae un id Evento entonces es un cambio y hay que cargar sus datos
        if ( idEvento != 999999 ) {
            cargaDatosEvento();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alta_evento, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Obtenemos el id actual usando variables globales
        TMed miApp = ( (TMed)getApplicationContext() );

        // Y lo asignamos a una validable
        String idPersona = miApp.getIdActual();

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        // Si presionan el botón de Guardar Persona
        if (id == R.id.addEvento ) {

            final String servicio = txtServicio.getSelectedItem().toString();
            final String fecha    = btnFecha.getText().toString();
            final String hora     = btnHora.getText().toString();
            final String evento   = txtEvento.getText().toString();
            final String lugar    = txtLugar.getText().toString();
            final String observaciones = txtObservaciones.getText().toString();

            // Validamos los campos obligatorios
            if ( fecha.trim().length() == 0 || fecha.trim().matches("Fecha") ) {
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
                dlgAlert.setMessage("La fecha del evento es obligatoria");
                dlgAlert.setTitle("FALTA INFORMACIÓN");
                dlgAlert.setPositiveButton("Aceptar", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
                return false;
            }

            if ( hora.trim().length() == 0 || hora.trim().matches("Hora") ) {
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
                dlgAlert.setMessage("La hora del evento es obligatoria");
                dlgAlert.setTitle("FALTA INFORMACIÓN");
                dlgAlert.setPositiveButton("Aceptar", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
                return false;
            }

            if ( evento.trim().length() == 0 ) {
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
                dlgAlert.setMessage("Escribe al menos el nombre del evento");
                dlgAlert.setTitle("FALTA INFORMACIÓN");
                dlgAlert.setPositiveButton("Aceptar", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
                return false;
            }

            // Insertamos
            dbManager.insertEvento( idPersona, servicio, fecha, hora, evento, lugar, observaciones );

            // Refrescamos la pantalla principal
            Intent main = new Intent(AltaEventoActivity.this, EventosActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Lanzamos nuevamente la pantalla principal
            startActivity(main);

        }

        return super.onOptionsItemSelected(item);


    }

    // Cargamos la tabla de servicios al combo (spiner)
    private void cargaServicios() {

        // Cargamos la tabla de servicios a una lista
        List<String> servicios = dbManager.dameTodosServicios();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item , servicios);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spnServicios.setAdapter(dataAdapter);

    }


    // Cargamos el registro del evento actual, desde la tabla eventos
    private void cargaDatosEvento() {

        dbManager = new DBManager(this);
        dbManager.open();
        Cursor cursor = dbManager.selectEventoActual( idEvento );

        // Recorremos el cursor si es que existe al menos un registro
        if ( cursor.moveToFirst() ) {
            //Recorremos el cursor hasta que no haya más registros
            do {

                String[] columns = new String[] { "id", "idServicio", "nombreServicio", "fechaEvento", "horaEvento", "evento" };

                String id = cursor.getString(0).trim();
                String idServicio = cursor.getString(1).trim();
                String nombreServicio = cursor.getString(2).trim();
                String fecha = cursor.getString(3).trim();
                String hora = cursor.getString(4).trim();
                String evento = cursor.getString(5).trim();

                //tableViewEventos.addBasicItem( evento, nombreServicio);
                tableViewEventos.addBasicItem( fecha + " -> " + hora, nombreServicio);

                // Agregamos al array la lista de IDs para poder identificar el que seleccionen
                idEventoArrayList.add( id );

            } while( cursor.moveToNext() );
        }

    }



    // Cuando presionen el botón, mostramos el control que les permite seleccionar una fecha
    public void btnFecha(View button) {
        DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show(getFragmentManager(), "datePicker");
    }
    // Cuando presionen el botón, mostramos el control que les permite seleccionar una hora
    public void btnHora(View button) {
        TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show(getFragmentManager(), "timePicker");
    }

    // Cuando seleccionen una fecha, la dibujamos como texto del botón
    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {

        String nombreMes[] = {"ENE", "FEB", "MAR", "ABR", "MAY", "JUN", "JUL", "AGO", "SEP", "OCT", "NOV", "DIC"};

        btnFecha = (Button) findViewById(R.id.fecha);

        calendar.set(year, monthOfYear, dayOfMonth);
        //btnFecha.setText( dateFormat.format( calendar.getTime() ) );
        btnFecha.setText( dayOfMonth + "/" + nombreMes[monthOfYear] + "/" + year );

    }

    // Cuando seleccionen la hora, la dibujamos como texto del botón
    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {

        btnHora = (Button) findViewById(R.id.hora);

        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        // No encontré la forma "correcta" de poner en el formato AM PM
        int am_pm = calendar.get(Calendar.AM_PM);
        String amOrpm=( ( am_pm == Calendar.AM ) ? " AM" : " PM" );

        btnHora.setText( timeFormat.format( calendar.getTime() ).toString() + amOrpm );

    }



}
