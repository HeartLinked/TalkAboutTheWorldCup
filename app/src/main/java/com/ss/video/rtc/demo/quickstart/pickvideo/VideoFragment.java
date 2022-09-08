package com.ss.video.rtc.demo.quickstart.pickvideo;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ss.rtc.demo.quickstart.R;
import com.ss.video.rtc.demo.quickstart.RTCRoomActivity;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class VideoFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private List<VideoInfo> mVideoInfoList = new ArrayList<>();
    private SelectorVideoAdapter mAdapter;
    private Button mBtn_ok;
    private VideoInfo mVideoInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_video, container , false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_selector_video);
        mBtn_ok = (Button) view.findViewById(R.id.btn_ok);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
        getVideoInfoList();
    }

    private void initView() {
        GridLayoutManager linearLayoutManager =  new GridLayoutManager(getActivity() , 3);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new SelectorVideoAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setItemOnClickListener(new BaseAdapterRV.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, Object o) {
                VideoInfo info = (VideoInfo) o;

                mAdapter.setSelectorPosition(position);

                if (mAdapter.getSelectorPosition() != -1){
                    mVideoInfo = info ;
                }else {
                    mVideoInfo = null ;
                }

            }
        });

        mBtn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("EEE", String.valueOf(mVideoInfo.uri)+"dd");
                if (mVideoInfo == null){
                    Toast.makeText(getActivity(), "当前未选中视频！", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    Log.e("EEE", String.valueOf(mVideoInfo.uri));
//                    System.out.println(mVideoInfo.uri.toString());
                    Intent intent = new Intent(getActivity(), RTCRoomActivity.class);
                    intent.putExtra("Uri", mVideoInfo.getPath());
//                    Log.e("EEE", String.valueOf(mVideoInfo.uri));
                    Activity activity = (Activity) v.getContext();
                    activity.setResult(1, intent);
                    activity.finish();
//                    ((Activity)(v.getContext())).startActivityForResult(intent,1);
                }

            }
        });
// content://media/external/video/media/103498
    }

    private void getVideoInfoList() {
        Cursor cursor = getActivity().getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null,
                null, null);

        if (cursor == null) return;
        Observable.just(cursor)
                .map(new Func1<Cursor, List<VideoInfo>>() {
                    @Override
                    public List<VideoInfo> call(Cursor cursor) {
                        return cursorToList(cursor);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<VideoInfo>>() {
                    @Override
                    public void call(List<VideoInfo> videoInfos) {
                        mAdapter.addData(videoInfos);
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    private List<VideoInfo> cursorToList(Cursor cursor) {

        mVideoInfoList.clear();
        VideoInfo videoInfo;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media._ID));
            String title = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
            String album = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM));

            Uri uri = cursor.getNotificationUri();


            String displayName = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
            String mimeType = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
            String path = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
            long duration = cursor
                    .getInt(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
            long size = cursor
                    .getLong(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));

            videoInfo = new VideoInfo(id, title,album, uri, displayName,
                    mimeType, path, size, duration );
            mVideoInfoList.add(videoInfo);
        }
        cursor.close();

        return mVideoInfoList;
    }
}