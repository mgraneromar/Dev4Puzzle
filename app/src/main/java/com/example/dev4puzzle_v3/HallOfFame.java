package com.example.dev4puzzle_v3;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HallOfFame extends AppCompatActivity {

    ListView lista;
    AdminSQLiteOpenHelper db;
    List<String> item = null;
    Button btnAceptar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hall_of_fame);

        lista = (ListView) findViewById(R.id.listViewResultados);
        showResultados();

        btnAceptar = findViewById(R.id.buttonHallFame);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MenuPrincipal.class));
            }
        });

    }
    private void showResultados(){
        db = new AdminSQLiteOpenHelper(this);
        Cursor c = db.getDatos();
        item = new ArrayList<String>();
        String title = "",content = "";

        if(c.moveToFirst()){
            do{
                title = c.getString(1);
                content = c.getString(2);
                item.add(title + " " + content);

            }while (c.moveToNext());
        }
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item);
        lista.setAdapter(adaptador);
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