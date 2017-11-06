package org.alertpreparedness.platform.alert.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by: Jordan Fisher
 * Dated: 02/08/16
 * Email: jordan@rolleragency.co.uk
 * <<--------------------->>
 * Copyright Roller Agency
 */
public class ImageDownload {
    private static Context context;

    private Activity activity;
    private Fragment fragment;
    private ImageLoader loader;

    private int CODE_GALLERY = 1010;
    private int CODE_CAMERA = 1011;
    private int CODE_CAMERA_PERMISSION = 1012;

    private String externalFilesDir;
    private String tempImageFile = "FILE" + IMAGE_EXTENSION;
    private String tempVideoFile = "FILE" + VIDEO_EXTENSION;
    private String fileName;
    private String directory;
    private int type;

    private int maximumDuration = 0;
    private int maximumSize = 0;
    private double videoQuality = 1.0;

    /**
     * Directory needs to be "file/to/" (with slashes after each file!)
     **/
    public ImageDownload(Activity activity, ImageLoader loader, String directory, String fileName) {
        this.activity = activity;
        this.fragment = null;
        this.loader = loader;
        this.fileName = fileName;
        this.directory = directory;
        externalFilesDir = activity.getExternalFilesDir(null).toString();
        context = activity.getApplicationContext();
    }

    public ImageDownload(Fragment fragment, ImageLoader loader, String directory, String fileName) {
        this.fragment = fragment;
        this.activity = null;
        this.loader = loader;
        this.fileName = fileName;
        this.directory = directory;
        externalFilesDir = fragment.getContext().getExternalFilesDir(null).toString();
        context = fragment.getContext();
    }

    // Get from the camera method
    public void getFromCamera(int type) {
        this.type = type;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity != null) {
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, CODE_CAMERA_PERMISSION);
                } else {
                    getFromCameraWithPermission(type);
                }
            } else if (fragment != null) {
                if (ContextCompat.checkSelfPermission(fragment.getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    fragment.requestPermissions(new String[]{Manifest.permission.CAMERA}, CODE_CAMERA_PERMISSION);
                } else {
                    getFromCameraWithPermission(type);
                }
            } else {
                getFromCameraWithPermission(type);
            }
        } else {
            getFromCameraWithPermission(type);
        }
    }

    private void getFromCameraWithPermission(int type) {
        Intent cameraIntent;
        if (type == Type.CAMERA_PHOTOS) {
            cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(externalFilesDir + "/" + tempImageFile)));
        } else if (type == Type.CAMERA_VIDEO) {
            cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(externalFilesDir + "/" + tempVideoFile)));
            if (maximumDuration != 0)
                cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, maximumDuration);
            if (maximumSize != 0)
                cameraIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, maximumSize);
            if (videoQuality >= 0.0 || videoQuality <= 1.0)
                cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, videoQuality);
        } else {
            loader.failed(Error.WRONG_INTENT_TYPE);
            return;
        }
        if (activity != null) {
            if (cameraIntent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivityForResult(cameraIntent, CODE_CAMERA);
            } else {
                loader.failed(Error.ACTIVITY_NO_PACKAGE_MANAGER);
            }
        } else if (fragment != null) {
            fragment.startActivityForResult(cameraIntent, CODE_CAMERA);
        } else {
            loader.failed(Error.ACTIVITY_OR_FRAGMENT_NOT_FOUND);
        }
    }

    public void getFromGallery(int type) {
        this.type = type;
        Intent galleryIntent;
        if (type == Type.GALLERY_PHOTOS) {
            galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        } else if (type == Type.GALLERY_VIDEO) {
            if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            } else {
                galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.INTERNAL_CONTENT_URI);
            }
        } else if (type == Type.GALLERY_PHOTOS_AND_VIDEO) {
            galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            //comma-separated MIME types
            galleryIntent.setType("video/*, images/*");
        } else {
            loader.failed(Error.WRONG_INTENT_TYPE);
            return;
        }
        if (activity != null) {
            activity.startActivityForResult(galleryIntent, CODE_GALLERY);
        } else if (fragment != null) {
            fragment.startActivityForResult(galleryIntent, CODE_GALLERY);
        } else {
            loader.failed(Error.ACTIVITY_OR_FRAGMENT_NOT_FOUND);
        }
    }


    /**
     * INTENT MODIFIERS
     **/
    /* DURATION **/
    public void setMaximumVideoDuration(int duration) {
        this.maximumDuration = duration;
        if (this.maximumDuration < 0)
            maximumDuration = 0;
    }

    public void clearMaxDuration() {
        this.maximumDuration = 0;
    }

    public int getMaximumVideoDuration() {
        return this.maximumDuration;
    }

    /* SIZE **/
    public void setMaximumVideoSizeMB(int size) {
        this.maximumSize = size * 1024 * 1046;
        if (this.maximumSize < 0)
            maximumSize = 0;
    }

    public void setMaximumVideoSizeKB(int size) {
        this.maximumSize = size * 1024;
        if (this.maximumSize < 0)
            maximumSize = 0;
    }

    public void setMaximumVideoSizeB(int size) {
        this.maximumSize = size;
        if (this.maximumSize < 0)
            maximumSize = 0;
    }

    public void clearMaximumVideoSize() {
        this.maximumSize = 0;
    }

    public int getMaximumVideoSize() {
        return this.maximumSize;
    }

    /* QUAILTY **/
    public void setVideoQuailty(double quality) {
        if (quality > 1.0)
            quality = 1.0d;
        if (quality < 0.0)
            quality = 0.0d;
        this.videoQuality = quality;
    }

    public double getVideoQuality() {
        return this.videoQuality;
    }


    /**
     * CALLBACKS
     **/
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantedResults) {
        if (requestCode == CODE_CAMERA && grantedResults[0] == PackageManager.PERMISSION_GRANTED) {
            getFromCameraWithPermission(type);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        o("TYPE         : [" + type + "]");
        o("REQUEST_CODE : [" + requestCode + "] C(" + CODE_CAMERA + ") CP(" + CODE_CAMERA_PERMISSION + ") G(" + CODE_GALLERY + ")");
        if (resultCode == Activity.RESULT_OK) {
            if (type == Type.CAMERA_PHOTOS && requestCode == CODE_CAMERA) {
                o("onActivityResult for CAMERA only photos");
                loader.showProgress(true);

                // Movement
                String currentLocation = externalFilesDir + "/" + tempImageFile;
                String saveLocation = externalFilesDir + "/" + directory + fileName + IMAGE_EXTENSION;
                o(" --> Moving file from [" + currentLocation + "]");
                o(" -->               to [" + saveLocation + "]");

                // Launch thread
                new ImageProcessor(currentLocation, saveLocation, null).start();
            } else if (type == Type.CAMERA_VIDEO && requestCode == CODE_CAMERA) {
                o("onActivityResult for CAMERA only videos");
                loader.showProgress(true);

                // Movement
                String currentLocation = externalFilesDir + "/" + tempVideoFile;
                String saveLocation = externalFilesDir + "/" + directory + fileName + VIDEO_EXTENSION;
                o(" --> Moving file from [" + currentLocation + "]");
                o(" -->               to [" + saveLocation + "]");

                // Launch thread
                new VideoProcessor(currentLocation, saveLocation, null).start();
            } else if (type == Type.GALLERY_PHOTOS && requestCode == CODE_GALLERY) {
                o("onActivityResult for GALLERY only photos");
                loader.showProgress(true);

                // Movement
                String currentLocation = externalFilesDir + "/" + tempImageFile;
                String saveLocation = externalFilesDir + "/" + directory + fileName + IMAGE_EXTENSION;
                o(" --> Moving file from [" + currentLocation + "]");
                o(" -->               to [" + saveLocation + "]");

                // Launch thread
                new ImageProcessor(currentLocation, saveLocation, data).start();
            } else if (type == Type.GALLERY_VIDEO && requestCode == CODE_GALLERY) {
                o("onActivityResult for GALLERY only photos");
                loader.showProgress(true);

                // Movement
                String currentLocation = externalFilesDir + "/" + tempVideoFile;
                String saveLocation = externalFilesDir + "/" + directory + fileName + VIDEO_EXTENSION;
                o(" --> Moving file from [" + currentLocation + "]");
                o(" -->               to [" + saveLocation + "]");

                // Launch thread
                new VideoProcessor(currentLocation, saveLocation, data).start();
            } else if (type == Type.GALLERY_PHOTOS_AND_VIDEO && requestCode == CODE_GALLERY) {
                o("onActivityResult for GALLERY photos and videos");
                loader.showProgress(true);
                if (data != null && data.getData() != null) {
                    String content = data.getData().toString();
                    if (content.contains("images")) {
                        // CAMERA UPLOAD

                        // Movement
                        String currentLocation = externalFilesDir + "/" + tempImageFile;
                        String saveLocation = externalFilesDir + "/" + directory + fileName + IMAGE_EXTENSION;
                        o(" --> Moving file from [" + currentLocation + "]");
                        o(" -->               to [" + saveLocation + "]");

                        // Launch thread
                        new ImageProcessor(currentLocation, saveLocation, data).start();
                    } else if (content.contains("video")) {
                        // VIDEO UPLOAD

                        // Movement
                        String currentLocation = externalFilesDir + "/" + tempVideoFile;
                        String saveLocation = externalFilesDir + "/" + directory + fileName + VIDEO_EXTENSION;
                        o(" --> Moving file from [" + currentLocation + "]");
                        o(" -->               to [" + saveLocation + "]");

                        // Launch thread
                        new VideoProcessor(currentLocation, saveLocation, data).start();
                    } else {
                        loader.failed(Error.ACTIVITY_UNKNOWN_MEDIA);
                    }
                } else {
                    loader.failed(Error.ACTIVITY_RESULT_FAIL);
                }
            } else {
                loader.failed(Error.ACTIVITY_RESULT_FAIL);
            }
        } else {
            loader.failed(Error.ACTIVITY_RESULT_FAIL);
        }
    }


    private class ImageProcessor extends Thread implements Runnable {
        private String location;
        private String saveLocation;
        private Intent intent;

        private ImageProcessor(String location, String saveLocation, Intent intent) {
            this.location = location;
            this.saveLocation = saveLocation;
            this.intent = intent;
        }

        @Override
        public void run() {
            super.run();
            Uri uri = Uri.fromFile(new File(location));

            if (intent != null) {
                try {
                    if (activity != null) {
                        inputStreamToFile(activity.getContentResolver().openInputStream(intent.getData()), uri);
                    } else if (fragment != null) {
                        inputStreamToFile(fragment.getActivity().getContentResolver().openInputStream(intent.getData()), uri);
                    } else {
                        loader.failed(Error.ACTIVITY_OR_FRAGMENT_NOT_FOUND);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    loader.failed(Error.FILE_NOT_FOUND);
                }
            }
            Bitmap bitmap = decodeSampledBitmapFromResource(uri, 1000, 1000);
            Bitmap rotated = rotateBitmap(bitmap, imageRotation(uri));
            saveBitmapToFile(rotated, Uri.fromFile(new File(saveLocation)));

            bitmap.recycle();
            rotated.recycle();
            loader.showProgress(false);
            loader.imageFinished(saveLocation);
        }
    }

    private class VideoProcessor extends Thread implements Runnable {
        private String location;
        private String saveLocation;
        private Intent intent;

        public VideoProcessor(String location, String saveLocation, Intent intent) {
            this.location = location;
            this.saveLocation = saveLocation;
            this.intent = intent;
        }

        @Override
        public void run() {
            super.run();
            // Move the file
            Uri uri = Uri.fromFile(new File(location));
            if (intent != null) {
                try {
                    if (activity != null) {
                        inputStreamToFile(activity.getContentResolver().openInputStream(intent.getData()), uri);
                    } else if (fragment != null) {
                        inputStreamToFile(fragment.getActivity().getContentResolver().openInputStream(intent.getData()), uri);
                    } else {
                        loader.failed(Error.ACTIVITY_OR_FRAGMENT_NOT_FOUND);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    loader.failed(Error.FILE_NOT_FOUND);
                }
            }
            moveFile(location, saveLocation);
            // Save the file
            loader.imageFinished(saveLocation);
        }
    }


    /**
     * TYPES
     **/
    public static class Type {
        public static final int CAMERA_PHOTOS = 1;
        public static final int CAMERA_VIDEO = 2;
        public static final int GALLERY_PHOTOS = 4;
        public static final int GALLERY_VIDEO = 5;
        public static final int GALLERY_PHOTOS_AND_VIDEO = 6;
    }

    public static class Error {
        public static final int WRONG_INTENT_TYPE = 1;
        public static final int ACTIVITY_RESULT_FAIL = 2;
        public static final int ACTIVITY_OR_FRAGMENT_NOT_FOUND = 3;
        public static final int ACTIVITY_NO_PACKAGE_MANAGER = 4;
        public static final int FILE_NOT_FOUND = 7;
        public static final int ACTIVITY_UNKNOWN_MEDIA = 8;
    }

    public static String IMAGE_EXTENSION = ".png";
    public static String VIDEO_EXTENSION = ".mp4";

    /**
     * LOGGER
     **/
    public static boolean showErrors = true;

    public static void o(String message) {
        if (showErrors) {
            Log.i("ImageDownload", message);
        }
    }

    /**
     * STORAGE STUFF
     **/
    private static boolean moveFile(File from, File to) {
        return from.getParentFile().exists() && from.exists() && from.renameTo(to);
    }

    private static boolean moveFile(String from, String to) {
        return moveFile(new File(from), new File(to));
    }


    /**
     * CALLBACKS
     **/
    public interface ImageLoader {
        public void imageFinished(String path);

        public void failed(int error);

        public void showProgress(boolean done);
    }


    /**
     * ELLIOTS STATIC METHODS
     **/
    private static void inputStreamToFile(InputStream is, Uri location) {
        try {
            File outputFile = new File(location.getPath());
            FileOutputStream os = new FileOutputStream(outputFile);

            int read;
            byte[] bytes = new byte[1024];

            while ((read = is.read(bytes)) != -1) {
                os.write(bytes, 0, read);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveBitmapToFile(Bitmap bitmap, Uri uri) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(uri.getPath());
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Bitmap rotateBitmap(Bitmap input, int rotation) {
        Matrix matrix = new Matrix();
        if (rotation != 0f) {
            matrix.preRotate(rotation);
        }
        return Bitmap.createBitmap(input, 0, 0, input.getWidth(), input.getHeight(), matrix, true);
    }


    private static int imageRotation(Uri uri) {
        File file = new File(uri.getPath());
        try {
            ExifInterface exif = new ExifInterface(file.getPath());
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_ROTATE_90);
            return exifToDegrees(rotation);
        } catch (IOException e) {
            return 0;
        }
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private static Bitmap decodeSampledBitmapFromResource(Uri uri, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uri.getPath(), options);

        int width = reqWidth;
        int height = reqHeight;

        boolean finished = false;
        Bitmap bitmap = null;
        while (!finished) {
            try {
                // Calculate inSampleSize
                options.inSampleSize = calculateInSampleSize(options, width, height);

                // Decode bitmap with inSampleSize set
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeFile(uri.getPath(), options);
                finished = true;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                width *= 0.75;
                height *= 0.75;
            }
        }

        return bitmap;
    }
}
