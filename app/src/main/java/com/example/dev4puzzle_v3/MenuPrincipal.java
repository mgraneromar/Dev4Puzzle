package com.example.dev4puzzle_v3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MenuPrincipal extends AppCompatActivity implements View.OnClickListener {

    Button seleccionarimagen;
    Button hallOfFame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        seleccionarimagen = findViewById(R.id.seleccionarimagen);
        hallOfFame = findViewById(R.id.halloffame);

        seleccionarimagen.setOnClickListener(this);
        hallOfFame.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.seleccionarimagen:
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                break;
            case R.id.halloffame:
                startActivity(new Intent(getApplicationContext(),HallOfFame.class));
                break;
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