package csc492.Bo_Y.rewards;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class recordHolder extends RecyclerView.ViewHolder {
    TextView name;
    TextView date;
    TextView points;
    TextView comment;
    public recordHolder(View view){
        super(view);
        this.name = view.findViewById(R.id.name);
        this.date = view.findViewById(R.id.date);
        this.points = view.findViewById(R.id.points);
        this.comment = view.findViewById(R.id.comment);
    }
}
