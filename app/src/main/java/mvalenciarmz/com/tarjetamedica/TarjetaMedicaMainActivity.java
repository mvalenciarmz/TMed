package mvalenciarmz.com.tarjetamedica;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import br.com.dina.ui.widget.UITableView.ClickListener;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

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

                String nombre = cursor.getString(1).trim()  + " " + cursor.getString(2).trim() + " " + cursor.getString(3);
                String numSegSocial = cursor.getString(4);
                String id = cursor.getString(0);

                // Guardamos en un Arraylist los datos para saber que dato específico seleccionaron
                nombreArrayList.add( cursor.getString(1).trim() );
                idArrayList.add( id );

                tableView.addBasicItem( R.drawable.tarjetarosa, nombre, numSegSocial);

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
            Intent i = new Intent(TarjetaMedicaMainActivity.this, AltaPersonaActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

}
