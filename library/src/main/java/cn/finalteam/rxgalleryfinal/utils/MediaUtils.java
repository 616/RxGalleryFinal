package cn.finalteam.rxgalleryfinal.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.finalteam.rxgalleryfinal.bean.BucketBean;
import cn.finalteam.rxgalleryfinal.bean.MediaBean;

/**
 * Desction:媒体获取工具
 * Author:pengjianbo
 * Date:16/5/4 下午4:11
 */
public class MediaUtils {

    public static List<MediaBean> getMediaWithImageList(Context context, int page, int limit) {
        return getMediaWithImageList(context, String.valueOf(Integer.MIN_VALUE), page, limit);
    }
    /**
     * 从数据库中读取图片
     * @param context
     * @param bucketId
     * @param page
     * @param limit
     * @return
     */
    public static List<MediaBean> getMediaWithImageList(Context context, String bucketId, int page, int limit) {
        int offset = (page -1) * limit;
        List<MediaBean> mediaBeanList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = new String[] {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.DATE_ADDED,//创建时间
                MediaStore.Images.Media.DATE_MODIFIED//最后修改时间
        };
        String selection = null;
        String []selectionArgs = null;
        if(!TextUtils.equals(bucketId, String.valueOf(Integer.MIN_VALUE))) {
            selection = MediaStore.Images.Media.BUCKET_ID + "=?";
            selectionArgs = new String[]{bucketId};
        }
        Cursor cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection,
                selectionArgs, MediaStore.Images.Media.DATE_ADDED +" DESC LIMIT " + limit +" OFFSET " + offset);
        if(cursor != null) {
            int count = cursor.getCount();
            if(count > 0) {
                cursor.moveToFirst();
                do {
                    MediaBean mediaBean = parseImageCursorAndCreateThumImage(context, cursor);
                    mediaBeanList.add(mediaBean);
                } while (cursor.moveToNext());
            }
        }

        if(cursor != null && !cursor.isClosed()){
            cursor.close();
        }
        cursor = null;
        return mediaBeanList;
    }

    public static List<MediaBean> getMediaWithVideoList(Context context, int page, int limit) {
        return getMediaWithVideoList(context, page, limit);
    }

    /**
     * 从数据库中读取视频
     * @param context
     * @param bucketId
     * @param page
     * @param limit
     * @return
     */
    public static List<MediaBean> getMediaWithVideoList(Context context, String bucketId, int page, int limit) {
        int offset = (page -1) * limit;
        List<MediaBean> mediaBeanList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = new String[] {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.BUCKET_ID,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.DATE_ADDED,//创建时间
                MediaStore.Video.Media.DATE_MODIFIED//最后修改时间
        };
        String selection = null;
        String []selectionArgs = null;
        if(!TextUtils.equals(bucketId, String.valueOf(Integer.MIN_VALUE))) {
            selection = MediaStore.Video.Media.BUCKET_ID + "=?";
            selectionArgs = new String[]{bucketId};
        }

        Cursor cursor = contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, selection,
                selectionArgs, MediaStore.Video.Media.DATE_ADDED +" DESC LIMIT " + limit +" OFFSET " + offset);
        if(cursor != null) {
            int count = cursor.getCount();
            if(count > 0) {
                cursor.moveToFirst();
                do {
                    MediaBean mediaBean = parseVideoCursorAndCreateThumImage(context, cursor);
                    mediaBeanList.add(mediaBean);
                } while (cursor.moveToNext());
            }
        }

        if(cursor != null && !cursor.isClosed()){
            cursor.close();
        }
        cursor = null;
        return mediaBeanList;
    }

    /**
     * 根据原图获取图片相关信息
     * @param context
     * @param originalPath
     * @return
     */
    public static MediaBean getMediaBeanWithImage(Context context, String originalPath) {
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = new String[] {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.DATE_ADDED,//创建时间
                MediaStore.Images.Media.DATE_MODIFIED//最后修改时间
        };
        Cursor cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, MediaStore.Images.Media.DATA +"=?",
                new String[]{originalPath}, null);
        MediaBean mediaBean = null;
        if(cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            mediaBean =  parseImageCursorAndCreateThumImage(context, cursor);
        }
        if(cursor != null && !cursor.isClosed()){
            cursor.close();
        }
        cursor = null;
        return mediaBean;
    }


    /**
     * 解析图片cursor并且创建缩略图
     * @param context
     * @param cursor
     * @return
     */
    private static MediaBean parseImageCursorAndCreateThumImage(Context context, Cursor cursor) {
        MediaBean mediaBean = new MediaBean();
        long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
        mediaBean.setId(id);
        String title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE));
        mediaBean.setTitle(title);
        String originalPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        mediaBean.setOriginalPath(originalPath);
        String bucketId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
        mediaBean.setBucketId(bucketId);
        String bucketDisplayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
        mediaBean.setBucketDisplayName(bucketDisplayName);
        String mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
        mediaBean.setMimeType(mimeType);
        long createDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
        mediaBean.setCreateDate(createDate);
        long modifiedDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
        mediaBean.setModifiedDate(modifiedDate);
        File storeFile = StorageUtils.getCacheDirectory(context);
        File bigThumFile = new File(storeFile, "big_" + FilenameUtils.getName(originalPath));
        File smallThumFile = new File(storeFile, "small_" + FilenameUtils.getName(originalPath));
        if(!smallThumFile.exists()){
            String thum = BitmapUtils.getThumbnailSmallPath(storeFile.getAbsolutePath(), originalPath);
            mediaBean.setThumbnailSmallPath(thum);
        }else{
            mediaBean.setThumbnailSmallPath(smallThumFile.getAbsolutePath());
        }
        if(!bigThumFile.exists()){
            String thum = BitmapUtils.getThumbnailBigPath(storeFile.getAbsolutePath(), originalPath);
            mediaBean.setThumbnailBigPath(thum);
        } else {
            mediaBean.setThumbnailBigPath(bigThumFile.getAbsolutePath());
        }

        mediaBean.setThumbnailSmallPath(bigThumFile.getAbsolutePath());
        return mediaBean;
    }

    /**
     * 解析视频cursor并且创建缩略图
     * @param context
     * @param cursor
     * @return
     */
    private static MediaBean parseVideoCursorAndCreateThumImage(Context context, Cursor cursor) {
        MediaBean mediaBean = new MediaBean();
        long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));
        mediaBean.setId(id);
        String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
        mediaBean.setTitle(title);
        String originalPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        mediaBean.setOriginalPath(originalPath);
        String bucketId = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID));
        mediaBean.setBucketId(bucketId);
        String bucketDisplayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME));
        mediaBean.setBucketDisplayName(bucketDisplayName);
        String mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE));
        mediaBean.setMimeType(mimeType);
        long createDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
        mediaBean.setCreateDate(createDate);
        long modifiedDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED));
        mediaBean.setModifiedDate(modifiedDate);

        //获取缩略图
        File storeFile = StorageUtils.getCacheDirectory(context);
        File bigThumFile = new File(storeFile, "big_" + FilenameUtils.getName(originalPath));
        File smallThumFile = new File(storeFile, "small_" + FilenameUtils.getName(originalPath));
        if(!smallThumFile.exists()){
            String thum = BitmapUtils.getVideoThumbnailSmallPath(storeFile.getAbsolutePath(), originalPath);
            mediaBean.setThumbnailSmallPath(thum);
        }else{
            mediaBean.setThumbnailSmallPath(smallThumFile.getAbsolutePath());
        }
        if(!bigThumFile.exists()){
            String thum = BitmapUtils.getVideoThumbnailBigPath(storeFile.getAbsolutePath(), originalPath);
            mediaBean.setThumbnailBigPath(thum);
        } else {
            mediaBean.setThumbnailBigPath(bigThumFile.getAbsolutePath());
        }

        return mediaBean;
    }

    /**
     * 获取所有的图片文件夹
     * @param context
     * @return
     */
    public static List<BucketBean> getAllBucketByImage(Context context) {
        return getAllBucket(context, true);
    }

    /**
     * 获取所以视频文件夹
     * @param context
     * @return
     */
    public static List<BucketBean> getAllBucketByVideo(Context context) {
        return getAllBucket(context, false);
    }

    /**
     * 获取所有的问media文件夹
     * @param context
     * @param isImage
     * @return
     */
    public static List<BucketBean> getAllBucket(Context context, boolean isImage) {
        List<BucketBean> bucketBeenList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection;
        if(isImage){
            projection = new String[] {
                    MediaStore.Images.Media.BUCKET_ID,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            };
        } else {
            projection = new String[] {
                    MediaStore.Video.Media.BUCKET_ID,
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            };
        }
        BucketBean allMediaBucket = new BucketBean();
        allMediaBucket.setBucketId(String.valueOf(Integer.MIN_VALUE));
        Uri uri;
        if(isImage) {
            allMediaBucket.setBucketName("所有图片");
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else {
            allMediaBucket.setBucketName("所有视频");
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }
        bucketBeenList.add(allMediaBucket);
        Cursor cursor = null;
        try {
             cursor = contentResolver.query(uri, projection, null, null, MediaStore.Video.Media.DATE_ADDED + " DESC");
        } catch (Exception e){
            Logger.e(e);
        }

        if(cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                BucketBean bucketBean = new BucketBean();
                String bucketId;
                String bucketKey;
                String cover;
                if(isImage) {
                    bucketId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
                    bucketBean.setBucketId(bucketId);
                    String bucketDisplayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                    bucketBean.setBucketName(bucketDisplayName);
                    bucketKey = MediaStore.Images.Media.BUCKET_ID;
                    cover = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                } else {
                    bucketId = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID));
                    bucketBean.setBucketId(bucketId);
                    String bucketDisplayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME));
                    bucketBean.setBucketName(bucketDisplayName);
                    bucketKey = MediaStore.Video.Media.BUCKET_ID;
                    cover = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                }
                if(TextUtils.isEmpty(allMediaBucket.getCover())) {
                    allMediaBucket.setCover(cover);
                }
                if(bucketBeenList.contains(bucketBean)) {
                    bucketBean = null;
                    bucketId = null;
                    bucketKey = null;
                   continue;
                }
                //获取数量
                Cursor c = contentResolver.query(uri, projection, bucketKey +"=?", new String[]{bucketId}, null);
                if(c != null && c.getCount() > 0) {
                    bucketBean.setImageCount(c.getCount());
                }
                bucketBean.setCover(cover);
                if(c != null && !c.isClosed()) {
                    c.close();
                }
                c = null;
                bucketBeenList.add(bucketBean);
            } while (cursor.moveToNext());
        }

        if(cursor != null && !cursor.isClosed()){
            cursor.close();
        }
        cursor = null;
        return bucketBeenList;
    }
}
