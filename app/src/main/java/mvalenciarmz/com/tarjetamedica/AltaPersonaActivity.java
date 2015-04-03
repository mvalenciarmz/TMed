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

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class AltaPersonaActivity extends ActionBarActivity implements DatePickerDialog.OnDateSetListener {

    // Para saber si estamos en un cambio (UPDATE) o en una alta (INSERT)
    private String idPersona;

    // Para el control que permite seleccionar una fecha
    private Calendar calendar;
    private DateFormat dateFormat;
    private Button btnFecha;

    // Para la interacción con la base de datos
    private DBManager dbManager;

    // Para obtener el valor de cada campo
    private EditText txtNombre;
    private EditText txtPaterno;
    private EditText txtMaterno;
    private Spinner txtSexo;
    private EditText txtNumSegSocial;
    private EditText txtUnidadMedica;
    private Spinner txtHorario;
    private EditText txtConsultorio;
    private Button   btnFechaNacimiento;
    private EditText txtCurp;
    private EditText txtTipoSangre;
    private EditText txtObservaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_persona);

        // Para el control que permite seleccionar una fecha
        calendar = Calendar.getInstance();
        dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());

        // Obtenemos la información de cada campo en la forma
        txtNombre = (EditText) findViewById(R.id.nombre);
        txtPaterno = (EditText) findViewById(R.id.paterno);
        txtMaterno = (EditText) findViewById(R.id.materno);
        txtSexo = (Spinner) findViewById(R.id.sexo);
        txtNumSegSocial = (EditText) findViewById(R.id.numSegSocial);
        txtUnidadMedica  = (EditText) findViewById(R.id.unidadMedica);
        txtHorario = (Spinner) findViewById(R.id.horario);
        txtConsultorio = (EditText) findViewById(R.id.consultorio);
        btnFechaNacimiento = (Button) findViewById(R.id.fechaNacimiento);
        txtCurp = (EditText) findViewById(R.id.curp);
        txtTipoSangre = (EditText) findViewById(R.id.tipoSangre);
        txtObservaciones = (EditText) findViewById(R.id.observaciones);

        // Para la interacción con la base de datos
        dbManager = new DBManager(this);
        dbManager.open();

        // Obtenemos el id del servicio actual ( si es una alta, valdrá 999999 )
        TMed miApp = ( (TMed)getApplicationContext() );
        idPersona = miApp.getIdActual();

        // Si NO tiene el id de la persona a 0 entonces es un cambio, y hay que recuperar los datos
        if ( !idPersona.equals("0") ) {
            CargaDatosPersona();
        }



    }

    // Cuando presionen el botón, mostramos el control que les permite seleccionar una fecha
    public void btnFechaNacimiento(View button) {
        DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show(getFragmentManager(), "datePicker");
    }



    // Esto dibuja el menú
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alta_persona, menu);
        return true;
    }

    // Detectamos si presionan un botón del menú
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        // Si presionan el botón de Guardar Persona
        if (id == R.id.addPersona) {

            final String nombre = txtNombre.getText().toString();
            final String paterno = txtPaterno.getText().toString();
            final String materno = txtMaterno.getText().toString();
            final String sexo = txtSexo.getSelectedItem().toString();
            final String numSegSocial = txtNumSegSocial.getText().toString();
            final String unidadMedica = txtUnidadMedica.getText().toString();
            final String horario = txtHorario.getSelectedItem().toString();
            final String consultorio = txtConsultorio.getText().toString();
            final String fechaNacimiento = btnFechaNacimiento.getText().toString();
            final String curp = txtCurp.getText().toString();
            final String tipoSangre = txtTipoSangre.getText().toString();
            final String observaciones = txtObservaciones.getText().toString();

            // Validamos los campos obligatorios
            if ( nombre.trim().length() == 0 ) {
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
                dlgAlert.setMessage("Escribe al menos el nombre de la persona");
                dlgAlert.setTitle("FALTA INFORMACIÓN");
                dlgAlert.setPositiveButton("Aceptar", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
                return false;
            }

            if ( fechaNacimiento.trim().length() == 0 || fechaNacimiento.trim().matches("Fecha Nacimiento") ) {
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
                dlgAlert.setMessage("La fecha de nacimiento es obligatoria para determinar la edad");
                dlgAlert.setTitle("FALTA INFORMACIÓN");
                dlgAlert.setPositiveButton("Aceptar", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
                return false;
            }

            // Checamos que el nombre no exista, siempre que sea un alta
            if ( idPersona.equals("0") ) {
                if (dbManager.existePersona(nombre) > 0) {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
                    dlgAlert.setMessage("El nombre ya está registrado");
                    dlgAlert.setTitle("YA EXISTE INFORMACIÓN");
                    dlgAlert.setPositiveButton("Aceptar", null);
                    dlgAlert.setCancelable(true);
                    dlgAlert.create().show();
                    return false;
                }
            }

            // Si id de persona = "0" es una alta ...
            if ( idPersona.equals("0") ) {
                dbManager.insertPersona( nombre, paterno, materno, sexo, numSegSocial, unidadMedica, horario, consultorio, fechaNacimiento, curp, tipoSangre, observaciones);
            // Si no, es un cambio
            } else {
                dbManager.updatePersona( idPersona, nombre, paterno, materno, sexo, numSegSocial, unidadMedica, horario, consultorio, fechaNacimiento, curp, tipoSangre, observaciones );
            }

            // Refrescamos la pantalla principal
            Intent main = new Intent(AltaPersonaActivity.this, TarjetaMedicaMainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Lanzamos nuevamente la pantalla principal
            startActivity(main);

        }

        return super.onOptionsItemSelected(item);

    }

    // Cuando seleccionen una fecha, la dibujamos como texto del botón
    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {

        String nombreMes[] = {"ENE", "FEB", "MAR", "ABR", "MAY", "JUN", "JUL", "AGO", "SEP", "OCT", "NOV", "DIC"};

        btnFecha = (Button) findViewById(R.id.fechaNacimiento);

        calendar.set(year, monthOfYear, dayOfMonth);
        //btnFecha.setText( dateFormat.format( calendar.getTime() ) );
        btnFecha.setText( dayOfMonth + "/" + nombreMes[monthOfYear] + "/" + year );

    }

    // Cargamos el registro de la persona actual, desde la tabla personas
    private void CargaDatosPersona() {

        dbManager = new DBManager(this);
        dbManager.open();
        Cursor cursor = dbManager.selectPersonaActual(idPersona);

        // Recorremos el cursor si es que existe al menos un registro
        if (cursor.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {

                String nombre = cursor.getString(1).trim();
                String paterno = cursor.getString(2).trim();
                String materno = cursor.getString(3).trim();
                String sexo = cursor.getString(4).trim();
                String numSegSocial = cursor.getString(5).trim();
                String unidadMedica = cursor.getString(6).trim();
                String horario = cursor.getString(7).trim();
                String consultorio = cursor.getString(8).trim();
                String fechaNacimiento = cursor.getString(9).trim();
                String curp = cursor.getString(10).trim();
                String tipoSangre = cursor.getString(11).trim();
                String observaciones = cursor.getString(12).trim();

                txtNombre.setText(nombre);
                txtPaterno.setText(paterno);
                txtMaterno.setText(materno);
                txtSexo.setSelection(((ArrayAdapter) txtSexo.getAdapter()).getPosition(sexo));
                txtNumSegSocial.setText(numSegSocial);
                txtUnidadMedica.setText(unidadMedica);
                txtHorario.setSelection(((ArrayAdapter) txtHorario.getAdapter()).getPosition(horario));
                txtConsultorio.setText(consultorio);
                btnFechaNacimiento.setText(fechaNacimiento);
                txtCurp.setText(curp);
                txtTipoSangre.setText(tipoSangre);
                txtObservaciones.setText(observaciones);

                String nombreMes[] = {"ENE", "FEB", "MAR", "ABR", "MAY", "JUN", "JUL", "AGO", "SEP", "OCT", "NOV", "DIC"};

                int dia;
                String  mes;
                int  ano;
                int longitudFecha = fechaNacimiento.length();   // mes y año siempre iguales, pero día puede ser de un dígito o dos
                if (longitudFecha == 11) {
                    dia = Integer.parseInt( fechaNacimiento.substring( 0, 2 ) );
                    mes = fechaNacimiento.substring( 3, 6 );
                    ano = Integer.parseInt( fechaNacimiento.substring( 7 ) );
                } else {
                    dia = Integer.parseInt( fechaNacimiento.substring( 0, 1 ) );
                    mes = fechaNacimiento.substring( 2, 5 );
                    ano = Integer.parseInt(fechaNacimiento.substring(6));
                }

                int numMes = Arrays.asList(nombreMes).indexOf(mes);
                calendar.set(ano, numMes, dia);

            } while (cursor.moveToNext());
        }

    }


}
