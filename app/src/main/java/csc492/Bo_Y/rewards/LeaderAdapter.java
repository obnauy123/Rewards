package csc492.Bo_Y.rewards;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LeaderAdapter extends RecyclerView.Adapter<LeaderHolder>{

    private ArrayList<Leader> list;
    private leaderboard mainActivity;
    public LeaderAdapter(ArrayList<Leader> list, leaderboard mainActivity){
        this.list = list;
        this.mainActivity = mainActivity;

    }
    @NonNull
    @Override
    public LeaderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.leader_entry, parent,false);
        item.setOnClickListener(this.mainActivity);
        item.setOnLongClickListener(this.mainActivity);
        return new LeaderHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderHolder holder, int position) {
        Leader leader = list.get(position);
        String pos = leader.getPosition();
        String name = leader.getName();
        int points = leader.getPoints();
        String imageString = leader.getImageString();

        holder.name.setText(name);
        holder.title.setText(pos);
        holder.points.setText(String.valueOf(points));

        try{
            byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.image.setImageBitmap(bitmap);
        } catch (Exception e){
            holder.image.setImageResource(R.drawable.default_photo);
        }


    }

    @Override
    public int getItemCount() {
        return list!= null ? list.size() : 0;
    }


}
