package io.sylmos.bonk.utils;

import android.graphics.Bitmap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public final class Utils {

    public static OutputStream getOutputStream(final Bitmap bitmap) {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream;
    }

    public static InputStream getInputStream(final Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        byte[] bitmapData = bos.toByteArray();
        return new ByteArrayInputStream(bitmapData);
    }

    public static <T> List<T> hashMapToList(HashMap<String, Object> map) {
        List<T> list = new ArrayList<>();
        map.forEach((s, t) -> list.set(Integer.parseInt(s), (T) t));
        return list;
    }

    public static <T> HashMap<String, T> listToHashMap(List<T> list) {
        HashMap<String, T> map = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            T t = list.get(i);
            map.put(String.valueOf(i), t);
        }
        return map;
    }

    public static String limitTextLength(String base, int maxLength) {
        if (base.length() <= maxLength) return base;
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for (char c : base.toCharArray()) {
            if (i == maxLength) break;
            builder.append(c);
            i++;
        }
        return builder.append("...").toString();
    }

    public static File saveBitmap(Bitmap bitmap, File location) throws IOException {
        ByteArrayOutputStream outputStream = (ByteArrayOutputStream) getOutputStream(bitmap);
        location.getParentFile().mkdirs();
        location.createNewFile();
        FileOutputStream fileOut = new FileOutputStream(location);
        fileOut.write(outputStream.toByteArray());
        fileOut.flush();
        fileOut.close();
        return location;
    }

    public static List<String> listToLowerCase(List<String> list, Locale locale) {
        List<String> newList = new ArrayList<>();
        for (String s : list) {
            newList.add(s.toLowerCase(locale));
        }
        return newList;
    }

    public static List<String> listToUpperCase(List<String> list, Locale locale) {
        List<String> newList = new ArrayList<>();
        for (String s : list) {
            newList.add(s.toLowerCase(locale));
        }
        return newList;
    }

}
