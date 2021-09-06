package com.blazeminds.pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blazeminds.pos.R;
import com.blazeminds.pos.autocomplete_resource.MyObject;

import java.util.ArrayList;
import java.util.List;

public class SingleProductFilterRecylarAdapter extends RecyclerView.Adapter<SingleProductFilterRecylarAdapter.ViewHolder> {


    Context con;
    int inc = 0;
    private List<MyObject> dataSelected = new ArrayList<>();
    View.OnClickListener mClickListener;

    public void setClickListener(View.OnClickListener callback) {
        mClickListener = callback;
    }
    public SingleProductFilterRecylarAdapter(List<MyObject> dataSelected, Context con) {
        this.dataSelected = dataSelected;
        this.con = con;
    }

    public void filterList(List<MyObject> filterdNames) {
        this.dataSelected = filterdNames;
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_filter_single_name_recylarview_layout,
                parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final MyObject dataModel = dataSelected.get(i);
        viewHolder.name.setText(dataModel.getObjectName());
//        String produt = dataModel.getProddesc();
//        Log.d("produt", produt);
        if (dataModel.isSelected()) {
            viewHolder.name.setTextColor(con.getResources().getColor(R.color.white));
            viewHolder.ll.setBackgroundColor(con.getResources().getColor(R.color.colorPrimaryDark));
        } else {
            viewHolder.name.setTextColor(con.getResources().getColor(R.color.black));
            viewHolder.ll.setBackgroundColor(con.getResources().getColor(R.color.white));
        }
        viewHolder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> data = new ArrayList<String>();

                dataModel.setSelected(true);
                data.add(dataModel.getObjectId());
                data.add(dataModel.getObjectName());
                v.setTag(data);
                mClickListener.onClick(v);
          /*  if (inc == 0) {
                inc = 1;
                viewHolder.name.setTextColor(con.getResources().getColor(R.color.white));
                viewHolder.ll.setBackgroundColor(con.getResources().getColor(R.color.blue));
            } else {
                inc = 0;

                viewHolder.name.setTextColor(con.getResources().getColor(R.color.black));
                viewHolder.ll.setBackgroundColor(con.getResources().getColor(R.color.white));
            }
            */
            }
        });

    }


    @Override
    public int getItemCount() {
        return dataSelected.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public LinearLayout ll;

        private ViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.search_filter_recylar_main_hd_tv);
            ll = view.findViewById(R.id.search_filter_recylar_main_layout_ll);

        }
    }

    public List<MyObject> getList() {
        return this.dataSelected;
    }
}
