package com.reveetech.cat.http.util;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * description：文件辅助类
 * <br>author：caowugao
 * <br>time： 2017/04/14 18:01
 */

public class FileUtil {
    private FileUtil() {
    }

    /**
     * @param dir        在sdcard中的目录
     * @param name       在sdcard中文件名
     * @param assetsFile assets文件夹中完整路径，如"images/yangzi.jpg:
     * @param context
     * @return void
     * @ 功    能：复制assets文件到sdcard中
     */
    public static void copyAssetsFile2Sdcard(String dir, String name, String assetsFile, Context context) {
        File file = new File(dir, name);
        if (!file.exists()) {
            try {
                InputStream inputStream = context.getAssets().open(assetsFile);
                writeFileByOutputStream(dir, name, inputStream, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 功    能：以输出流的形式保存文件
     *
     * @param dir
     * @param name
     * @param inputStream
     * @param isClosed    保存完成后是否关闭输入流
     * @return void
     */
    public static void writeFileByOutputStream(String dir, String name, InputStream inputStream, boolean isClosed) {
        File filePath = fixRealFile(dir, name);
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        try {
            fos = new FileOutputStream(filePath);
            bos = new BufferedOutputStream((fos));
            bis = new BufferedInputStream(inputStream);
            byte[] buff = new byte[20 * 1024];
            int len;
            while ((len = bis.read(buff)) > 0) {
                bos.write(buff, 0, len);
            }
            bos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                closeIO(bos, bis, fos);
                if (isClosed) {
                    closeIO(inputStream);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 如果目录不存在，则创建目录，如果文件不存在，则创建文件
     *
     * @param dir  文件目录
     * @param name 文件名
     * @return File 返回最终的文件
     */
    public static File fixRealFile(String dir, String name) {
        String path = dir + File.separator + name;
        File filePath = new File(path);
        if (!filePath.exists()) {
            File dirFile = new File(dir);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            try {
                filePath.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filePath;
    }

    /**
     * 关闭流
     *
     * @param closeables
     * @throws Exception
     */
    public static void closeIO(Closeable... closeables) throws Exception {
        if (null == closeables || closeables.length <= 0) {
            return;
        }
        for (Closeable cb : closeables) {
            try {
                if (null == cb) {
                    continue;
                }
                cb.close();
            } catch (IOException e) {
                throw new Exception(FileUtil.class.getClass().getName(), e);
            }
        }
    }

    /**
     * 字符流的形式保存文件
     *
     * @param dir  在sdcard中的目录
     * @param name 在sdcard中文件名
     * @param data
     * @return void
     */
    public static void writeFileByWriter(String dir, String name, String data) {
        File filePath = fixRealFile(dir, name);
        FileOutputStream fileOut = null;
        BufferedWriter bw = null;
        OutputStreamWriter osw = null;
        try {
            fileOut = new FileOutputStream(filePath);
            osw = new OutputStreamWriter(fileOut);
            bw = new BufferedWriter(osw);
            bw.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                closeIO(bw, osw, fileOut);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从文件中读取文本
     *
     * @param filePath
     * @return String
     * @throws Exception
     */
    public static String readFile(String filePath) throws Exception {
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
//            throw new Exception(FileUtil.class.getName() + "readFile---->" + filePath + " not found");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return inputStream2String(is);
    }

    /**
     * 输入流转字符串
     *
     * @param is
     * @return String
     */
    public static String inputStream2String(InputStream is) {
        if (null == is) {
            return null;
        }
        StringBuilder resultSb = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            resultSb = new StringBuilder();
            String line;
            while (null != (line = br.readLine())) {
                resultSb.append(line + "\n");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                closeIO(is);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null == resultSb ? null : resultSb.toString();
    }


}
