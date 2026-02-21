package com.noel.foodnow.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.noel.foodnow.R;
import com.noel.foodnow.models.CarritoManager;
import com.noel.foodnow.models.Producto;

import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    private List<Producto> lista;
    private Context context;

    public ProductoAdapter(List<Producto> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto p = lista.get(position);
        holder.txtNombre.setText(p.getNombre());
        holder.txtPrecio.setText(String.format("%.2f €", p.getPrecio().doubleValue()));

        holder.cantidadActual = 1;
        holder.txtCantidad.setText("1");

        // Carga con GLIDE
        if (p.getImagen() != null && !p.getImagen().isEmpty()) {
            Glide.with(context).load(p.getImagen()).into(holder.img);
        } else {
            holder.img.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.btnMas.setOnClickListener(v -> {
            holder.cantidadActual++;
            holder.txtCantidad.setText(String.valueOf(holder.cantidadActual));
        });

        holder.btnMenos.setOnClickListener(v -> {
            if (holder.cantidadActual > 1) {
                holder.cantidadActual--;
                holder.txtCantidad.setText(String.valueOf(holder.cantidadActual));
            }
        });

        holder.btnAgregar.setOnClickListener(v -> {
            CarritoManager.getInstance().agregarProducto(p, holder.cantidadActual);
            Toast.makeText(context, p.getNombre() + " añadido", Toast.LENGTH_SHORT).show();
            holder.cantidadActual = 1;
            holder.txtCantidad.setText("1");
        });
    }

    @Override
    public int getItemCount() { return lista.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView txtNombre, txtPrecio, txtCantidad;
        Button btnMas, btnMenos, btnAgregar;
        int cantidadActual = 1;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgProducto);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtPrecio = itemView.findViewById(R.id.txtPrecio);
            txtCantidad = itemView.findViewById(R.id.txtCantidad);
            btnMas = itemView.findViewById(R.id.btnMas);
            btnMenos = itemView.findViewById(R.id.btnMenos);
            btnAgregar = itemView.findViewById(R.id.btnAgregar);
        }
    }
}