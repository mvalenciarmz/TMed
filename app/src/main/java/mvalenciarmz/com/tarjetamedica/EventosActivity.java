package mvalenciarmz.com.tarjetamedica;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import br.com.dina.ui.widget.UITableView;


public class EventosActivity extends ActionBarActivity {

    // Para el manejo de la base de datos ...
    private DBManager dbManager;

    // Para mantener un array con los datos que se están mostrando en la tabla
    private ArrayList<String> idServicioArrayList = new ArrayList<String>();
    private ArrayList<String> fechaEventoArrayList = new ArrayList<String>();
    private ArrayList<String> horaEventoArrayList = new ArrayList<String>();

    // Definimos el tableview
    UITableView tableViewEventos ;

    String idPersona;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventos);

        // Creamos el tableview
        tableViewEventos = (UITableView) findViewById(R.id.tableViewEventos );
        // Rellenamos con datos
        createList("Pendiente", "");   // Por default pondremos sólo los eventos pendientes, y todos los servicios
        // Lo mostramos
        tableViewEventos .commit();

        // Obtenemos el nombre actual usando variables globales
        TMed miApp = ( (TMed)getApplicationContext() );

        // Y lo asignamos al título
        setTitle( miApp.getNombreActual() );

    }

    // Obtenemos de la base de datos los eventos y los mostramos en la tabla
    // Por default, mostraremos sólo aquellos eventos con status PENDIENTE
    private void createList( String statusMostrar, String servicioMostrar ) {

        // Obtenemos el nombre actual usando variables globales
        TMed miApp = ( (TMed)getApplicationContext() );
        idPersona = miApp.getIdActual();

        CustomClickListener listener = new CustomClickListener();
        tableViewEventos.setClickListener(listener);

        dbManager = new DBManager(this);
        dbManager.open();
        Cursor cursor = dbManager.selectEventos( idPersona, statusMostrar, servicioMostrar );

        // Recorremos el cursor si es que existe al menos un registro
        if ( cursor.moveToFirst() ) {
            //Recorremos el cursor hasta que no haya más registros
            do {

                String[] columns = new String[] { "id", "idServicio", "nombreServicio", "fechaEvento", "horaEvento", "evento", "status" };

                String id = cursor.getString(0).trim();
                String idServicio = cursor.getString(1).trim();
                String nombreServicio = cursor.getString(2).trim();
                String fecha = cursor.getString(3).trim();
                String hora = cursor.getString(4).trim();
                String evento = cursor.getString(5).trim();
                String status = cursor.getString(6).trim();

                //tableViewEventos.addBasicItem( evento, nombreServicio);
                switch ( status ) {
                    case "Cumplido":
                        tableViewEventos.addBasicItem( R.drawable.ok, evento, nombreServicio + " " + fecha + " " + hora);
                        break;
                    case "No se cumplió":
                        tableViewEventos.addBasicItem( R.drawable.nocumplido, evento, nombreServicio + " " + fecha + " " + hora);
                        break;
                    default:
                        tableViewEventos.addBasicItem( R.drawable.pendiente, evento, nombreServicio + " " + fecha + " " + hora);
                        break;
                }


                // Agregamos al array la lista de IDs para poder identificar el que seleccionen
                idServicioArrayList.add( idServicio );
                fechaEventoArrayList.add( fecha );
                horaEventoArrayList.add( hora );

            } while( cursor.moveToNext() );
        }

    }



    // Esto es para capturar el click de un elemento de la lista
    private class CustomClickListener implements UITableView.ClickListener {

        @Override
        public void onClick(int index) {

            // Manejo de variables globales:
            TMed miApp = ( (TMed)getApplicationContext() );
            // Para mantener el ID del evento que están seleccionando
            miApp.setIdServicioActual( idServicioArrayList.get(index) );
            miApp.setFechaEventoActual( fechaEventoArrayList.get(index) );
            miApp.setHoraEventoActual( horaEventoArrayList.get(index) );

            // Lanzamos la actividad
            Intent i = new Intent(EventosActivity.this, AltaEventoActivity.class);
            startActivity(i);

        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_eventos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Si quieren modificar la información de la persona
        if ( id == R.id.editarPersona ) {

            Intent i = new Intent(EventosActivity.this, AltaPersonaActivity.class);
            startActivity(i);

        }

        // Si quieren adicionar un nuevo evento
        if (id == R.id.addEvento ) {

            // Manejo de variables globales:
            TMed miApp = ( (TMed)getApplicationContext() );
            // En una alta, éste valor será 999999 ( asumimos que no llegarán a registrar dicho número de movimientos )
            miApp.setIdServicioActual("999999");

            // Lanzamos la actividad
            Intent i = new Intent(EventosActivity.this, AltaEventoActivity.class);
            startActivity(i);
        }

        // Mostrar sólo los eventos pendientes
        if ( id == R.id.mostrarEventosPendientes ) {

            tableViewEventos.clear();
            createList("Pendiente", "");
            tableViewEventos.commit();

        }
        // Mostrar sólo los eventos cumplidos
        if ( id == R.id.mostrarEventosCumplidos ) {

            tableViewEventos.clear();
            createList("Cumplido", "");
            tableViewEventos.commit();

        }
        // Mostrar sólo los eventos No cumplidos
        if ( id == R.id.mostrarEventosNoCumplidos ) {

            tableViewEventos.clear();
            createList("No se cumplió", "");
            tableViewEventos.commit();

        }
        // Mostrar todos los eventos
        if ( id == R.id.mostrarEventosTodos ) {

            tableViewEventos.clear();
            createList("", "");
            tableViewEventos.commit();

        }

        // Filtrar por tipo de servicio
        if ( id == R.id.cintura ) { tableViewEventos.clear();   createList("", "CINTURA");   tableViewEventos.commit(); }
        if ( id == R.id.citas ) { tableViewEventos.clear();   createList("", "CITAS");   tableViewEventos.commit(); }
        if ( id == R.id.estatura ) { tableViewEventos.clear();   createList("", "ESTATURA");   tableViewEventos.commit(); }
        if ( id == R.id.glucosa ) { tableViewEventos.clear();   createList("", "GLUCOSA");   tableViewEventos.commit(); }
        if ( id == R.id.peso ) { tableViewEventos.clear();   createList("", "PESO");   tableViewEventos.commit(); }
        if ( id == R.id.presionArterial ) { tableViewEventos.clear();   createList("", "PRESIÓN ARTERIAL");   tableViewEventos.commit(); }
        if ( id == R.id.prevencion ) { tableViewEventos.clear();   createList("", "PREVENCIÓN");   tableViewEventos.commit(); }
        if ( id == R.id.vacunas ) { tableViewEventos.clear();   createList("", "VACUNAS");   tableViewEventos.commit(); }
        if ( id == R.id.todosServicios ) { tableViewEventos.clear();   createList("", "");   tableViewEventos.commit(); }



        return super.onOptionsItemSelected(item);
    }
}
