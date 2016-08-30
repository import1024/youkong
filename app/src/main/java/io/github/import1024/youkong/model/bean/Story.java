package io.github.import1024.youkong.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by redback on 8/24/16.
 */
public class Story implements Parcelable {
    public Story(int id, String title, String image, String imageSource, String content) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.imageSource = imageSource;
        this.content = content;
    }

    public Story() {

    }

    public int id;
    public String title;
    public String image;
    public String imageSource;
    public String content;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.image);
        dest.writeString(this.imageSource);
        dest.writeString(this.content);
    }

    protected Story(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.image = in.readString();
        this.imageSource = in.readString();
        this.content = in.readString();
    }

    public static final Parcelable.Creator<Story> CREATOR = new Parcelable.Creator<Story>() {
        public Story createFromParcel(Parcel source) {
            return new Story(source);
        }

        public Story[] newArray(int size) {
            return new Story[size];
        }
    };
}
