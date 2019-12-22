package com.fernandohbrasil.alura.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.fernandohbrasil.alura.R;
import com.fernandohbrasil.alura.dao.NotaDAO;
import com.fernandohbrasil.alura.model.Nota;
import com.fernandohbrasil.alura.ui.recyclerview.adapter.ListaNotasAdapter;
import com.fernandohbrasil.alura.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.List;

import static com.fernandohbrasil.alura.ui.activity.NotaActivityConstantes.CHAVE_NOTA;
import static com.fernandohbrasil.alura.ui.activity.NotaActivityConstantes.CHAVE_POSICAO;
import static com.fernandohbrasil.alura.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_ALTERA_NOTA;
import static com.fernandohbrasil.alura.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_INSERE_NOTA;
import static com.fernandohbrasil.alura.ui.activity.NotaActivityConstantes.POSICAO_INVALIDA;

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
                vaiParaFormularioNotaActivityInsere();
            }
        });
    }

    private void vaiParaFormularioNotaActivityInsere() {
        Intent iniciaFormularioNota = new Intent(ListaNotasActivity.this, FormularioNotaActivity.class);
        startActivityForResult(iniciaFormularioNota, CODIGO_REQUISICAO_INSERE_NOTA);
    }

    private List<Nota> pegaTodasNotas() {
        NotaDAO dao = new NotaDAO();
        for (int i = 0; i < 10; i++) {
            dao.insere(new Nota("Título " + (i + 1),
                    "Descricao " + (i + 1)));
        }
        return dao.todos();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (ehUmResultadoInsereNota(requestCode, data)) {
            if (resultCode == Activity.RESULT_OK) {
                Nota nota = (Nota) data.getSerializableExtra(CHAVE_NOTA);
                adiciona(nota);
            }
        }

        if (ehResultadoAlteraNota(requestCode, data)) {
            if (resultCode == Activity.RESULT_OK) {
                Nota notaRecebida = (Nota) data.getSerializableExtra(CHAVE_NOTA);
                int posicaoRecebida = data.getIntExtra(CHAVE_POSICAO, POSICAO_INVALIDA);

                if (ehPosicaoValida(posicaoRecebida)) {
                    altera(notaRecebida, posicaoRecebida);
                } else {
                    Toast.makeText(this,
                            "Ocorreu um problema na alteracao da nota",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void altera(Nota nota, int posicao) {
        new NotaDAO().altera(posicao, nota);
        adapter.altera(posicao, nota);
    }

    private boolean ehPosicaoValida(int posicaoRecebida) {
        return posicaoRecebida > POSICAO_INVALIDA;
    }

    private boolean ehResultadoAlteraNota(int requestCode, @Nullable Intent data) {
        return data != null
                && requestCode == CODIGO_REQUISICAO_ALTERA_NOTA
                && data.hasExtra(CHAVE_NOTA);
    }

    private void adiciona(Nota nota) {
        new NotaDAO().insere(nota);
        adapter.adiciona(nota);
    }

    private boolean ehUmResultadoInsereNota(int requestCode, @Nullable Intent data) {
        return data != null
                && requestCode == CODIGO_REQUISICAO_INSERE_NOTA
                && data.hasExtra(CHAVE_NOTA);
    }

    private void configuraRecyclerView(List<Nota> todasNotas) {
        RecyclerView listaNota = findViewById(R.id.lista_notas_recyclerview);
        configuraAdapter(todasNotas, listaNota);
    }

    private void configuraAdapter(List<Nota> todasNotas, final RecyclerView listaNota) {
        adapter = new ListaNotasAdapter(todasNotas, this);
        listaNota.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Nota nota, int posicao) {
                vaiParaFormularioNotaActivityAltera(nota, posicao);
            }
        });
    }

    private void vaiParaFormularioNotaActivityAltera(Nota nota, int posicao) {
        Intent abreFormularioComNota = new Intent(ListaNotasActivity.this,
                FormularioNotaActivity.class);
        abreFormularioComNota.putExtra(CHAVE_NOTA, nota);
        abreFormularioComNota.putExtra(CHAVE_POSICAO, posicao);
        startActivityForResult(abreFormularioComNota, CODIGO_REQUISICAO_ALTERA_NOTA);
    }
}