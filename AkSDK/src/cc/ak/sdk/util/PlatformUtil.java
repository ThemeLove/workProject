
package cc.ak.sdk.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class PlatformUtil {

    private static String channel = null;
    private static final String CX_QUDAO_NAME = "cx_channel_";

    public static String getChannel(Context context) {
        if (!TextUtils.isEmpty(channel) && !TextUtils.equals(channel, "cx_demo")) {
            return channel;
        }

        final String start_flag = "META-INF/" + CX_QUDAO_NAME;
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.contains(start_flag)) {
                    channel = entryName.replace(start_flag, "");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (TextUtils.isEmpty(channel)) {
            channel = "cx_demo";
        }
        return channel;
    }
}
