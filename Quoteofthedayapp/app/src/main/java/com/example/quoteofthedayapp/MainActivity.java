package com.example.quoteofthedayapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private TextView quoteTextView;
    private Button refreshButton, shareButton, favoriteButton, deleteAllButton;
    private LinearLayout favoritesContainer;

    private String[] quotes = {
            "The only way to do great work is to love what you do. - Steve Jobs",
            "Believe you can and you're halfway there. - Theodore Roosevelt",
            "The future belongs to those who believe in the beauty of their dreams. - Eleanor Roosevelt",
            "The greatest glory in living lies not in never falling, but in rising every time we fall. -Nelson Mandela",
            "The way to get started is to quit talking and begin doing. -Walt Disney",
            "Your time is limited, so don't waste it living someone else's life. Don't be trapped by dogma – which is living with the results of other people's thinking. -Steve Jobs",
            "The future belongs to those who believe in the beauty of their dreams. -Eleanor Roosevelt",
            "If you look at what you have in life, you'll always have more. If you look at what you don't have in life, you'll never have enough. -Oprah Winfrey",
            "If you set your goals ridiculously high and it's a failure, you will fail above everyone else's success. -James Cameron",
            "You may say I'm a dreamer, but I'm not the only one. I hope someday you'll join us. And the world will live as one. -John Lennon",
            "You must be the change you wish to see in the world. -Mahatma Gandhi",
            "Spread love everywhere you go. Let no one ever come to you without leaving happier. -Mother Teresa",
            "The only thing we have to fear is fear itself. -Franklin D. Roosevelt",
            "Darkness cannot drive out darkness: only light can do that. Hate cannot drive out hate: only love can do that. -Martin Luther King Jr.",
            "Do one thing every day that scares you. -Eleanor Roosevelt",
            "Well done is better than well said. -Benjamin Franklin",
            "The best and most beautiful things in the world cannot be seen or even touched - they must be felt with the heart. -Helen Keller",
            "It is during our darkest moments that we must focus to see the light. -Aristotle",
            "Do not go where the path may lead, go instead where there is no path and leave a trail. -Ralph Waldo Emerson",
            "Be yourself; everyone else is already taken. -Oscar Wilde",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        quoteTextView = findViewById(R.id.quoteTextView);
        favoritesContainer = findViewById(R.id.favoritesContainer);
        refreshButton = findViewById(R.id.refreshButton);
        shareButton = findViewById(R.id.shareButton);
        favoriteButton = findViewById(R.id.favoriteButton);
        deleteAllButton = findViewById(R.id.deleteAllButton);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRandomQuote();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareQuote();
            }
        });

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToFavorites();
            }
        });

        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAllFavorites();
            }
        });

        // Load favorites initially
        loadFavoriteQuotes();
    }

    private void showRandomQuote() {
        Random random = new Random();
        int index = random.nextInt(quotes.length);
        quoteTextView.setText(quotes[index]);
    }

    private void shareQuote() {
        String currentQuote = quoteTextView.getText().toString();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, currentQuote);

        startActivity(Intent.createChooser(shareIntent, "Share Quote"));
    }

    private void saveToFavorites() {
        String currentQuote = quoteTextView.getText().toString();

        SharedPreferences sharedPreferences = getSharedPreferences("favorite_quotes", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> favorites = sharedPreferences.getStringSet("favorite_quotes", new HashSet<String>());
        if (favorites == null) {
            favorites = new HashSet<>();
        }
        favorites.add(currentQuote);

        editor.putStringSet("favorite_quotes", favorites);
        editor.apply();

        Toast.makeText(this, "Quote added to favorites!", Toast.LENGTH_SHORT).show();
        loadFavoriteQuotes(); // Refresh favorites list
    }

    private void loadFavoriteQuotes() {
        favoritesContainer.removeAllViews();

        SharedPreferences sharedPreferences = getSharedPreferences("favorite_quotes", Context.MODE_PRIVATE);
        Set<String> favorites = sharedPreferences.getStringSet("favorite_quotes", new HashSet<String>());

        if (favorites != null && !favorites.isEmpty()) {
            for (final String quote : favorites) {
                TextView textView = new TextView(this);
                textView.setText(quote);
                textView.setTextSize(16);
                textView.setPadding(0, 10, 0, 10);

                // Add a delete button next to each favorite quote
                Button deleteButton = new Button(this);
                deleteButton.setText("Delete");
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeFromFavorites(quote);
                    }
                });

                LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                buttonLayoutParams.setMargins(8, 0, 0, 0); // Adjust margins as needed

                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.addView(textView);
                linearLayout.addView(deleteButton, buttonLayoutParams);

                favoritesContainer.addView(linearLayout);
            }
        } else {
            // Display a message if no favorites are saved
            TextView textView = new TextView(this);
            textView.setText("No favorite quotes saved.");
            textView.setTextSize(16);
            textView.setPadding(0, 10, 0, 10);
            favoritesContainer.addView(textView);
        }
    }

    private void removeFromFavorites(String quoteToRemove) {
        SharedPreferences sharedPreferences = getSharedPreferences("favorite_quotes", Context.MODE_PRIVATE);
        Set<String> favorites = sharedPreferences.getStringSet("favorite_quotes", new HashSet<String>());

        if (favorites != null) {
            favorites.remove(quoteToRemove);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet("favorite_quotes", favorites);
            editor.apply();

            Toast.makeText(this, "Quote removed from favorites!", Toast.LENGTH_SHORT).show();

            // Refresh favorites list
            loadFavoriteQuotes();
        }
    }

    private void deleteAllFavorites() {
        SharedPreferences sharedPreferences = getSharedPreferences("favorite_quotes", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(this, "All favorite quotes deleted!", Toast.LENGTH_SHORT).show();

        // Refresh favorites list
        loadFavoriteQuotes();
    }
}
