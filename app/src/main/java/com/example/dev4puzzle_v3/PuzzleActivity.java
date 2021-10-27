package com.example.dev4puzzle_v3;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static java.lang.Math.abs;

public class PuzzleActivity extends AppCompatActivity {
    ArrayList<PuzzlePiece> pieces;

    Chronometer cronometro;
    String nombreJugador;
    String registroActual;

    int facil = 0;
    int intermedio = 0;
    int dificil = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        final RelativeLayout layout = findViewById(R.id.layout);
        final ImageView imageView = findViewById(R.id.ImageView);

        Intent intent = getIntent();
        final String assetName = intent.getStringExtra("assetName");

        //Se declara el objeto Cronometro para que aparezca en pantalla.
        cronometro = (Chronometer)findViewById(R.id.chronometer);

        // ejecutar código relacionado con la imagen después de que se haya diseñado la vista
        // tener todas las dimensiones calculadas
        imageView.post(new Runnable() {
            @Override
            public void run() {
                if (assetName != null) {
                    setPicFromAsset(assetName, imageView);
                    //Se inicia el proyecto al empezar la partida.
                    cronometro.start();
                }
                pieces = splitImage();
                TouchListener touchListener = new TouchListener(PuzzleActivity.this);
                // Orden aleatorio de las piezas
                Collections.shuffle(pieces);
                for (PuzzlePiece piece : pieces) {
                    piece.setOnTouchListener(touchListener);
                    layout.addView(piece);
                    // aleatorizar la posición, en la parte inferior de la pantalla
                    RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) piece.getLayoutParams();
                    lParams.leftMargin = new Random().nextInt(layout.getWidth() - piece.pieceWidth);
                    lParams.topMargin = layout.getHeight() - piece.pieceHeight;
                    piece.setLayoutParams(lParams);
                }
            }
        });
    }

    private void setPicFromAsset(String assetName, ImageView imageView) {
        // Obtener las dimensiones de la Vista
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        AssetManager am = getAssets();
        try {
            InputStream is = am.open("img/" + assetName);
            // Obtener las dimensiones del mapa de bits
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, new Rect(-1, -1, -1, -1), bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine cuánto reducir la imagen
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            is.reset();

            // Decodifica el archivo de imagen en un mapa de bits de tamaño para llenar la vista
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeStream(is, new Rect(-1, -1, -1, -1), bmOptions);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<PuzzlePiece> splitImage() {

        int piecesNumber =  0;
        int rows = 0;
        int cols = 0;

        // Se recoge el valor de las variables del menú de selección de nivel.
        facil = getIntent().getIntExtra("facil", 0);
        //Se asignan al log de Android studio las variables para que imprima su valor por consola.
        Log.d("NIVEL FACIL", String.valueOf(facil));
        intermedio = getIntent().getIntExtra("intermedio",0);
        Log.d("NIVEL INTERMEDIO", String.valueOf(intermedio));
        dificil = getIntent().getIntExtra("dificil",0 );;
        Log.d("NIVEL DIFICL", String.valueOf(dificil));

        if (facil == 1){
            piecesNumber = 12;
            rows = 4;
            cols = 3;
        } else if (intermedio == 1){
            piecesNumber = 24;
            rows = 6;
            cols = 4;
        } else if (dificil == 1){
            piecesNumber = 54;
            rows = 9;
            cols = 6;
        }

        ImageView imageView = findViewById(R.id.ImageView);
        ArrayList<PuzzlePiece> pieces = new ArrayList<>(piecesNumber);

        // Obtener el mapa de bits escalado de la imagen de origen
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        int[] dimensions = getBitmapPositionInsideImageView(imageView);
        int scaledBitmapLeft = dimensions[0];
        int scaledBitmapTop = dimensions[1];
        int scaledBitmapWidth = dimensions[2];
        int scaledBitmapHeight = dimensions[3];

        int croppedImageWidth = scaledBitmapWidth - 2 * abs(scaledBitmapLeft);
        int croppedImageHeight = scaledBitmapHeight - 2 * abs(scaledBitmapTop);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledBitmapWidth, scaledBitmapHeight, true);
        Bitmap croppedBitmap = Bitmap.createBitmap(scaledBitmap, abs(scaledBitmapLeft), abs(scaledBitmapTop), croppedImageWidth, croppedImageHeight);

        // Calcula el con y la altura de las piezas
        int pieceWidth = croppedImageWidth/cols;
        int pieceHeight = croppedImageHeight/rows;

        // Crea cada pieza de mapa de bits y agrégala a la matriz resultante
        int yCoord = 0;
        for (int row = 0; row < rows; row++) {
            int xCoord = 0;
            for (int col = 0; col < cols; col++) {
                // calcular el desplazamiento para cada pieza
                int offsetX = 0;
                int offsetY = 0;
                if (col > 0) {
                    offsetX = pieceWidth / 3;
                }
                if (row > 0) {
                    offsetY = pieceHeight / 3;
                }

                // aplica el desplazamiento a cada pieza
                Bitmap pieceBitmap = Bitmap.createBitmap(croppedBitmap, xCoord - offsetX, yCoord - offsetY, pieceWidth + offsetX, pieceHeight + offsetY);
                PuzzlePiece piece = new PuzzlePiece(getApplicationContext());
                piece.setImageBitmap(pieceBitmap);
                piece.xCoord = xCoord - offsetX + imageView.getLeft();
                piece.yCoord = yCoord - offsetY + imageView.getTop();
                piece.pieceWidth = pieceWidth + offsetX;
                piece.pieceHeight = pieceHeight + offsetY;

                // este mapa de bits contendrá la imagen de la pieza final del rompecabezas
                Bitmap puzzlePiece = Bitmap.createBitmap(pieceWidth + offsetX, pieceHeight + offsetY, Bitmap.Config.ARGB_8888);

                // dibujar ruta
                int bumpSize = pieceHeight / 4;
                Canvas canvas = new Canvas(puzzlePiece);
                Path path = new Path();
                path.moveTo(offsetX, offsetY);
                if (row == 0) {
                    // pieza lateral superior
                    path.lineTo(pieceBitmap.getWidth(), offsetY);
                } else {
                    // golpe superior
                    path.lineTo(offsetX + (pieceBitmap.getWidth() - offsetX) / 3, offsetY);
                    path.cubicTo(offsetX + (pieceBitmap.getWidth() - offsetX) / 6, offsetY - bumpSize, offsetX + (pieceBitmap.getWidth() - offsetX) / 6 * 5, offsetY - bumpSize, offsetX + (pieceBitmap.getWidth() - offsetX) / 3 * 2, offsetY);
                    path.lineTo(pieceBitmap.getWidth(), offsetY);
                }

                if (col == cols - 1) {
                    // pieza lateral derecha
                    path.lineTo(pieceBitmap.getWidth(), pieceBitmap.getHeight());
                } else {
                    // golpe a la derecha
                    path.lineTo(pieceBitmap.getWidth(), offsetY + (pieceBitmap.getHeight() - offsetY) / 3);
                    path.cubicTo(pieceBitmap.getWidth() - bumpSize,offsetY + (pieceBitmap.getHeight() - offsetY) / 6, pieceBitmap.getWidth() - bumpSize, offsetY + (pieceBitmap.getHeight() - offsetY) / 6 * 5, pieceBitmap.getWidth(), offsetY + (pieceBitmap.getHeight() - offsetY) / 3 * 2);
                    path.lineTo(pieceBitmap.getWidth(), pieceBitmap.getHeight());
                }

                if (row == rows - 1) {
                    // pieza lateral inferior
                    path.lineTo(offsetX, pieceBitmap.getHeight());
                } else {
                    // golpe de fondo
                    path.lineTo(offsetX + (pieceBitmap.getWidth() - offsetX) / 3 * 2, pieceBitmap.getHeight());
                    path.cubicTo(offsetX + (pieceBitmap.getWidth() - offsetX) / 6 * 5,pieceBitmap.getHeight() - bumpSize, offsetX + (pieceBitmap.getWidth() - offsetX) / 6, pieceBitmap.getHeight() - bumpSize, offsetX + (pieceBitmap.getWidth() - offsetX) / 3, pieceBitmap.getHeight());
                    path.lineTo(offsetX, pieceBitmap.getHeight());
                }

                if (col == 0) {
                    // pieza lateral izquierda
                    path.close();
                } else {
                    // golpe a la izquierda
                    path.lineTo(offsetX, offsetY + (pieceBitmap.getHeight() - offsetY) / 3 * 2);
                    path.cubicTo(offsetX - bumpSize, offsetY + (pieceBitmap.getHeight() - offsetY) / 6 * 5, offsetX - bumpSize, offsetY + (pieceBitmap.getHeight() - offsetY) / 6, offsetX, offsetY + (pieceBitmap.getHeight() - offsetY) / 3);
                    path.close();
                }

                // enmascarar la pieza
                Paint paint = new Paint();
                paint.setColor(0XFF000000);
                paint.setStyle(Paint.Style.FILL);

                canvas.drawPath(path, paint);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                canvas.drawBitmap(pieceBitmap, 0, 0, paint);

                // dibuja un borde blanco
                Paint border = new Paint();
                border.setColor(0X80FFFFFF);
                border.setStyle(Paint.Style.STROKE);
                border.setStrokeWidth(8.0f);
                canvas.drawPath(path, border);

                // dibuja un borde negro
                border = new Paint();
                border.setColor(0X80000000);
                border.setStyle(Paint.Style.STROKE);
                border.setStrokeWidth(3.0f);
                canvas.drawPath(path, border);

                // establece el mapa de bits resultante en la pieza
                piece.setImageBitmap(puzzlePiece);

                pieces.add(piece);
                xCoord += pieceWidth;
            }
            yCoord += pieceHeight;
        }

        return pieces;
    }

    private int[] getBitmapPositionInsideImageView(ImageView imageView) {
        int[] ret = new int[4];

        if (imageView == null || imageView.getDrawable() == null)
            return ret;

        // Obtener las dimensiones de la imagen
        // Obtener valores de matriz de imagen y colocarlos en una matriz
        float[] f = new float[9];
        imageView.getImageMatrix().getValues(f);

        // Extrae los valores de la escala usando las constantes (si se mantiene la relación de aspecto, scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        // Obtener el elemento dibujable (también podría obtener el mapa de bits detrás del elemento dibujable y getWidth / getHeight)
        final Drawable d = imageView.getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();

        // Calcula las dimensiones reales
        final int actW = Math.round(origW * scaleX);
        final int actH = Math.round(origH * scaleY);

        ret[2] = actW;
        ret[3] = actH;

        // Obtener la posición de la imagen
        // Suponemos que la imagen está centrada en ImageView
        int imgViewW = imageView.getWidth();
        int imgViewH = imageView.getHeight();

        int top = (int) (imgViewH - actH)/2;
        int left = (int) (imgViewW - actW)/2;

        ret[0] = left;
        ret[1] = top;

        return ret;
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

    public void AlertDialog() {
        //Se declara Intent para guardar las variables y pasarlas al Activity guardar partida.
        Intent intent2 = new Intent(this, GuardarPartida.class);
        //Se declara método para que aparezca la ventana de dialogo.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //Se guarda el valor del cronometro en ese momento.
        registroActual=cronometro.getText().toString();
        //Se para el cronometro.
        cronometro.stop();
        builder.setTitle("FIN DE LA PARTIDA");
        //Se declara variable para solicitar texto al usuario en ventana.
        final EditText input = new EditText(this);
        builder.setView(input);
        //Falta añadir dato de puntuación y tiempo de partida.
        builder.setMessage("Enhorabuena has resuelto el puzzle!\n" + "Tiempo de resolución: " + registroActual + "\nIngresa tu nombre: ");

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Se guarda el dato dado por el usuario.
                nombreJugador = input.getText().toString().trim();
                //Imprime en la consola de Android studio el valor de las variables.
                Log.d("NOMBRE JUGADOR", String.valueOf(nombreJugador));
                Log.d("GUARDADO", String.valueOf(registroActual));
                //Recoge el valor introducido por el usuario en el nombre y el tiempo de partida.
                intent2.putExtra("nombre", nombreJugador);
                intent2.putExtra("tiempo", registroActual);
                startActivity(intent2);
            }

        });;
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void Enviar(View view){
        Intent intent = new Intent(this, GuardarPartida.class);
        intent.putExtra("nombre", nombreJugador);
        intent.putExtra("tiempo", registroActual);
        startActivity(intent);
    }

    public void checkGameOver() {
        if (isGameOver()) {
            AlertDialog();
        }
    }

    private boolean isGameOver() {
        for (PuzzlePiece piece : pieces) {
            if (piece.canMove) {
                return false;
            }
        }

        return true;
    }

}