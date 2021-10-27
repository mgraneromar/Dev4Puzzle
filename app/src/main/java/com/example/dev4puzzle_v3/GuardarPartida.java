package com.example.dev4puzzle_v3;

import static com.example.dev4puzzle_v3.AdminSQLiteOpenHelper.TABLE_JUGADORES;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class GuardarPartida extends AppCompatActivity {

    private TextView nombreText;
    private TextView tiempoText;
    public String nombre;
    public String tiempo;
    Button btnGuardar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardar_partida);
        btnGuardar = findViewById(R.id.btnGuardar);

        nombreText = (TextView) findViewById(R.id.nombreJugador);
        tiempoText = (TextView) findViewById(R.id.tiempoJugador);

        nombre = getIntent().getStringExtra("nombre");
        nombreText.setText("Jugador: " + nombre);
        tiempo = getIntent().getStringExtra("tiempo");
        tiempoText.setText("Tiempo de partida: " + tiempo);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creación o actualización de la BBDD.
                AdminSQLiteOpenHelper adminSQLiteOpenHelper = new AdminSQLiteOpenHelper(GuardarPartida.this);
                SQLiteDatabase db = adminSQLiteOpenHelper.getWritableDatabase();

                //Cargar datos de la partida en la BBDD.
                ContentValues values = new ContentValues();
                values.put("nombre", nombre);
                values.put("tiempo", tiempo);
                db.insert(TABLE_JUGADORES, null, values);

                AlertDialogGuardarPartida();
            }
        });

    }

    public void AlertDialogGuardarPartida() {
        Intent intent = new Intent(this, HallOfFame.class);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("GUARDAR PARTIDA");
        builder.setMessage("¡JUGADOR GUARDADO CON ÉXITO!\n\n" + "Tiempo de partida: " + tiempo + "\nNombre: " + nombre);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                startActivity(intent);
            }
        });;

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu miMenu){
        getMenuInflater().inflate(R.menu.overflow, miMenu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem opcion_menu){
        int id = opcion_menu.getItemId();

        if(id == R.id.ayuda){
            Intent intent = new Intent(this, Ayuda.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(opcion_menu);
    }

}