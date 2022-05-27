package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_page);
        TextView name = findViewById(R.id.idTVProductName2);
        TextView price = findViewById(R.id.idTVPrice2);
        TextView check = findViewById(R.id.idTVcheck2);
        ImageView photo = findViewById(R.id.idIVUser2);
        name.setText(ProductRVAdapter.name);
        name.setTypeface(null, Typeface.BOLD);
        price.setText(ProductRVAdapter.price);
        check.setText(ProductRVAdapter.check);
        photo.setImageDrawable(ProductRVAdapter.photo);
        EditText text = findViewById(R.id.Message);
        if (check.getText().toString().contains("https://")) {
            check.setTextIsSelectable(true);
            String dynamicUrl = check.getText().toString();
            String linkedText = String.format(
                    "<a href=\"%s\">" + dynamicUrl + "</a> ", dynamicUrl);
            check.setText(Html.fromHtml(linkedText));
            check.setLinksClickable(true);
            check.setMovementMethod(LinkMovementMethod.getInstance());
            text.setText("Спасибо за покупку " + name.getText() + "!\r\n \uD83E\uDDFE Чек можно скачать тут: " + check.getText() + "\r\nЖдем Вас снова!");
        } else {
            text.setText("Спасибо за покупку " + name.getText() + "!\r\nЖдем Вас снова!");
        }
        EditText number = findViewById(R.id.editTextPhone);
        Button send = findViewById(R.id.buttonSend);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (number.getText().length() == 11) {
                    StringBuilder phone = new StringBuilder(number.getText().toString());
                    phone.replace(0, 1, "7");
                    Toast.makeText(ProductActivity.this, phone, Toast.LENGTH_SHORT).show();
                    Intent browserIntent = new
                            Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send/?phone=" + phone + "&text=" + text.getText()));
                    startActivity(browserIntent);
                }
            }
        });
    }
}