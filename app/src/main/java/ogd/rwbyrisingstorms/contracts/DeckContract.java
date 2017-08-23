package ogd.rwbyrisingstorms.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.regex.Pattern;

/**
 * Created by luismeneses on 7/12/17.
 */

public class DeckContract implements BaseColumns {

    public static final String AUTHORITY = "ogd.rwbyrisingstorms";

    public static final Uri CONTENT_URI(String authority, String path) {
        return new Uri.Builder().scheme("content")
                .authority(authority)
                .path(path)
                .build();
    }

    public static final Uri CONTENT_URI = CONTENT_URI(AUTHORITY, "Deck");

    public static Uri withExtendedPath(Uri uri,
                                       String... path) {
        Uri.Builder builder = uri.buildUpon();
        for (String p : path)
            builder.appendPath(p);
        return builder.build();
    }

    public static final Uri CONTENT_URI(long id) {
        return CONTENT_URI(Long.toString(id));
    }

    public static final Uri CONTENT_URI(String id) {
        return withExtendedPath(CONTENT_URI, id);
    }

    public static final long getId(Uri uri) {
        return Long.parseLong(uri.getLastPathSegment());
    }

    public static final String CONTENT_PATH(Uri uri) {
        return uri.getPath().substring(1);
    }

    public static final String CONTENT_PATH = CONTENT_PATH(CONTENT_URI);

    public static final String CONTENT_PATH_ITEM = CONTENT_PATH(CONTENT_URI("#"));


    public static final String ID = "_id";

    public static final String TITLE = "title";

    public static final String IMG = "img";

    private static int titleColumn = -1;

    private static int imgColumn = -1;

    public static String getTitle(Cursor cursor) {
        if (titleColumn < 0) {
            titleColumn = cursor.getColumnIndexOrThrow(TITLE);
        }
        return cursor.getString(titleColumn);
    }

    public static void putTitle(ContentValues values, String title) {
        values.put(TITLE, title);
    }

    public static String getImg(Cursor cursor) {
        if (imgColumn < 0) {
            imgColumn = cursor.getColumnIndexOrThrow(IMG);
        }
        return cursor.getString(imgColumn);
    }

    public static void putImg(ContentValues values, String img) { values.put(IMG, img); }
}
