package com.lumpyslounge.gardenjournal.UI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.lumpyslounge.gardenjournal.R;
import com.squareup.picasso.Picasso;

import com.lumpyslounge.gardenjournal.Model.Model.Plant;
import com.lumpyslounge.gardenjournal.Util.DateFormatter;

public class PlantAdapter extends FirestoreRecyclerAdapter<Plant, PlantAdapter.ViewHolder>
{
    private OnItemClickListener listener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See
     * {@link FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public PlantAdapter(FirestoreRecyclerOptions<Plant> options)
    {
        super(options);
    }

    @Override
    protected void onBindViewHolder(ViewHolder viewHolder, int position, Plant plant)
    {
        String imageUrl = plant.getImageUrl();
        viewHolder.title.setText(plant.getName());
        viewHolder.date.setText(DateFormatter.getStringFromTimestamp(plant.getDatePlanted()));

        Picasso.get().load(imageUrl).placeholder(R.drawable.daffodils).error(R.drawable.daffodils).
                fit().into(viewHolder.photo);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card,parent,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView title;
        public TextView date;
        public ImageView photo;



        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            title = itemView.findViewById(R.id.textView_item_card_title);
            date = itemView.findViewById(R.id.textView_item_card_date);
            photo = itemView.findViewById(R.id.imageView_item_card_photo);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener !=null)
                    {
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);
                    }

                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;

    }
}
