package com.example.listarecetas;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import android.content.Intent;
import java.util.List;




public class MainActivity extends AppCompatActivity implements OnListaActualizadaListener {

    private List<Producto> listaProductos;
    private AdaptadorProducto adaptador;
    private double tasaDolar = 135.0;

    private EditText campoIngredientes;
    private TextView textoResultado;

    private final String CLAVE_API = "AIzaSyA2X0Wx-ZL7T1lO_hMICAvrFjGytlsZGsc"; // Sustituye por tu clave real

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Lista de productos
        listaProductos = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.listaProductos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new AdaptadorProducto(listaProductos,this);
        recyclerView.setAdapter(adaptador);



        // Campo de tasa del dólar
        EditText campoTasa = findViewById(R.id.campoTasaDolar);
        //campoTasa.setText(String.valueOf(tasaDolar));
        campoTasa.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    tasaDolar = Double.parseDouble(s.toString());
                } catch (NumberFormatException e) {
                    tasaDolar = 1.0;
                }
                actualizarTotal();
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        // Botón para agregar producto
        Button botonAgregar = findViewById(R.id.botonAgregarProducto);
        botonAgregar.setOnClickListener(v -> mostrarDialogoAgregarProducto());

        // botón para ver el historial de recetas
        Button botonHistorial = findViewById(R.id.botonVerHistorial);
        botonHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistorialActivity.class);
            startActivityForResult(intent, 200);
        });

        //BOTON PARA GUARDAR LA LISTA ACTUAL
        Button botonGuardarLista = findViewById(R.id.botonGuardarLista);
        botonGuardarLista.setOnClickListener(v -> guardarListaDeCompra());


        // Botón de historial de compra
        Button botonHistorialCompras = findViewById(R.id.botonHistorialCompras);
        botonHistorialCompras.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistorialListaActivity.class);
            startActivityForResult(intent, 200);
        });



        // Campo de texto y botón para IA
        campoIngredientes = findViewById(R.id.campoIngredientes);
        textoResultado = findViewById(R.id.resultadoReceta);
        Button botonGenerar = findViewById(R.id.botonGenerarReceta);
        botonGenerar.setOnClickListener(v -> {
            String ingredientes = campoIngredientes.getText().toString();
            if (!ingredientes.isEmpty()) {
                pedirRecetaIA(ingredientes);
            } else {
                textoResultado.setText("Por favor, escribe algunos ingredientes.");
            }
        });
    }

    @Override
    public void onListaActualizada() {
        actualizarTotal(); // se llama automáticamente al sumar/restar/eliminar
    }

    private void guardarListaDeCompra() {
        if (listaProductos.isEmpty()) {
            Toast.makeText(this, "No hay productos para guardar", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONArray jsonArray = new JSONArray();
        for (Producto p : listaProductos) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("nombre", p.getNombre());
                obj.put("cantidad", p.getCantidad());
                obj.put("precio", p.getPrecioUsd());
                jsonArray.put(obj);
            } catch (Exception ignored) {}
        }

        ListaCompra nueva = new ListaCompra();
        nueva.fechaCreacion = System.currentTimeMillis();
        nueva.contenidoJson = jsonArray.toString();

        RecetaDatabase.obtenerInstancia(this).listaCompraDao().insertar(nueva);

        Toast.makeText(this, "Lista guardada", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK) {
            String json = data.getStringExtra("listaProductos");
            try {
                JSONArray array = new JSONArray(json);
                listaProductos.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    Producto p = new Producto(
                            obj.getString("nombre"),
                            obj.getInt("cantidad"),
                            obj.getDouble("precio")
                    );
                    listaProductos.add(p);
                }
                adaptador.notifyDataSetChanged();
                actualizarTotal();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void mostrarDialogoAgregarProducto() {
        View vistaDialogo = LayoutInflater.from(this).inflate(R.layout.dialogo_agregar_producto, null);
        EditText nombre = vistaDialogo.findViewById(R.id.campoNombre);
        EditText cantidad = vistaDialogo.findViewById(R.id.campoCantidad);
        EditText precio = vistaDialogo.findViewById(R.id.campoPrecio);

        new AlertDialog.Builder(this)
                .setTitle("Agregar Producto")
                .setView(vistaDialogo)
                .setPositiveButton("Agregar", (dialog, which) -> {
                    String nombreTexto = nombre.getText().toString();
                    int cantidadValor = 1;
                    double precioValor = 0.0;

                    try {
                        cantidadValor = Integer.parseInt(cantidad.getText().toString());
                    } catch (NumberFormatException ignored) {}

                    try {
                        precioValor = Double.parseDouble(precio.getText().toString());
                    } catch (NumberFormatException ignored) {}

                    Producto producto = new Producto(nombreTexto, cantidadValor, precioValor);
                    listaProductos.add(producto);
                    adaptador.notifyDataSetChanged();
                    actualizarTotal();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void actualizarTotal() {
        double totalUsd = 0.0;
        for (Producto producto : listaProductos) {
            totalUsd += producto.getPrecioUsd() * producto.getCantidad();
        }
        double totalBs = totalUsd * tasaDolar;

        TextView textoTotal = findViewById(R.id.textoTotal);
        textoTotal.setText(String.format("Total: $%.2f / Bs %.2f", totalUsd, totalBs));
    }

    private void pedirRecetaIA(String ingredientes) {
        OkHttpClient cliente = new OkHttpClient();
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + CLAVE_API;

        JSONObject mensaje = new JSONObject();
        try {
            JSONArray contenidos = new JSONArray();
            JSONObject parte = new JSONObject();
            parte.put("text", "Tengo estos ingredientes: " + ingredientes + ". ¿Qué receta puedo hacer? , genera dos receta con su lista de ingredientes");
            JSONObject contenido = new JSONObject();
            contenido.put("parts", new JSONArray().put(parte));
            contenidos.put(contenido);
            mensaje.put("contents", contenidos);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody cuerpo = RequestBody.create(mensaje.toString(), MediaType.get("application/json"));
        Request peticion = new Request.Builder().url(url).post(cuerpo).build();

        cliente.newCall(peticion).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> textoResultado.setText("Error de conexión: " + e.getMessage()));
            }

            public void onResponse(Call call, Response respuesta) throws IOException {
                if (respuesta.isSuccessful()) {
                    try {
                        JSONObject json = new JSONObject(respuesta.body().string());
                        String resultado = json
                                .getJSONArray("candidates")
                                .getJSONObject(0)
                                .getJSONObject("content")
                                .getJSONArray("parts")
                                .getJSONObject(0)
                                .getString("text");

                        runOnUiThread(() -> textoResultado.setText(resultado));
                        // Guardar receta en la base de datos
                        Receta nueva = new Receta();
                        nueva.textoReceta = resultado;
                        nueva.ingredientes = ingredientes;
                        nueva.fechaCreacion = System.currentTimeMillis();
                        nueva.favorito = false;

                        RecetaDatabase.obtenerInstancia(MainActivity.this).recetaDao().insertar(nueva);

                    } catch (Exception e) {
                        runOnUiThread(() -> textoResultado.setText("Error al procesar la respuesta"));
                    }
                } else {
                    runOnUiThread(() -> textoResultado.setText("Error: " + respuesta.code()));
                }
            }
        });
    }
}
