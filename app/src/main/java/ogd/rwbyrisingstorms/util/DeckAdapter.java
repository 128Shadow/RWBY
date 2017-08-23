package ogd.rwbyrisingstorms.util;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import ogd.rwbyrisingstorms.R;


public class DeckAdapter extends ResourceCursorAdapter {

    protected final static int ROW_LAYOUT = android.R.layout.simple_list_item_2;

    public DeckAdapter(Context context, Cursor cursor) {
        super(context, ROW_LAYOUT, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(ROW_LAYOUT, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView title = (TextView) view.findViewById(R.id.deck_card_title);
    }
}