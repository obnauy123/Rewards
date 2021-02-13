package csc492.Bo_Y.rewards;
import androidx.annotation.NonNull;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class recordAdapter extends RecyclerView.Adapter<recordHolder> {
    private ArrayList<Award_record> list;
    private login_profile mainActivity;
    public recordAdapter(ArrayList<Award_record> list, login_profile mainActivity){
        this.list = list;
        this.mainActivity = mainActivity;

    }
    @NonNull
    @Override
    public recordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_entry, parent,false);
        item.setOnClickListener(this.mainActivity);
        item.setOnLongClickListener(this.mainActivity);
        return new recordHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull recordHolder holder, int position) {
        Award_record record = list.get(position);
        String date = record.getDate();
        String name = record.getName();
        int points = record.getPoints();
        String comment = record.getComment();
        holder.name.setText(name);
        holder.date.setText(date);
        holder.points.setText(""+points);
        holder.comment.setText(comment);
    }

    @Override
    public int getItemCount() {
        return list!= null ? list.size() : 0;
    }
}
