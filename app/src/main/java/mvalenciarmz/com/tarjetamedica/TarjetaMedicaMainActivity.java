package mvalenciarmz.com.tarjetamedica;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import br.com.dina.ui.widget.UITableView.ClickListener;

import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import br.com.dina.ui.widget.UITableView;


public class TarjetaMedicaMainActivity extends ActionBarActivity {

    // Para el manejo de la base de datos ...
    private DBManager dbManager;

    // Para mantener un array con los datos que se están mostrando en la tabla
    private ArrayList<String> nombreArrayList = new ArrayList<String>();
    private ArrayList<String> idArrayList = new ArrayList<String>();

    // Definimos el tableview
    UITableView tableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarjeta_medica_main);

        // Creamos el tableview
        tableView = (UITableView) findViewById(R.id.tableView);
        // Rellenamos con datos
        createList();
        // Lo mostramos
        tableView.commit();

// de aquí
// http://developer.android.com/guide/topics/ui/notifiers/notifications.html

// http://stackoverflow.com/questions/24321872/i-need-to-implement-notification-reminder-in-my-android-app
//        http://developer.android.com/training/scheduling/alarms.html

/*
http://karanbalkar.com/2013/07/tutorial-41-using-alarmmanager-and-broadcastreceiver-in-android/
https://github.com/nishanil/AndroidWear/tree/master/ServiceReminder
http://androidideasblog.blogspot.co.uk/2011/07/alarmmanager-and-notificationmanager.html

 */

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.tarjetaazulfuerte)
                        .setContentTitle("Tarjeta Médica")
                        .setContentText("Puedo escribir los versos más tristes ésta noche, escribir por ejemplo: la noche está estrellada, y tiritan azules los astros a lo lejos");

// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, TarjetaMedicaMainActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(AltaEventoActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());



    }


    // Obtenemos de la base de datos las personas, y las ponemos en el tableview
    private void createList() {

        CustomClickListener listener = new CustomClickListener();
        tableView.setClickListener(listener);

        dbManager = new DBManager(this);
        dbManager.open();
        Cursor cursor = dbManager.selectPersonas();

        // Recorremos el cursor si es que existe al menos un registro
        if ( cursor.moveToFirst() ) {
            //Recorremos el cursor hasta que no haya más registros
            do {

                String nombre = cursor.getString(1).trim() + " " + cursor.getString(2).trim() + " " + cursor.getString(3);
                String sexo = cursor.getString(4).substring(5).trim();
                String numSegSocial = cursor.getString(5);
                String id = cursor.getString(0);
                String totalEventos = cursor.getString(13).trim();

                if ( totalEventos.equals("0") || totalEventos.equals("") ) {
                    totalEventos = "Sin eventos pendientes.";
                }
                else if ( totalEventos.equals("1") ) {
                    totalEventos = totalEventos + " evento pendiente.";
                } else {
                    totalEventos = totalEventos + " eventos pendientes.";
                }

                // En base a la fecha de nacimiento, calculamos la edad
                String fechaNacimiento = cursor.getString(9);
                Calendar c1 = GregorianCalendar.getInstance();
                int anActual = Integer.valueOf(c1.getTime().toLocaleString().substring(7, 11));

                int longitudFecha = fechaNacimiento.length();   // mes y año siempre iguales, pero día puede ser de un dígito o dos
                int anNacimiento;
                if (longitudFecha == 11) {
                    anNacimiento = Integer.valueOf(fechaNacimiento.substring(7));
                } else {
                    anNacimiento = Integer.valueOf(fechaNacimiento.substring(6));
                }

                int edad = anActual - anNacimiento;

                // Guardamos en un Arraylist los datos para saber que dato específico seleccionaron
                nombreArrayList.add(cursor.getString(1).trim());
                idArrayList.add(id);

                // niñas y niños de 0 a 9 años
                if (edad <= 9) {
                    tableView.addBasicItem(R.drawable.tarjetaverde, nombre, totalEventos);
                // Adolescentes de 10 a 19 años
                } else if (edad > 9 && edad <= 19) {
                    tableView.addBasicItem(R.drawable.tarjetaazul, nombre, totalEventos);
                // Mujeres de 20 a 59 años
                } else if ( sexo == "Femenino" && (edad > 19 && edad <= 59) ) {
                    tableView.addBasicItem(R.drawable.tarjetarosa, nombre, totalEventos);
                // hombres de 20 a 59 años
                } else if (sexo == "Masculino" && (edad > 19 && edad <= 59)) {
                    tableView.addBasicItem(R.drawable.tarjetaazulfuerte, nombre, totalEventos);
                // adultos mayores
                } else {
                    tableView.addBasicItem( R.drawable.tarjetacafe, nombre, totalEventos);
                }

            } while( cursor.moveToNext() );
        }

    }



    // Esto es para capturar el click de un elemento de la lista
    private class CustomClickListener implements ClickListener {

        @Override
        public void onClick(int index) {

            // Manejo de variables globales:
            TMed miApp = ( (TMed)getApplicationContext() );

            miApp.setNombreActual( nombreArrayList.get(index) );
            miApp.setIdActual( idArrayList.get(index) );

            // Lanzamos la actividad
            Intent i = new Intent(TarjetaMedicaMainActivity.this, EventosActivity.class);
            startActivity(i);

        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tarjeta_medica_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Si quieren dar de alta una nueva persona ...
        if (id == R.id.masPersona) {

            // Manejo de variables globales:
            TMed miApp = ( (TMed)getApplicationContext() );
            // Vaciamos el id de la persona, para saber y estar seguros que es una alta
            miApp.setNombreActual("");
            miApp.setIdActual("0");

            Intent i = new Intent(TarjetaMedicaMainActivity.this, AltaPersonaActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    // Cada que regresemos a la pantalla, actualizaremos los datos por si cambiaron algo
    @Override
    public void onResume(){
        super.onResume();

        nombreArrayList = new ArrayList<String>();
        idArrayList = new ArrayList<String>();

        tableView.clear();

        // Rellenamos con datos
        createList();
        // Lo mostramos
        tableView.commit();

    }


}
