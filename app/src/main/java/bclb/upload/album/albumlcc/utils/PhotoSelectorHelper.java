package bclb.upload.album.albumlcc.utils;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bclb.upload.album.albumlcc.bean.AlbumModel;
import bclb.upload.album.albumlcc.bean.DisplayInfoBean;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class PhotoSelectorHelper {

    private AlbumHelpler albumController;

    public PhotoSelectorHelper(Context context) {
        albumController = new AlbumHelpler(context);
    }

    /**
     * 获得图片和视频
     *
     * @param listener
     */
    public void getReccentPhotoList(final OnLoadPhotoListener listener) {
        Observable.create(new Observable.OnSubscribe<List<String>>() {
            @Override
            public void call(Subscriber<? super List<String>> subscriber) {
                List<String> picVideos = new ArrayList<String>();
                List<DisplayInfoBean> photos = albumController.getPicVideos();
                Collections.sort(photos, new Comparator<DisplayInfoBean>() {
                    @Override
                    public int compare(DisplayInfoBean o1, DisplayInfoBean o2) {
                        return (int) (Long.parseLong(o2.getAddTime()) - Long.parseLong(o1.getAddTime()));
                    }
                });
                for (int i = 0; i < photos.size(); i++) {
                    picVideos.add(photos.get(i).getPath());
                }
                subscriber.onNext(picVideos);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new Subscriber<List<String>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onPhotoLoaded(new ArrayList<String>());
                    }

                    @Override
                    public void onNext(List<String> list) {
                        listener.onPhotoLoaded(list);
                    }
                });
    }

    /**
     * 获得所有的照片
     */
    public void getAllPics(final OnLoadPhotoListener listener) {
        Observable.create(new Observable.OnSubscribe<List<String>>() {
            @Override
            public void call(Subscriber<? super List<String>> subscriber) {
                List<String> list = albumController.getPics();
                subscriber.onNext(list);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new Subscriber<List<String>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<String> list) {
                        listener.onPhotoLoaded(list);
                    }
                });
    }

    /**
     * 获取视频相册列表
     */
    public void getVideoAlbumList(final OnLoadAlbumListener listener) {

        Observable.create(new Observable.OnSubscribe<List<AlbumModel>>() {
            @Override
            public void call(Subscriber<? super List<AlbumModel>> subscriber) {
                List<AlbumModel> albums = albumController.getVideoAlbums();
                subscriber.onNext(albums);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new Subscriber<List<AlbumModel>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<AlbumModel> albumModels) {
                        listener.onAlbumLoaded(albumModels);
                    }
                });

    }

    /**
     * 获取相册列表
     */
    public void getAlbumList(final OnLoadAlbumListener listener) {

        Observable.create(new Observable.OnSubscribe<List<AlbumModel>>() {
            @Override
            public void call(Subscriber<? super List<AlbumModel>> subscriber) {
                List<AlbumModel> albums = albumController.getAlbums();
                subscriber.onNext(albums);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(

                new Subscriber<List<AlbumModel>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(List<AlbumModel> albumModels) {
                        listener.onAlbumLoaded(albumModels);

                    }
                }
        );

    }

    /**
     * 获取单个相册下的所有视频的信息
     */
    public void getAlbumVideoList(final OnLoadPhotoListener listener) {
        Observable.create(new Observable.OnSubscribe<List<DisplayInfoBean>>() {
            @Override
            public void call(Subscriber<? super List<DisplayInfoBean>> subscriber) {
                List<DisplayInfoBean> videos = albumController.getVideos();
                subscriber.onNext(videos);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new Subscriber<List<DisplayInfoBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<DisplayInfoBean> displayInfoBeen) {
                        listener.onVideoLoaded(displayInfoBeen);
                    }
                });
    }

    /**
     * 获取单个相册下的所有照片信息
     */
    public void getAlbumPhotoList(final String name, final OnLoadPhotoListener listener) {
        Observable.create(new Observable.OnSubscribe<List<String>>() {
            @Override
            public void call(Subscriber<? super List<String>> subscriber) {
                List<String> photos = albumController.getAlbumPic(name);
                subscriber.onNext(photos);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new Subscriber<List<String>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("======",e.toString());
                    }

                    @Override
                    public void onNext(List<String> albumModels) {
                        listener.onPhotoLoaded(albumModels);
                    }
                });
    }


    /**
     * 获取本地图库照片回调
     */
    public interface OnLoadPhotoListener {
        void onPhotoLoaded(List<String> photos);

        void onVideoLoaded(List<DisplayInfoBean> videos);
    }

    /**
     * 获取本地相册信息回调
     */
    public interface OnLoadAlbumListener {
        void onAlbumLoaded(List<AlbumModel> albums);
    }

}

