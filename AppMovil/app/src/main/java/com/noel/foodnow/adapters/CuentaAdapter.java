package com.noel.foodnow.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.noel.foodnow.R;
import com.noel.foodnow.models.LineaCuenta; // Crearemos este modelo luego
import java.util.List;

public class CuentaAdapter extends RecyclerView.Adapter<CuentaAdapter.ViewHolder> {

    private List<LineaCuenta> lineas;

    public CuentaAdapter(List<LineaCuenta> lineas) {
        this.lineas = lineas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cuenta, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LineaCuenta linea = lineas.get(position);

        holder.txtCantidad.setText(linea.getCantidad() + "x");
        holder.txtNombre.setText(linea.getNombreProducto());
        holder.txtPrecio.setText(String.format("%.2f €", linea.getSubtotal()));
    }

    @Override
    public int getItemCount() {
        return lineas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtCantidad, txtNombre, txtPrecio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCantidad = itemView.findViewById(R.id.txtCantidadItemCuenta);
            txtNombre = itemView.findViewById(R.id.txtNombreItemCuenta);
            txtPrecio = itemView.findViewById(R.id.txtPrecioItemCuenta);
        }
    }
}