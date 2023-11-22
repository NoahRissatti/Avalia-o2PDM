package com.example.cadastrotreino;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UsuarioAdapter extends ArrayAdapter<User> {
    private Context context;
    private List<User> userList;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public UsuarioAdapter(Context context, List<User> userList) {
        super(context, 0, userList);
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView textViewInfo = convertView.findViewById(android.R.id.text1);

        final User user = userList.get(position);

        // Exibir informações desejadas (nome, idade e peso)
        String userInfo = "Nome: " + user.getNome() + "\nIdade: " + user.getIdade() + "\nPeso: " + user.getPeso();
        textViewInfo.setText(userInfo);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDetalhesUsuario(user);
            }
        });

        return convertView;
    }

    private void mostrarDetalhesUsuario(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Detalhes do Usuário");

        String detalhesUsuario = "Nome: " + user.getNome() + "\nIdade: " + user.getIdade() + "\nPeso: " + user.getPeso() +
                "\nSexo: " + user.getSexo() + "\nDia da Semana: " + user.getDiaSemana() + "\nDescrição: " + user.getDescricao();

        builder.setMessage(detalhesUsuario);

        builder.setNegativeButton("Deletar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deletarUsuario(user);
            }
        });

        builder.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deletarUsuario(User user) {
        // Certifique-se de inicializar o FirebaseDatabase antes de usá-lo
        firebaseDatabase = FirebaseDatabase.getInstance("https://treino-ba698-default-rtdb.firebaseio.com/");
        databaseReference = firebaseDatabase.getReference("usuarios");

        Query query = databaseReference.orderByChild("nome").equalTo(user.getNome());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    userSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Usuário excluído com sucesso", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Erro ao excluir usuário", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Erro ao excluir usuário por nome", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
