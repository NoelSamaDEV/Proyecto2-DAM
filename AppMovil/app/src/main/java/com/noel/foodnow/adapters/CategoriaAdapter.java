package com.noel.foodnow.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.noel.foodnow.R;
import com.noel.foodnow.models.Categoria;
import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.ViewHolder> {

    private List<Categoria> listaCategorias;
    private Context context;
    private final OnItemClickListener listener;

    // Interfaz para saber cuándo hacen clic
    public interface OnItemClickListener {
        void onItemClick(Categoria categoria);
    }

    public CategoriaAdapter(List<Categoria> lista, Context context, OnItemClickListener listener) {
        this.listaCategorias = lista;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categoria, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Categoria item = listaCategorias.get(position);
        holder.txtNombre.setText(item.getNombre());

        // Intenta cargar la URL. Si falla o es null, pone el icono de galería por defecto.
        Glide.with(context)
                .load(item.getImagenUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .into(holder.imagen);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() { return listaCategorias.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagen;
        TextView txtNombre;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.imgCategoria);
            txtNombre = itemView.findViewById(R.id.txtNombreCategoria);
        }
    }
}