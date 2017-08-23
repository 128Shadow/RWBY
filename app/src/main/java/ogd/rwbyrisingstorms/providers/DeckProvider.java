package ogd.rwbyrisingstorms.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import ogd.rwbyrisingstorms.contracts.DeckContract;

import static android.content.ContentValues.TAG;

/**
 * Created by luismeneses on 7/12/17.
 */

public class DeckProvider extends ContentProvider {
    public DeckProvider() {
    }

    private static final String AUTHORITY = DeckContract.AUTHORITY;

    private static final String CONTENT_PATH = DeckContract.CONTENT_PATH;

    private static final String CONTENT_PATH_ITEM = DeckContract.CONTENT_PATH_ITEM;


    private static final String DATABASE_NAME = "deck.db";

    private static final int DATABASE_VERSION = 1;

    private static final String CARD_TABLE = "cards";

    private static final String DECK_TABLE = "deck";

    public static final String KEY_ROWID = "_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_IMG = "img";

    // Create the constants used to differentiate between the different URI  requests.
    private static final int ALL_ROWS = 1;
    private static final int SINGLE_ROW = 2;

    private static final String DATABASE_CREATE = "create table deck (_id integer primary key autoincrement, "
            + "title text, img text);";

    public static class DbHelper extends SQLiteOpenHelper {

        private static final String DATABASE_DROP = "DROP TABLE IF EXISTS " + DECK_TABLE;

        public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DATABASE_DROP);
            onCreate(db);
        }
    }

    private DbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        return true;
    }

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, CONTENT_PATH, ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, CONTENT_PATH_ITEM, SINGLE_ROW);
    }

    @Override
    public String getType(Uri uri) {
        switch(uriMatcher.match(uri)) {
            case ALL_ROWS:
                return "vnd.android.cursor.dir/deck";
            case SINGLE_ROW:
                return "vnd.android.cursor.item/deck";
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case ALL_ROWS:
                long row = db.insert(DECK_TABLE, null, values);
                if(row > 0) {
                    Uri instanceUri = DeckContract.CONTENT_URI(row);
                    ContentResolver cr = getContext().getContentResolver();
                    cr.notifyChange(instanceUri, null);

                    return instanceUri;
                }
            case SINGLE_ROW:
                throw new IllegalArgumentException("insert expects a whole-table URI");
            default:
                throw new IllegalStateException("insert: bad case");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case ALL_ROWS:
                Cursor test = db.query(DECK_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                return test;
            case SINGLE_ROW:
                String rowId = uri.getPathSegments().get(1);
                return db.query(DECK_TABLE, projection,
                        DeckContract.ID + "=" + Long.parseLong(uri.getPathSegments().get(1)), selectionArgs, null, null, sortOrder);
            default:
                throw new IllegalStateException("insert: bad case");
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new IllegalStateException("Update of Deck not supported");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DECK_TABLE, selection, selectionArgs);
        return 0;
    }

}
