package com.android1.socialnetwork.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class CardData implements Parcelable {

    private String id; // идентификатор записи (выявлять редактируемую/удаляемую запись)
    private String title; // заголовок
    private String description; // описание
    private int picture; // изображение
    private boolean like; // флажок
    private Date date; // дата

    public CardData(String title, String description, int picture, boolean like, Date date){
        this.title = title;
        this.description=description;
        this.picture=picture;
        this.like=like;
        this.date=date;
    }

// Начало обслуживания требований Parcelable

    // Так как мы будем обмениваться данными - необходим объект с реализацией интерфейса Parcelable
    protected CardData(Parcel in) {
        title = in.readString();
        description = in.readString();
        picture = in.readInt();
        like = in.readByte() != 0;
        date = new Date(in.readLong());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeInt(picture);
        dest.writeByte((byte) (like ? 1 : 0));
        dest.writeLong(date.getTime());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<CardData> CREATOR = new Parcelable.Creator<CardData>() {
        @Override
        public CardData createFromParcel(Parcel in) {
            return new CardData(in);
        }
        @Override
        public CardData[] newArray(int size) {
            return new CardData[size];
        }
    };
// Конец обслуживания Parcelable.

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPicture() {
        return picture;
    }

    public boolean isLike() {
        return like;
    }

    public Date getDate() {
        return date;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLike(boolean like) {
        this.like = like;
    }


}
