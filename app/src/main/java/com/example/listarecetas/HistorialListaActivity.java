package com.example.listarecetas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistorialListaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ListaCompraAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_lista);

        recyclerView = findViewById(R.id.recyclerHistorialLista);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cargarHistorial();
    }

    private void cargarHistorial() {
        List<ListaCompra> listas = RecetaDatabase.obtenerInstancia(this).listaCompraDao().obtenerTodas();
        adapter = new ListaCompraAdapter(listas, this);
        recyclerView.setAdapter(adapter);
    }

    public void reutilizarLista(String contenidoJson) {
        Intent intent = new Intent();
        intent.putExtra("listaProductos", contenidoJson);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}
