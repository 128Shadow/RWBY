package ogd.rwbyrisingstorms.managers;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.Set;

import ogd.rwbyrisingstorms.async.AsyncContentResolver;
import ogd.rwbyrisingstorms.async.IContinue;
import ogd.rwbyrisingstorms.async.IEntityCreator;
import ogd.rwbyrisingstorms.async.QueryBuilder;
import ogd.rwbyrisingstorms.async.SimpleQueryBuilder;
import ogd.rwbyrisingstorms.contracts.DeckContract;
import ogd.rwbyrisingstorms.entities.Card;

import static android.R.attr.id;
import static android.content.ContentValues.TAG;

/**
 * Created by luismeneses on 7/12/17.
 */

public class DeckManager extends Manager<Card> {

    private static final int LOADER_ID = 1;

    private SimpleQueryBuilder sqb;

    private static final IEntityCreator<Card> creator = new IEntityCreator<Card>() {
        @Override
        public Card create(Cursor cursor) {
            return new Card(cursor);
        }
    };

    private AsyncContentResolver contentResolver;

    private Context view;

    public DeckManager(Context context) {
        super(context, creator, LOADER_ID);
        contentResolver = new AsyncContentResolver(context.getContentResolver());
    }

    public void getDeckAsync(QueryBuilder.IQueryListener listener) {
        SimpleQueryBuilder sqb = new SimpleQueryBuilder(null, listener);
        contentResolver.queryAsync(DeckContract.CONTENT_URI, new String[] { DeckContract.ID, DeckContract.TITLE, DeckContract.IMG }, null, null, null, sqb);
    }

    public void persistAsync(Card card) {
        ContentValues values = new ContentValues();
        String title = card.title;
        String img = card.img;
        values.put(DeckContract.TITLE, title);
        values.put(DeckContract.IMG, img);

        contentResolver.insertAsync(DeckContract.CONTENT_URI, values, null);
    }

}
