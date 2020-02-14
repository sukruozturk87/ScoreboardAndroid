package com.scoreboard;

import android.content.Context;
import java.util.List;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ScoreListAdapter extends ArrayAdapter<Player> {

    private Context context;

    private List<Player> playerList;

    public ScoreListAdapter (Context context, int resource, List<Player> playerList) {
        super(context, resource, playerList);
        this.context = context;
        this.playerList = playerList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        row = inflater.inflate(R.layout.score_row, parent, false);

        TextView rowNumberTV = row.findViewById(R.id.rowNumberTV);
        TextView playernameTV = row.findViewById(R.id.playernameTV);
        TextView scoreTV = row.findViewById(R.id.scoreTV);

        rowNumberTV.setText(position+1 + ".");

        Player player = playerList.get(position);

        playernameTV.setText(player.getName());
        scoreTV.setText(String.valueOf(player.getScore()));

        return row;
    }
}
