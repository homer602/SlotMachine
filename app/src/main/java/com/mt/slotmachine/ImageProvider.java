package com.mt.slotmachine;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.JsonReader;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


public class ImageProvider {


    public static class ImagePair {

        String name;
        Bitmap bm;


        public ImagePair(String name, Bitmap bm) {
            this.name = name;
            this.bm = bm;
        }
    }

    public static InputStream getInStreamFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return input;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public static Bitmap getBitmapFromURL(String src) {
        InputStream input = getInStreamFromURL(src);
        if (input!=null) {
            return BitmapFactory.decodeStream(input);
        }
        return Bitmap.createBitmap(null);

    }

    public static ImagePair[] parseURL (String url) throws IOException {

        List tmp = readJsonStream(getInStreamFromURL(url));
        ImagePair[] result = new ImagePair[tmp.size()];

        int i=0;
        for (Iterator<List> iter=tmp.iterator();iter.hasNext();) {
            result[i] = (ImagePair) iter.next();
            i++;
            //Log.d("mich",result[i].name);
        }

        return result;
    }



    public static List readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {

            return readMessagesArray(reader);
        }
            finally{
                reader.close();
            }
    }

    public static ImagePair readMessage(JsonReader reader) throws IOException {
        String name = new String();
        String url = new String();

        reader.beginObject();

        while (reader.hasNext()) {
            AtomicReference<String> token = new AtomicReference<>(reader.nextName());
            if (token.get().equals("name")){
                name = reader.nextString();
            } else if (token.get().equals("url")) {
                url = reader.nextString();
            }  else {
                reader.skipValue();
            }
        }
        reader.endObject();

        Bitmap bm = getBitmapFromURL(url);
        return new ImagePair(name, bm);
    }

    public static List readMessagesArray (JsonReader reader)throws IOException {
         List messages = new ArrayList();
         reader.beginObject();
         if (reader.nextName().equals("images")) {/*Log.d ("mich","found images");*/}
            //
         reader.beginArray();
            while (reader.hasNext()) {
                messages.add(readMessage(reader));
            }
            reader.endArray();
            return messages;
    }


}
