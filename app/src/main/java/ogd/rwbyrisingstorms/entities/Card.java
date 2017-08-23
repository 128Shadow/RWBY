package ogd.rwbyrisingstorms.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import ogd.rwbyrisingstorms.contracts.DeckContract;

/**
 * Created by luismeneses on 7/12/17.
 */

public class Card implements Parcelable {

    public long id;

    public String title;
    public String img;

    public String getTitle() {
        return title;
    }
    public String getImg() { return img; }

    public Card(String title, String img) {
        //this.id = id;
        this.title = title;
        this.img = img;
    }

    public Card(Parcel in) {
        //id = in.readLong();
        title = in.readString();
        img = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        //out.writeLong(id);
        out.writeString(title);
        out.writeString(img);
    }

    public static final Parcelable.Creator<Card> CREATOR = new Parcelable.Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    public Card(Cursor cursor) {
        this.title = DeckContract.getTitle(cursor);
        this.img = DeckContract.getImg(cursor);
    }

    public void writeToProvider(ContentValues out) {
        DeckContract.putTitle(out, this.title);
        DeckContract.putImg(out, this.img);
    }


}