package com.example.listarecetas;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistorialActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecetaAdapter adapter;
    private EditText campoBusqueda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        campoBusqueda = findViewById(R.id.campoBusqueda);
        recyclerView = findViewById(R.id.recyclerHistorial);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cargarHistorial("");

        campoBusqueda.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                cargarHistorial(s.toString());
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    private void cargarHistorial(String filtro) {
        RecetaDao dao = RecetaDatabase.obtenerInstancia(this).recetaDao();
        List<Receta> recetas = filtro.isEmpty() ? dao.obtenerTodas() : dao.buscarPorTexto(filtro);
        adapter = new RecetaAdapter(recetas, this);
        recyclerView.setAdapter(adapter);
    }
}
