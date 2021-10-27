package com.example.dev4puzzle_v3;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import java.io.IOException;

import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity {

    int facil = 0;
    int intermedio = 0;
    int dificil = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AssetManager am = getAssets();
        try {
            final String[] files  = am.list("img");

            GridView grid = findViewById(R.id.grid);
            grid.setAdapter(new ImageAdapter(this));
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Intent intent = new Intent(getApplicationContext(), PuzzleActivity.class);
                    intent.putExtra("assetName", files[i % files.length]);
                    //Declaramos AlerDialog para que aparezca ventana emergente
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("ELIGE NIVEL");

                    //Declaramos valor de los botones.
                    String[] niveles = {"FÁCIL", "INTERMEDIO", "DIFICIL"};
                    int checkedItem = 3; // Marca que opción aparece señalada por defecto. Ninguna
                    builder.setSingleChoiceItems(niveles, checkedItem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Menú que se ejecuta al pulsar la opción del nivel
                            switch (which) {
                                case 0:
                                    facil = 1;
                                    intent.putExtra("facil", facil);
                                    break;
                                case 1:
                                    intermedio = 1;
                                    intent.putExtra("intermedio", intermedio);
                                    break;
                                case 2:
                                    dificil = 1;
                                    intent.putExtra("dificil", dificil);
                                    break;
                            }
                        }
                    });

                    // añadir Aceptar y cancelar.
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Al pulsar en "Aceptar" se inicia la partida.
                            startActivity(intent);
                            //Imprime en la consola de Android studio el valor de las variables.
                            Log.d("NIVEL FACIL", String.valueOf(facil));
                            Log.d("NIVEL INTERMEDIO", String.valueOf(intermedio));
                            Log.d("NIVEL DIFICL", String.valueOf(dificil));
                        }
                    });
                    builder.setNegativeButton("Cancelar", null);

                    // Crea y visualiza la ventana con el menú
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        } catch (IOException e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT);
        }

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