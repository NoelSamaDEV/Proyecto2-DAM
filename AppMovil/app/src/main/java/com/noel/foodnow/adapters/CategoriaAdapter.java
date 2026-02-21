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

    private List<Categoria> categorias;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Categoria categoria);
    }

    public CategoriaAdapter(List<Categoria> categorias, Context context, OnItemClickListener listener) {
        this.categorias = categorias;
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
        Categoria item = categorias.get(position);
        holder.txtNombre.setText(item.getNombre());

        // CORREGIDO: Ahora usa getImagen() para coincidir con el modelo
        Glide.with(context)
                .load(item.getImagen())
                .placeholder(R.drawable.logo)
                .into(holder.imgCategoria);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCategoria;
        TextView txtNombre;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCategoria = itemView.findViewById(R.id.imgCategoria);
            txtNombre = itemView.findViewById(R.id.txtNombreCategoria);
        }
    }
}