package com.example.mymusicplayerproject;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.zip.Inflater;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.CustomViewHolder> {
    private Context context;
    private ArrayList<MusicData> musicList;

    //생성자 오버라이딩 하기
    public MusicAdapter(Context context, ArrayList<MusicData> musicList) {
        this.context = context;
        this.musicList = musicList;
    }

    @NonNull
    @Override
    public MusicAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_item, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder customViewHolder, int position) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        Bitmap albumImg = getAlbumImg(context, Integer.parseInt(musicList.get(position).getAlbumArt()), 200);

        if (albumImg != null) {
            customViewHolder.albumArt.setImageBitmap(albumImg);
        }

        customViewHolder.title.setText(musicList.get(position).getTitle());
        customViewHolder.artist.setText(musicList.get(position).getArtists());
       // customViewHolder.duration.setText(Integer.parseInt(musicList.get(position).getDuration()));
        customViewHolder.duration.setText(simpleDateFormat.format(Integer.parseInt(musicList.get(position).getDuration())));
    }


    private Bitmap getAlbumImg(Context context, int albumArt, int imgMaxSize) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.parse("content://media/external/audio/albumart/" + albumArt);

        if (uri != null) {
            ParcelFileDescriptor fd = null;
            try {
                fd = contentResolver.openFileDescriptor(uri, "r");
                options.inJustDecodeBounds = true;
                int scale = 0;

                if (options.outHeight > imgMaxSize || options.outWidth > imgMaxSize) {
                    scale = (int) Math.pow(2, (int) Math.round(Math.log(imgMaxSize /
                            (double) Math.max(options.outHeight, options.outWidth)) / Math.log(0.5)));
                }
                options.inJustDecodeBounds = false;
                options.inSampleSize = scale;

                Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, options);
                if (bitmap != null) {
                    if (options.outWidth != imgMaxSize || options.outHeight != imgMaxSize) {
                        Bitmap tmp = Bitmap.createScaledBitmap(bitmap, imgMaxSize, imgMaxSize, true);
                        bitmap.recycle();
                        bitmap = tmp;
                    }
                }

                return bitmap;

            } catch (FileNotFoundException e) {
                Log.d("MusicAdapter", "컨텐트 리졸버 에러");
            } finally {
                if (fd != null) {
                    try {
                        fd.close();
                    } catch (IOException e) {
                        Log.d("MusicAdapter", "fd.close");
                    }
                }
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return (musicList != null) ? (musicList.size()) : (0);
    }

    //내부클래스 뷰홀더를 만든다.
    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private ImageView albumArt;
        private TextView title, artist, duration;

        public CustomViewHolder(@NonNull View itemView) {

            super(itemView);
            albumArt = itemView.findViewById(R.id.imgAlbum);
            title = itemView.findViewById(R.id.tvTitle);
            artist = itemView.findViewById(R.id.tvSingerName);
            duration = itemView.findViewById(R.id.tvTime);
        }
    }

    public void setMusicList(ArrayList<MusicData> musicList) {
        this.musicList = musicList;
    }
}
