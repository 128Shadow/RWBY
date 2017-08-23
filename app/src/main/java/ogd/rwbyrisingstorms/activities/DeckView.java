package ogd.rwbyrisingstorms.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ogd.rwbyrisingstorms.R;
import ogd.rwbyrisingstorms.async.QueryBuilder;
import ogd.rwbyrisingstorms.async.SimpleQueryBuilder;
import ogd.rwbyrisingstorms.contracts.DeckContract;
import ogd.rwbyrisingstorms.entities.Card;
import ogd.rwbyrisingstorms.managers.DeckManager;
import ogd.rwbyrisingstorms.providers.DeckProvider;

public class DeckView extends AppCompatActivity implements QueryBuilder.IQueryListener {

    private DeckManager deckManager;

    private static SimpleCursorAdapter curAdapter;
    private static SimpleAdapter currAdapter;
    private static TwoWayView cardListView;
    private static Cursor cards;
    private static Activity current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_view);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        current = this;

        cardListView = (TwoWayView) findViewById(R.id.list);

        deckManager = new DeckManager(this);

        /*
        Card test = new Card("ruby", "ruby");
        deckManager.persistAsync(test);
        test = new Card("wiess", "wiess");
        deckManager.persistAsync(test);
        test = new Card("blake", "blake");
        deckManager.persistAsync(test);
        test = new Card("yang", "yang");
        deckManager.persistAsync(test);
        */

        deckManager.getDeckAsync(this);
    }

    private void setListener() {
        cardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                cards.moveToPosition(i);
                Log.i("Main", "clicked: " + cards.getString(cards.getColumnIndex(DeckContract.TITLE)));
                Card card = new Card(cards.getString(cards.getColumnIndex(DeckContract.TITLE)), cards.getString(cards.getColumnIndex(DeckContract.IMG)));
                Intent send = new Intent(current, CardView.class);
                send.putExtra("card", card);

                startActivity(send);
            }
        });
    }

    @Override
    public void handleResults(Cursor cursor) {
        cards = cursor;
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();

            //String[] from = new String[] { DeckProvider.KEY_TITLE };
            int[] to = new int[] { R.id.imageView, R.id.deck_card_title };

            List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
            Map<String, Object> datum = new HashMap<String, Object>(2);

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                datum = new HashMap<String, Object>(2);
                datum.put("thumbnail", getResources().getIdentifier(
                        cursor.getString(cursor.getColumnIndex(DeckContract.IMG)),
                        "drawable", this.getPackageName()));
                datum.put("name", cursor.getString(cursor.getColumnIndex(DeckContract.TITLE)));
                data.add(datum);
            }

            currAdapter = new SimpleAdapter(this, data, R.layout.deck_card, new String[] {"thumbnail","name"}, to);
            //curAdapter = new SimpleCursorAdapter(this, R.layout.deck_card, cursor, from, to, 0);

            cardListView.setAdapter(currAdapter);
            setListener();
        }
    }

    @Override
    public void closeResults() {
    }

}
