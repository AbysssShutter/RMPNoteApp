package com.ikit.rmpnoteapp.ui.joke;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ikit.rmpnoteapp.R;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JokesOnYou extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_jokes_on_you);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.jokeMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView textJokeSet = findViewById(R.id.jokeTextViewPart1);
        TextView textJokeDel = findViewById(R.id.jokeTextViewPart2);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://v2.jokeapi.dev/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JokeApi jokeApi = retrofit.create(JokeApi.class);

        jokeApi.getRandomJoke().enqueue(new retrofit2.Callback<Joke>() {
            @Override
            public void onResponse(Call<Joke> call, Response<Joke> response) {
                Joke joke = response.body();
                if (joke != null) {
                    textJokeSet.setText(joke.getSetup());
                    textJokeDel.setText(joke.getDelivery());
                }
            }
            @Override
            public void onFailure(Call<Joke> call, Throwable t) {}
        });
    }
}