package com.example.cadastrotreino;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText editTextNome, editTextPeso, editTextIdade, editTextDescricao;
    private RadioGroup radioGroupSexo;
    private Spinner spinnerDiaSemana;
    private Button buttonCadastrar;
    private Button buttonCancelar;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        // Inicializar Firebase
        firebaseDatabase = FirebaseDatabase.getInstance("https://treino-ba698-default-rtdb.firebaseio.com/");
        databaseReference = firebaseDatabase.getReference("usuarios");

        // Inicializar Views
        editTextNome = findViewById(R.id.editTextNome);
        editTextPeso = findViewById(R.id.editTextPeso);
        editTextIdade = findViewById(R.id.editTextIdade);
        editTextDescricao = findViewById(R.id.editTextDescricao);
        radioGroupSexo = findViewById(R.id.radioGroupSexo);
        spinnerDiaSemana = findViewById(R.id.spinnerDiaSemana);
        buttonCadastrar = findViewById(R.id.buttonCadastrar);
        buttonCancelar = findViewById(R.id.buttonCancelar);

        // Configurar Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.dias_semana,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDiaSemana.setAdapter(adapter);

        // Configurar Botão de Cadastro
        buttonCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadastrarUsuario();
            }
        });

        buttonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ListaUsuariosActivity.class);
                startActivity(intent);
            }
        });
    }

    private void cadastrarUsuario() {
        String nome = editTextNome.getText().toString().trim();
        String pesoStr = editTextPeso.getText().toString().trim();
        String idadeStr = editTextIdade.getText().toString().trim();
        String descricao = editTextDescricao.getText().toString().trim();
        String sexo = obterSexoSelecionado();
        String diaSemana = spinnerDiaSemana.getSelectedItem().toString();

        if (nome.isEmpty() || pesoStr.isEmpty() || idadeStr.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show();
            return;
        }

        double peso = Double.parseDouble(pesoStr);
        int idade = Integer.parseInt(idadeStr);

        User user = new User(nome, idade, sexo, peso, diaSemana, descricao);

        DatabaseReference newRef = databaseReference.push();
        newRef.setValue(user, (databaseError, databaseReference) -> {
            if (databaseError != null) {
                Toast.makeText(MainActivity.this, "Erro ao cadastrar usuário", Toast.LENGTH_SHORT).show();
            } else {
                limparCampos();

                String userId = newRef.getKey();
                Intent intent = new Intent(MainActivity.this, ListaUsuariosActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);

                Toast.makeText(MainActivity.this, "Usuário cadastrado com sucesso", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String obterSexoSelecionado() {
        int radioButtonId = radioGroupSexo.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(radioButtonId);
        return radioButton != null ? radioButton.getText().toString() : "";
    }

    private void limparCampos() {
        editTextNome.getText().clear();
        editTextPeso.getText().clear();
        editTextIdade.getText().clear();
        editTextDescricao.getText().clear();
        radioGroupSexo.clearCheck();
        spinnerDiaSemana.setSelection(0);
    }
}
