package com.example.david.ejerciciowebservice;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private EditText editTextCantidad;
    private Spinner spinnerDe,spinnerA;
    private Button buttonConvertir;
    private TextView textViewResultado;
    private String[] medidas;
    private String spinnerDeMedida,spinnerAMedida,cantidad,resultadoParseado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editTextCantidad=(EditText)findViewById(R.id.editTextCantidad);
        spinnerDe=(Spinner)findViewById(R.id.spinnerDe);
        spinnerA=(Spinner)findViewById(R.id.spinnerA);
        buttonConvertir=(Button)findViewById(R.id.buttonConvertir);
        textViewResultado=(TextView)findViewById(R.id.textViewResultado);
        medidas=new String[]{"Kilometers","Yards","Meters","Miles"};

        //Rellenamos los spinner con los datos del array
        ArrayAdapter<String> adaptador=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,medidas);
        spinnerDe.setAdapter(adaptador);
        spinnerA.setAdapter(adaptador);

        //Obtener las medidas del spinner DE
        spinnerDe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerDeMedida = adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView){
            }
        });

        //Obtener las medidas del spinner A
        spinnerA.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerAMedida = adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView){
            }
        });
    }

    public void pulsar(View v) {
        if (v.getId() == R.id.buttonConvertir){
            cantidad=editTextCantidad.getText().toString();
            AsyncPost task=new AsyncPost();
            task.execute(cantidad);
        }
    }

    //Función AsyncPost
    private class AsyncPost extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection conn;
            try{
                //Conexión con POST
                URL url=new URL("http://www.webservicex.net/length.asmx/ChangeLengthUnit");
                String param="LengthValue="+ URLEncoder.encode(params[0],"UTF-8")+"&fromLengthUnit="+URLEncoder.encode(spinnerDeMedida,"UTF-8")+"&toLengthUnit="+URLEncoder.encode(spinnerAMedida,"UTF-8");
                conn= (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                //enviar por POST
                PrintWriter out=new PrintWriter((conn.getOutputStream()));
                out.print(param);
                out.close();
                //Recoger el resultado que nos devuelve
                String resultado="";
                //Comienza a escuchar
                Scanner inStream=new Scanner(conn.getInputStream());
                //Procesa el Stream
                while(inStream.hasNextLine()) {
                    resultado=inStream.nextLine();
                    resultadoParseado = resultado.replace("</double>","");
                    String resul=resultadoParseado.substring(resultado.indexOf('>')+1);
                    resultadoParseado=resul;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        return null;
        }

        @Override
        protected void onPostExecute(Void aVoid){
            textViewResultado.setText("  "+resultadoParseado);
        }
    }
}
