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
    private static final String DATABASE = "musicDB";
    private static final int VERSION = 1;
    private Context context;

    private static MusicDB musicDB;

    private MusicDB(@Nullable Context context) {
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
        sqLiteDatabase.execSQL(
                "create table if not exists mp3TBL(" +
                        "id varchar(20) primary key," +
                        "artists varchar(20)," +
                        "title varchar(50)," +
                        "albumArt varchar(30)," +
                        "duration varchar(20)," +
                        "count integer," +
                        "liked integer);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists mp3TBL");
        onCreate(sqLiteDatabase);
    }

    public ArrayList<MusicData> selectMusicTBL() {

        ArrayList<MusicData> musicDBlist = new ArrayList<MusicData>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("select*from mp3TBL;", null);
        while (cursor.moveToNext()) {
            MusicData musicData = new MusicData(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(5),
                    cursor.getInt(6));

            musicDBlist.add(musicData);
        }

        cursor.close();

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
                            "'" + musicData.getDuration() + "',"
                            + 0 + "," + 0 + ");";

                    sqLiteDatabase.execSQL(query);
                }
            }
            retureValue = true;

        } catch (Exception e) {
            Log.d("MainActivity", "정보입력");
        }
        return retureValue;
    }


    public boolean updateQuery(ArrayList<MusicData> musicDataArrayList) {
        boolean retureValue = false;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        try {
            for (MusicData musicData : musicDataArrayList) {

                String query = "update mp3TBL set count = " + musicData.getCount() + ", liked = " + musicData.getLiked() + "  where id ='" + musicData.getId() + "';";
                sqLiteDatabase.execSQL(query);
            }
            retureValue = true;

        } catch (Exception e) {
            Log.d("MainActivity", "정보입력");
            return false;

        }
        return retureValue;
    }

    public ArrayList<MusicData> findContentProvMp3List() {
        ArrayList<MusicData> musicDataArrayList = new ArrayList<>();

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

                MusicData mData = new MusicData(id, artists, title, albumArt, duration, 0, 0);
                musicDataArrayList.add(mData);
            }
        }
        return musicDataArrayList;
    }

    public ArrayList<MusicData> compareArrayList() {
        ArrayList<MusicData> sdCardList = findContentProvMp3List();
        ArrayList<MusicData> dbList = selectMusicTBL();

        if (dbList.isEmpty()) {
            return sdCardList;
        }
        if (dbList.containsAll(sdCardList)){
            return dbList;

        }

        int size = dbList.size();
        for (int i = 0; i < size; i++) {
            if (dbList.contains(sdCardList.get(i))) {
                continue;
            }
            dbList.add(sdCardList.get(i));
            ++size;
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
                    cursor.getString(4),
                    cursor.getInt(5),
                    cursor.getInt(6));

            likeMusicList.add(musicData);
        }
        cursor.close();

        return likeMusicList;
    }

}
