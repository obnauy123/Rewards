package csc492.Bo_Y.rewards;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class LeaderHolder extends RecyclerView.ViewHolder {
    TextView name;
    TextView title;
    TextView points;
    ImageView image;
    public LeaderHolder(View view){
        super(view);
        this.name = view.findViewById(R.id.leader_name);
        this.title = view.findViewById(R.id.leader_title);
        this.points = view.findViewById(R.id.leader_points);
        this.image = view.findViewById(R.id.leader_image);
    }
}
