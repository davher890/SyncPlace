package com.syncplace;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.syncplace.v2.R;

public class FavoritoListAdapter extends BaseAdapter {
	
	private Activity activity;
	List<Lugar> favoritos;
	private static LayoutInflater inflater=null;
	
	public FavoritoListAdapter(Activity a, List<Lugar> favoritos) {
		activity = a;
		this.favoritos = favoritos;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return favoritos.size();
	}

	@Override
	public Object getItem(int position) {
		return favoritos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return favoritos.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi=convertView;
        ViewHolder holder;
        if(convertView==null){
            vi = inflater.inflate(R.layout.elementolista, null);
            holder=new ViewHolder();
            holder.titulo = (TextView)vi.findViewById(R.id.LblTitulo);
            holder.subtitulo = (TextView)vi.findViewById(R.id.LblSubTitulo);
            vi.setTag(holder);
        }else{
            holder = (ViewHolder)vi.getTag();
        }

        Lugar item = new Lugar();
        item = favoritos.get(position);

        holder.titulo.setText(item.getNombre());
        holder.subtitulo.setText(item.getDescripcion());
        return vi;
	}
	
	public class ViewHolder {
        TextView titulo;
        TextView subtitulo;        
	}
}