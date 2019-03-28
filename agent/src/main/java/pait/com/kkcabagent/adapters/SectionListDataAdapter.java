package pait.com.kkcabagent.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import pait.com.kkcabagent.interfaces.ActivityFragmentInterface;
import pait.com.kkcabdriver.R;
import pait.com.kkcabagent.model.SingleItemModel;
import pait.com.kkcabagent.model.TripDetailClass;

public class SectionListDataAdapter extends RecyclerView.Adapter<SectionListDataAdapter.SingleItemRowHolder> {

    private ArrayList<SingleItemModel> itemsList;
    private Context mContext;
    private ActivityFragmentInterface listener;
    private SingleItemModel singleItem;

    SectionListDataAdapter(Context context, ArrayList<SingleItemModel> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
    }

    public void setOnDataListener(ActivityFragmentInterface _interface){
        listener = _interface;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_single_card, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {
        singleItem = itemsList.get(i);
        holder.tvTitle.setText(singleItem.getName());
        Glide.with(mContext)
                .load(singleItem.getUrl())
                .into(holder.itemImage);
    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    class SingleItemRowHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        ImageView itemImage;

        SingleItemRowHolder(View view) {
            super(view);

            this.tvTitle = view.findViewById(R.id.tvTitle);
            this.itemImage = view.findViewById(R.id.img_veh);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TripDetailClass trip = new TripDetailClass();
                    trip.setVehName(tvTitle.getText().toString());
                    trip.setVehImgName(singleItem.getUrl());
                    listener.onCallBack(trip);
                    //Toast.makeText(v.getContext(), tvTitle.getText(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
