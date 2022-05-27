package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductRVAdapter extends RecyclerView.Adapter<ProductRVAdapter.ViewHolder> {

    // variable for our array list and context.
    private ArrayList<Product> productArrayList;
    private Context context;
    public static String name, price, check;
    public static Drawable photo;

    // creating a constructor.
    public ProductRVAdapter(ArrayList<Product> productArrayList, Context context) {
        this.productArrayList = productArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflating our layout file on below line.
        View view = LayoutInflater.from(context).inflate(R.layout.user_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // getting data from our array list in our modal class.
        Product product = productArrayList.get(position);

        // on the below line we are setting data to our text view.
        holder.productNameTV.setText(product.getProduct_name());
        holder.productNameTV.setTypeface(null, Typeface.BOLD);
        holder.priceTV.setText(product.getPrice());
        if (product.getCheck().contains("https://")){
            holder.checkTV.setTextIsSelectable(true);
            String dynamicUrl = product.getCheck();
            String linkedText = String.format(
                    "<a href=\"%s\">"+dynamicUrl+"</a> ", dynamicUrl);
            holder.checkTV.setText(Html.fromHtml(linkedText));
            holder.checkTV.setLinksClickable(true);
            holder.checkTV.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            holder.checkTV.setText(product.getCheck());
        }

        // on below line we are loading our image from the URL
        // in our image view using Picasso.
        Picasso.get().load(product.getPhoto()).into(holder.userIV);
    }

    @Override
    public int getItemCount() {
        // returning the size of array list.
        return productArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // creating a variable for our text view and image view.
        private TextView productNameTV, priceTV, checkTV;
        private ImageView userIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            // initializing our variables.
            productNameTV = itemView.findViewById(R.id.idTVName);
            priceTV = itemView.findViewById(R.id.idTVPrice);
            checkTV = itemView.findViewById(R.id.idTVCheck);
            userIV = itemView.findViewById(R.id.idIVUser);
        }

        @Override
        public void onClick(View view) {
            name = productNameTV.getText().toString();
            price = priceTV.getText().toString();
            check = checkTV.getText().toString();
            photo = userIV.getDrawable();
            Intent intent = new Intent(context, ProductActivity.class);
            context.startActivity(intent);
            //Toast.makeText(context.getApplicationContext(), productNameTV.getText(), Toast.LENGTH_SHORT).show();
        }
    }
}


