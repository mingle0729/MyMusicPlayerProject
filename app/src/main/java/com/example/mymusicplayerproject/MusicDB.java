package com.example.mymusicplayerproject;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MusicDB extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE = "musicDB";
    private Context context;
    private static MusicDB musicDB;

    public MusicDB(@Nullable Context context) {
        super(context, DATABASE, null, VERSION);
        this.context = context;
    }

    public static MusicDB getInstance(Context context) {
        if (musicDB == null) {
            musicDB = new MusicDB(context);
        }
        return musicDB;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table if not exists mp3TBL(" +
                "id varchar(10) not null primary key," +
                "artists varchar(10)," +
                "title varchar(30)," +
                "albumArt varchar(30)," +
                "duration varchar(30));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists mp3TBL");
        onCreate(sqLiteDatabase);
    }

    private ArrayList<MusicData> selectMusicTBL() {

        ArrayList<MusicData> musicDBlist = new ArrayList<MusicData>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("select*from mp3TBL;", null);
        while (cursor.moveToNext()) {
            MusicData musicData = new MusicData(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4));

            musicDBlist.add(musicData);
        }

        cursor.close();
        sqLiteDatabase.close();

        return musicDBlist;
    }

    public boolean insertQuery(ArrayList<MusicData> musicDataArrayList) {
        boolean retureValue = false;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        try {
            for (MusicData musicData : musicDataArrayList) {
                ArrayList<MusicData> dataArrayList = selectMusicTBL();

                if (!dataArrayList.contains(musicData)) {
                    String query = "insert into mp3TBL values(" +
                            "'" + musicData.getId() + "'," +
                            "'" + musicData.getArtists() + "'," +
                            "'" + musicData.getTitle() + "'," +
                            "'" + musicData.getAlbumArt() + "'," +
                            "'" + musicData.getDuration() + "')";

                    sqLiteDatabase.execSQL(query);
                }
            }
            retureValue = true;

        } catch (Exception e) {
            Log.d("MainActivity", "정보입력");
        } finally {
            sqLiteDatabase.close();
        }
        return retureValue;
    }

    public boolean deleteQuery(ArrayList<MusicData> musicDataArrayList) {
        boolean retureValue = false;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        try {
            for (MusicData musicData : musicDataArrayList) {

                String query = "delete from mp3TBL where title ='" + musicData.getTitle() + "';";
                sqLiteDatabase.execSQL(query);
            }

            retureValue = true;

        } catch (Exception e) {
            Log.d("MainActivity", "정보입력");
        } finally {
            sqLiteDatabase.close();
        }

        return retureValue;
    }

    public boolean updateQuery(ArrayList<MusicData> musicDataArrayList) {
        boolean retureValue = false;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        try {
            for (MusicData musicData : musicDataArrayList) {

                String query = "update mp3TBL set  where title ='" + musicData.getTitle() + "';";
                sqLiteDatabase.execSQL(query);
            }
            retureValue = true;

        } catch (Exception e) {
            Log.d("MainActivity", "정보입력");
        } finally {
            sqLiteDatabase.close();
        }

        return retureValue;
    }

    public ArrayList<MusicData> findContentProvMp3List() {
        ArrayList<MusicData> sdCardList = new ArrayList<>();

        String[] musicData = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION};


        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                musicData, null, null, musicData[2] + " ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(musicData[0]));
                String artists = cursor.getString(cursor.getColumnIndex(musicData[1]));
                String title = cursor.getString(cursor.getColumnIndex(musicData[2]));
                String albumArt = cursor.getString(cursor.getColumnIndex(musicData[3]));
                String duration = cursor.getString(cursor.getColumnIndex(musicData[4]));

                MusicData mData = new MusicData(id, artists, title, albumArt, duration);
                sdCardList.add(mData);
            }
        }
        return sdCardList;
    }

    public ArrayList<MusicData> compareArrayList() {
        ArrayList<MusicData> sdCardList = findContentProvMp3List();
        ArrayList<MusicData> dbList = selectMusicTBL();
        int size = 0;

        if (dbList.isEmpty()) {
            return sdCardList;
        }

        for (int i = 0; i < size; i++) {
            if (dbList.contains(sdCardList.get(i))) {
                continue;
            }
            dbList.add(sdCardList.get(i));
            size++;
        }
        return dbList;
    }

    public ArrayList<MusicData> saveLikeList() {
        ArrayList<MusicData> likeMusicList = new ArrayList<MusicData>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("select*from mp3TBL where liked = 1;", null);
        while (cursor.moveToNext()) {
            MusicData musicData = new MusicData(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4));

            likeMusicList.add(musicData);
        }
        cursor.close();
        sqLiteDatabase.close();

        return likeMusicList;
    }

}
