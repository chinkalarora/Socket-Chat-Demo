package com.mbass.myapplication.util;

import android.net.Uri;


import androidx.annotation.NonNull;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class NetworkUtils {

    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final String APPLICATION = "application/x-www-form-urlencoded";


    @NonNull
    public static RequestBody createPartFromString(String descriptionString) {
        RequestBody sample = null;

        try{
                sample = RequestBody.create(MediaType.parse(APPLICATION), descriptionString);
        }catch (Exception e){
            e.printStackTrace();
        }

        return sample;
    }

    @NonNull
    public static MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
        File file = new File(fileUri.getPath());

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), file);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

}