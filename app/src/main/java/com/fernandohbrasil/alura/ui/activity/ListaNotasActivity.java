package com.fernandohbrasil.alura.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.fernandohbrasil.alura.R;
import com.fernandohbrasil.alura.dao.NotaDAO;
import com.fernandohbrasil.alura.model.Nota;
import com.fernandohbrasil.alura.ui.recyclerview.adapter.ListaNotasAdapter;

import java.util.List;

import static com.fernandohbrasil.alura.ui.activity.NotaActivityConstantes.CHAVE_NOTA;
import static com.fernandohbrasil.alura.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_INSERE_NOTA;
import static com.fernandohbrasil.alura.ui.activity.NotaActivityConstantes.CODIGO_RESULTADO_NOTA_CRIADA;

public class ListaNotasActivity extends AppCompatActivity {

    private ListaNotasAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_notas);

        List<Nota> todasNotas = pegaTodasNotas();
        configuraRecyclerView(todasNotas);

        configuraBotaoInsereNota();
    }

    private void configuraBotaoInsereNota() {
        TextView botaoInsereNota = findViewById(R.id.lista_notas_insere_nota);
        botaoInsereNota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vaiParaFormularioNotaActivity();
            }
        });
    }

    private void vaiParaFormularioNotaActivity() {
        Intent iniciaFormularioNota = new Intent(ListaNotasActivity.this, FormularioNotaActivity.class);
        startActivityForResult(iniciaFormularioNota, CODIGO_REQUISICAO_INSERE_NOTA);
    }

    private List<Nota> pegaTodasNotas() {
        NotaDAO dao = new NotaDAO();
        return dao.todos();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (ehUmResultadoComNota(requestCode, resultCode, data)) {
            Nota nota = (Nota) data.getSerializableExtra(CHAVE_NOTA);
            adiciona(nota);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void adiciona(Nota nota) {
        new NotaDAO().insere(nota);
        adapter.adiciona(nota);
    }

    private boolean ehUmResultadoComNota(int requestCode, int resultCode, @Nullable Intent data) {
        return data != null && requestCode == CODIGO_REQUISICAO_INSERE_NOTA && resultCode == CODIGO_RESULTADO_NOTA_CRIADA && data.hasExtra(CHAVE_NOTA);
    }

    private void configuraRecyclerView(List<Nota> todasNotas) {
        RecyclerView listaNota = findViewById(R.id.lista_notas_recyclerview);
        configuraAdapter(todasNotas, listaNota);
    }

    private void configuraAdapter(List<Nota> todasNotas, RecyclerView listaNota) {
        adapter = new ListaNotasAdapter(todasNotas, this);
        listaNota.setAdapter(adapter);
    }
}