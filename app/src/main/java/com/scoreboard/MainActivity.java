package com.scoreboard;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView sortArrowIV;
    private ListView playerAndScoreLV;
    private TextView emptyTV;
    private DBHelper db;
    private List<Player> playerList;
    private ScoreListAdapter scoreListAdapter;

    // Sorting type
    private boolean isDescending;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout sortButtonLL = findViewById(R.id.sortButtonLL);
        sortButtonLL.setOnClickListener(this);
        sortArrowIV = findViewById(R.id.sortArrowIV);
        playerAndScoreLV = findViewById(R.id.playerAndScoreLV);
        FloatingActionButton addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(this);
        emptyTV = findViewById(R.id.emptyTV);

        // Default sorting type is descending
        isDescending = true;

        db = new DBHelper(this);

        playerList = new ArrayList<>();

        // Retrieve all data from database
        try {
            playerList = db.getAllPlayers();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // If there is no player, show empty message on screen
        if (playerList.isEmpty()) {
            emptyTV.setVisibility(View.VISIBLE);
        }

        // Sorting the list descending
        sortListDSC(playerList);

        // Load Adapter and List
        loadAdapterAndList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addButton:
                showDialog();
                break;
            case R.id.sortButtonLL:
                if (isDescending) {
                    sortListASC(playerList);
                    scoreListAdapter.notifyDataSetChanged();
                    isDescending = false;
                    sortArrowIV.setImageDrawable(getResources()
                            .getDrawable(R.drawable.ic_keyboard_arrow_up_white_24dp));
                } else {
                    sortListDSC(playerList);
                    scoreListAdapter.notifyDataSetChanged();
                    isDescending = true;
                    sortArrowIV.setImageDrawable(getResources()
                            .getDrawable(R.drawable.ic_keyboard_arrow_down_white_24dp));
                }
                break;
        }
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_score);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setWindowAnimations(R.style.DialogInOut);

        final EditText nameET = dialog.findViewById(R.id.nameET);
        final EditText scoreET = dialog.findViewById(R.id.scoreET);

        FrameLayout removeButton = dialog.findViewById(R.id.dialog_remove_button);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button okButton = dialog.findViewById(R.id.dialog_ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameET.getText().toString().isEmpty() && scoreET.getText().toString().isEmpty()) { // If both edittext are empty
                    nameET.setError(getResources().getString(R.string.empty_text));
                    scoreET.setError(getResources().getString(R.string.empty_text));
                } else if (nameET.getText().toString().isEmpty()) { // If name edittext is empty
                    nameET.setError(getResources().getString(R.string.empty_text));
                } else if (scoreET.getText().toString().isEmpty()) { // If score edittext is empty
                    scoreET.setError(getResources().getString(R.string.empty_text));
                } else { // Name and score edittext are correct.
                    // Create a new player
                    Player player = new Player(nameET.getText().toString()
                            , Integer.parseInt(scoreET.getText().toString()));

                    // Database operations...
                    try {

                        if (!db.isPlayerExists(player)) {
                            // Player doesn't exist in database. Add player...
                            db.addNewPlayer(player);
                        } else { // Player already exists in database.

                            // Get current score of the player
                            int currentScore = Integer.parseInt(db.getPlayerScore(player.getName()));

                            // If player new score is greater than current score, update the score and show message on screen.
                            if (player.getScore() > currentScore) {
                                db.updatePlayerScore(player);
                                Toast.makeText(getApplicationContext()
                                        , getResources().getString(R.string.score_updated)
                                        , Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext()
                                        , getResources().getString(R.string.score_already_higher)
                                        , Toast.LENGTH_SHORT).show();
                            }
                        }

                        // Retrieve all data from database
                        playerList = db.getAllPlayers();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Sorting type
                    if (isDescending) {
                        sortListDSC(playerList);
                    } else {
                        sortListASC(playerList);
                    }

                    // Refresh the list
                    loadAdapterAndList();

                    // List is not empty now. Empty text has to gone.
                    emptyTV.setVisibility(View.GONE);

                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    // List is loading or refreshing
    private void loadAdapterAndList() {
        scoreListAdapter = new ScoreListAdapter(this, R.layout.score_row, playerList);
        playerAndScoreLV.setAdapter(scoreListAdapter);
    }

    // Sort List Descending
    private void sortListDSC (List<Player> playerList) {
        Collections.sort(playerList, new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                if (o1.getScore() > o2.getScore())
                    return -1;
                else if (o1.getScore() < o2.getScore())
                    return 1;
                else
                    return 0;
            }
        });
    }

    // Sort List Ascending
    private void sortListASC (List<Player> playerList) {
        Collections.sort(playerList, new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                if (o1.getScore() > o2.getScore())
                    return 1;
                else if (o1.getScore() < o2.getScore())
                    return -1;
                else
                    return 0;
            }
        });
    }
}
