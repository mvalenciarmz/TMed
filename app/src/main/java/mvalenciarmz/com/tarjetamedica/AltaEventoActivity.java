package mvalenciarmz.com.tarjetamedica;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AltaEventoActivity extends ActionBarActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    // Para saber si estamos en un cambio (UPDATE) o en una alta (INSERT)
    private String idPersona;
    private String idServicio;
    private String fechaEvento;
    private String horaEvento;

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
    private EditText txtResultadoEvento;
    private Spinner  txtStatus;

    private TextView lblServicio;
    private TextView lblEvento;
    private TextView lblLugar;
    private TextView lblComentarios;
    private TextView lblResultadoEvento;
    private TextView lblStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_evento);

        // Obtenemos el id del servicio actual ( si es una alta, valdrá 999999 )
        TMed miApp = ( (TMed)getApplicationContext() );
        idPersona = miApp.getIdActual();
        idServicio  = miApp.getIdServicioActual();
        fechaEvento = miApp.getFechaEventoActual();
        horaEvento = miApp.getHoraEventoActual();



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
        txtResultadoEvento = (EditText) findViewById(R.id.resultadoEvento);
        txtStatus = (Spinner) findViewById(R.id.status);

        lblServicio = (TextView) findViewById(R.id.lblServicio);
        lblEvento = (TextView) findViewById(R.id.lblDescripcionEvento);
        lblLugar = (TextView) findViewById(R.id.lblLugar);
        lblComentarios = (TextView) findViewById(R.id.lblComentarios);
        lblResultadoEvento = (TextView) findViewById(R.id.lblResultadoEvento);
        lblStatus = (TextView) findViewById(R.id.lblStatus);


        // Para la interacción con la base de datos
        dbManager = new DBManager(this);
        dbManager.open();

        // Cargamos todos los servicios al combo
        cargaServicios();

        // Si trae un id Evento entonces es un cambio y hay que cargar sus datos
        if ( idServicio != "999999" ) {
            cargaDatosEvento();
            txtResultadoEvento.setVisibility(View.VISIBLE);
            txtStatus.setVisibility(View.VISIBLE);
            lblResultadoEvento.setVisibility(View.VISIBLE);
            lblStatus.setVisibility(View.VISIBLE);
        // Si es una alta, ocultamos el status (ya que en alta será PENDIENTE) y el resultado del evento (ya que aún no se produce)
        } else {
            txtResultadoEvento.setVisibility(View.GONE);
            txtStatus.setVisibility(View.GONE);
            lblResultadoEvento.setVisibility(View.GONE);
            lblStatus.setVisibility(View.GONE);
        }

        lblServicio.setTextColor(Color.GRAY);
        lblEvento.setTextColor(Color.GRAY);
        lblLugar.setTextColor(Color.GRAY);
        lblComentarios.setTextColor(Color.GRAY);
        lblResultadoEvento.setTextColor(Color.GRAY);
        lblStatus.setTextColor(Color.GRAY);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alta_evento, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        // Si presionan el botón de Eliminar Evento
        if (id == R.id.deleteEvento ) {

            new AlertDialog.Builder(this)
                .setTitle("Borrar Evento")
                .setMessage("¿ Confirmas que deseas borrar éste evento ?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dbManager.deleteEvento( idPersona, idServicio, fechaEvento, horaEvento);

                        // Refrescamos la pantalla principal
                        Intent main = new Intent(AltaEventoActivity.this, EventosActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        // Lanzamos nuevamente la pantalla principal
                        startActivity(main);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

        }



        // Si presionan el botón de Guardar Evento
        if (id == R.id.addEvento ) {

            final String servicio = txtServicio.getSelectedItem().toString();
            final String fecha = btnFecha.getText().toString();
            final String hora = btnHora.getText().toString();
            final String evento = txtEvento.getText().toString();
            final String lugar = txtLugar.getText().toString();
            final String observaciones = txtObservaciones.getText().toString();
            final String status = txtStatus.getSelectedItem().toString();
            final String resultadoEvento = txtResultadoEvento.getText().toString();

            // Validamos los campos obligatorios
            if (fecha.trim().length() == 0 || fecha.trim().matches("Fecha")) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
                dlgAlert.setMessage("La fecha del evento es obligatoria");
                dlgAlert.setTitle("FALTA INFORMACIÓN");
                dlgAlert.setPositiveButton("Aceptar", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
                return false;
            }

            if (hora.trim().length() == 0 || hora.trim().matches("Hora")) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
                dlgAlert.setMessage("La hora del evento es obligatoria");
                dlgAlert.setTitle("FALTA INFORMACIÓN");
                dlgAlert.setPositiveButton("Aceptar", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
                return false;
            }

            if (evento.trim().length() == 0) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
                dlgAlert.setMessage("Escribe al menos el nombre del evento");
                dlgAlert.setTitle("FALTA INFORMACIÓN");
                dlgAlert.setPositiveButton("Aceptar", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
                return false;
            }

            // Si idServicio = 999999 es una alta ...
            if ( idServicio == "999999" ) {
                // Insertamos
                dbManager.insertEvento(idPersona, servicio, fecha, hora, evento, lugar, observaciones);
                // Si no, es un cambio
            } else {
                dbManager.updateEvento( idPersona, idServicio, fechaEvento, horaEvento, servicio, fecha, hora, evento, lugar, observaciones, status, resultadoEvento );
            }

// Esto será para la alarma que pondremos un día antes del evento
            /*

            String nombreMes[] = {"ENE", "FEB", "MAR", "ABR", "MAY", "JUN", "JUL", "AGO", "SEP", "OCT", "NOV", "DIC"};

            int dia;
            String  mes;
            int  ano;
            int longitudFecha = fecha.length();   // mes y año siempre iguales, pero día puede ser de un dígito o dos

            if (longitudFecha == 11) {
                dia = Integer.parseInt( fecha.substring( 0, 2 ) );
                mes = fecha.substring( 3, 6 );
                ano = Integer.parseInt( fecha.substring( 7 ) );
            } else {
                dia = Integer.parseInt( fecha.substring( 0, 1 ) );
                mes = fecha.substring( 2, 5 );
                ano = Integer.parseInt(fecha.substring(6));
            }

            int numMes = Arrays.asList(nombreMes).indexOf(mes);

            // Calculamos la fecha de hoy
            Date dateHoy = new Date();

            // Obtenemos la fecha del evento
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            Date dateEvento = new Date();
            try {
                dateEvento = formatter.parse(dia+"-"+ ( numMes+1 ) +"-"+ano);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Obtenemos los días que faltan entre el día de hoy la fecha del evento
            int diasParaElEvento = ((int)((dateEvento.getTime()/(24*60*60*1000)) -(int)(dateHoy.getTime()/(24*60*60*1000))));

            // Si faltan días para el evento, lanzamos una alarma
            // ESTE USAMOS ---> http://stackoverflow.com/questions/28017943/android-how-to-set-a-notification-to-a-specific-date-in-the-future
            // solo lo dejamos por referencia : http://blog.blundell-apps.com/notification-for-a-user-chosen-time/
            if ( diasParaElEvento >= 0 ) {
                // 0 días significa que falta un día, pero como la alarma la queremos un día antes lo dejamos así
                Calendar alarmaUnDiaAntes = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, 9);
                calendar.set(Calendar.MINUTE, 48);

                alarmaUnDiaAntes.add(Calendar.DATE, diasParaElEvento);

                System.out.println( "lanzamos alarma");

                Intent intent = new Intent(this, Receiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 001, intent, 0);

                AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP, alarmaUnDiaAntes.getTimeInMillis(), pendingIntent);

            }

*/


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
        Cursor cursor = dbManager.selectEventoActual( idPersona, idServicio, fechaEvento, horaEvento );

        // Recorremos el cursor si es que existe al menos un registro
        if ( cursor.moveToFirst() ) {
            //Recorremos el cursor hasta que no haya más registros
            do {

                String[] columns = new String[] { "id", "idServicio", "nombreServicio", "fechaEvento", "horaEvento", "evento", "lugar", "observaciones", "status", "resultadoEvento" };
                String id = cursor.getString(0).trim();
                String idServicio = cursor.getString(1).trim();
                String nombreServicio = cursor.getString(2).trim();
                String fecha = cursor.getString(3).trim();
                String hora = cursor.getString(4).trim();
                String evento = cursor.getString(5).trim();
                String lugar = cursor.getString(6).trim();
                String comentario = cursor.getString(7).trim();
                String status = cursor.getString(8).trim();
                String resultadoEvento = cursor.getString(9).trim();

                //System.out.println( status );
                //System.out.println( resultadoEvento );

                String nombreMes[] = {"ENE", "FEB", "MAR", "ABR", "MAY", "JUN", "JUL", "AGO", "SEP", "OCT", "NOV", "DIC"};

                int dia;
                String  mes;
                int  ano;
                int longitudFecha = fecha.length();   // mes y año siempre iguales, pero día puede ser de un dígito o dos

                if (longitudFecha == 11) {
                    dia = Integer.parseInt( fecha.substring( 0, 2 ) );
                    mes = fecha.substring( 3, 6 );
                    ano = Integer.parseInt( fecha.substring( 7 ) );
                } else {
                    dia = Integer.parseInt( fecha.substring( 0, 1 ) );
                    mes = fecha.substring( 2, 5 );
                    ano = Integer.parseInt(fecha.substring(6));
                }

                int numMes = Arrays.asList(nombreMes).indexOf(mes);

                // Ponemos la fecha del evento en el boton y el control de calendario para que la tenga al cargar
                Calendar calendar;
                calendar = Calendar.getInstance();
                calendar.set(ano, numMes, dia);



                txtServicio.setSelection(((ArrayAdapter)txtServicio.getAdapter()).getPosition(nombreServicio));
                btnFecha.setText(fecha);
                btnHora.setText( hora );
                if ( evento != null ) { txtEvento.setText( evento ); }
                if ( lugar != null ) { txtLugar.setText( lugar ); }
                if ( comentario != null ) { txtObservaciones.setText(comentario); }

                txtStatus.setSelection(((ArrayAdapter)txtStatus.getAdapter()).getPosition(status));
/*
                switch(status) {
                    case "Cumplido":
                        txtStatus.setSelection(1);
                        break;
                    case "No se cumplió":
                        txtStatus.setSelection(2);
                        break;
                    default:
                        txtStatus.setSelection(0);
                }
*/
                if ( resultadoEvento != null ) { txtResultadoEvento.setText(resultadoEvento); }

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
