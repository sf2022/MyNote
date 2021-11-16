package cn.edu.bistu.mynote.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import cn.edu.bistu.mynote.Bean.Note;
import cn.edu.bistu.mynote.R;

public class ListAdapter extends ArrayAdapter<Note> {
    private final int resourceId;
    public ListAdapter(Context context, int textViewResourceId, List<Note> objects) {
        super(context, textViewResourceId, objects);
        this.resourceId=textViewResourceId;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Note nt = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder =new ViewHolder();
            viewHolder.author = view.findViewById(R.id.author);
            viewHolder.date = view.findViewById(R.id.date);
            viewHolder.title=view.findViewById(R.id.title);
            view.setTag(viewHolder);//将viewholder存储在view里
        }
        else {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();//重新获取viewholder
        }
        viewHolder.author.setText(nt.getAuthor());
        viewHolder.date.setText(nt.getDate());
        viewHolder.title.setText(nt.getTitle());
        return view;
    }
    class ViewHolder{
        TextView date;
        TextView author;
        TextView title;
    }
}
