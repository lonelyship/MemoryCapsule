package com.lonelyship.CommonUtils;


import android.os.Environment;
import android.os.StatFs;

import java.text.DecimalFormat;

/**
 * 取得手機硬碟空間有多少的類別
 * 參考:http://stackoverflow.com/questions/7115016/how-to-find-the-amount-of-free-storage-disk-space-left-on-android
 */

public class DiskUtils {
    private static final long MEGA_BYTE = 1048576;

    /**
     * Calculates total space on disk
     * @param external  If true will query external disk, otherwise will query internal disk.
     * @return Number of mega bytes on disk.
     */
    public static long lGetTotalSpace(boolean external)
    {
        StatFs statFs = getStats(external);
        long total = statFs.getBlockCountLong() * statFs.getBlockSizeLong();
        return total;
    }

    public static String sGetTotalSpace(boolean external)
    {
        StatFs statFs = getStats(external);
        long total = statFs.getBlockCountLong() * statFs.getBlockSizeLong();
        return bytesToHuman(total);
    }

    /**
     * Calculates free space on disk
     * @param external  If true will query external disk, otherwise will query internal disk.
     * @return Number of free mega bytes on disk.
     */
    public static long lGetFreeSpace(boolean external)
    {
        StatFs statFs = getStats(external);
        long availableBlocks = statFs.getAvailableBlocksLong();
        long blockSize = statFs.getBlockSizeLong();
        long freeBytes = availableBlocks * blockSize;

        return freeBytes;
    }

    public static String sGetFreeSpace (boolean external)
    {
        StatFs statFs = getStats(external);
        long availableBlocks = statFs.getAvailableBlocksLong();
        long blockSize = statFs.getBlockSizeLong();
        long freeBytes = availableBlocks * blockSize;
        return bytesToHuman(freeBytes);
    }

    /**
     * Calculates occupied space on disk
     * @param external  If true will query external disk, otherwise will query internal disk.
     * @return Number of occupied mega bytes on disk.
     */
    public static long lGetBusySpace(boolean external)
    {
        long busy = lGetTotalSpace(external) - lGetFreeSpace(external);
        return busy;
    }

    public static String sGetBusySpace(boolean external) {
        long busy = lGetTotalSpace(external) - lGetFreeSpace(external);
        return bytesToHuman(busy);
    }

    
    private static StatFs getStats(boolean external){
        String path;

        if (external){
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        else{
            path = Environment.getRootDirectory().getAbsolutePath();
        }

        return new StatFs(path);
    }

    public static String floatForm (double d)
    {
        return new DecimalFormat("#.##").format(d);
    }

    public static String bytesToHuman (long size)
    {
        long Kb = 1  * 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        long Tb = Gb * 1024;
        long Pb = Tb * 1024;
        long Eb = Pb * 1024;

        if (size <  Kb)                 return floatForm(        size     ) + " bytes";
        if (size >= Kb && size < Mb)    return floatForm((double)size / Kb) + " KB";
        if (size >= Mb && size < Gb)    return floatForm((double)size / Mb) + " MB";
        if (size >= Gb && size < Tb)    return floatForm((double)size / Gb) + " GB";
        if (size >= Tb && size < Pb)    return floatForm((double)size / Tb) + " TB";
        if (size >= Pb && size < Eb)    return floatForm((double)size / Pb) + " PB";
        if (size >= Eb)                 return floatForm((double)size / Eb) + " EB";

        return "undefined";
    }

}