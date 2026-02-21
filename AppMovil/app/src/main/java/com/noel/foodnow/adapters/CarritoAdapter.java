package com.noel.foodnow.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.noel.foodnow.R;
import com.noel.foodnow.models.CarritoManager;
import java.math.BigDecimal;
import java.util.List;

public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.ViewHolder> {

    private List<CarritoManager.ProductoPedido> lista;
    private OnCarritoCambiadoListener listener;

    // Interfaz para avisar a la pantalla de que el total en euros ha cambiado
    public interface OnCarritoCambiadoListener {
        void onTotalCambiado();
    }

    public CarritoAdapter(List<CarritoManager.ProductoPedido> lista, OnCarritoCambiadoListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carrito, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CarritoManager.ProductoPedido item = lista.get(position);

        holder.txtNombre.setText(item.getProducto().getNombre());
        holder.txtCantidad.setText(String.valueOf(item.getCantidad()));

        BigDecimal precioUnitario = item.getProducto().getPrecio();
        BigDecimal cantidad = new BigDecimal(item.getCantidad());
        BigDecimal totalItem = precioUnitario.multiply(cantidad);

        holder.txtPrecioTotal.setText(String.format("%.2f €", totalItem.doubleValue()));

        // --- FUNCIONES DE LOS BOTONES ---
        holder.btnMas.setOnClickListener(v -> {
            CarritoManager.getInstance().agregarProducto(item.getProducto(), 1);
            notifyDataSetChanged();
            listener.onTotalCambiado(); // Recalcula el total de la pantalla
        });

        holder.btnMenos.setOnClickListener(v -> {
            CarritoManager.getInstance().restarCantidad(item.getProducto());
            notifyDataSetChanged();
            listener.onTotalCambiado();
        });

        holder.btnEliminar.setOnClickListener(v -> {
            CarritoManager.getInstance().eliminarProducto(item.getProducto());
            notifyDataSetChanged();
            listener.onTotalCambiado();
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtCantidad, txtPrecioTotal;
        Button btnMas, btnMenos;
        ImageButton btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreCarrito);
            txtCantidad = itemView.findViewById(R.id.txtCantidadCarrito);
            txtPrecioTotal = itemView.findViewById(R.id.txtPrecioTotalCarrito);
            btnMas = itemView.findViewById(R.id.btnMasCarrito);
            btnMenos = itemView.findViewById(R.id.btnMenosCarrito);
            btnEliminar = itemView.findViewById(R.id.btnEliminarCarrito);
        }
    }
}