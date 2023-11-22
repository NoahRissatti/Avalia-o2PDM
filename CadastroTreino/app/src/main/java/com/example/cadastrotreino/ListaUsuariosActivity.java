package com.example.cadastrotreino;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListaUsuariosActivity extends AppCompatActivity {

    private List<User> userList;
    private UsuarioAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_usuarios);

        ListView listViewUsuarios = findViewById(R.id.listViewUsuarios);

        userList = new ArrayList<>();
        adapter = new UsuarioAdapter(this, userList);
        listViewUsuarios.setAdapter(adapter);

        DatabaseReference usuariosRef = FirebaseDatabase.getInstance("https://treino-ba698-default-rtdb.firebaseio.com/").getReference("usuarios");

        Button buttonNovoTreino = findViewById(R.id.buttonNovoTreino);
        buttonNovoTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirCadastroTreino();
            }
        });

        usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear(); // Limpar a lista antes de adicionar novos dados

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    userList.add(user);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ListaUsuariosActivity.this, "Erro ao ler usuários do Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void abrirCadastroTreino() {
        Intent intent = new Intent(ListaUsuariosActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
