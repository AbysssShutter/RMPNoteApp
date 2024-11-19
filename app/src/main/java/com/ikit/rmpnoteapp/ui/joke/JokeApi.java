package com.ikit.rmpnoteapp.ui.joke;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JokeApi {
    @GET("joke/Any?blacklistFlags=nsfw,religious,political,racist,sexist,explicit&type=twopart")
    public Call<Joke> getRandomJoke();
}