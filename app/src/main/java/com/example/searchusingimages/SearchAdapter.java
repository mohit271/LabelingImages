package com.example.searchusingimages;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private final Context context;
     ArrayList<SearchModel> ListSearchModel;
    // itemClicked listener;

//    public SearchAdapter(itemClicked listener) {
//        this.listener = listener;
//    }

   public SearchAdapter(Context context, ArrayList<SearchModel> searchModel) {
         this.context = context;
        this.ListSearchModel = searchModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_rv,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final SearchModel searchModel = ListSearchModel.get(position);
        holder.titleTv.setText(searchModel.getTitle());
        holder.descriptionTV.setText(searchModel.getSnippet());
        holder.snippetTv.setText(searchModel.getLink());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse(searchModel.getLink()));
//                context.startActivity(intent);
                String str =searchModel.getLink();
                Intent intent = new Intent(context.getApplicationContext(), WebViewActivity.class);
                intent.putExtra("urlLink", str);
//
//        // start the Intent
      context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
     //   if(ListSearchModel!=null)
        return ListSearchModel.size();
       // return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
         TextView titleTv,snippetTv,descriptionTV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.tvTitle);
            snippetTv= itemView.findViewById(R.id.tvSnippet);
            descriptionTV= itemView.findViewById(R.id.tvDescription);
        }
    }

}
