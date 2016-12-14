
package cc.ak.sdk.tool;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResourceSort
{
    public static List<String> all = null;
    public static List<String> typeSequence = null;
    public static Map<String, List<String>> unique = null;
    public static DataOutputStream out = null;
    public static DataInputStream in = null;
    public static String file = null;

    public static Pattern pattern = Pattern
            .compile("<public type=\"(\\w*?)\" name=\"([ \\w\\.]*?)\" id=\"0x([\\dabcdef]*)\" />");

    public void startPublic(String fileName) {
        List<String> all = new ArrayList<String>();
        unique = new HashMap<String, List<String>>();
        typeSequence = new ArrayList<String>();

        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("file 地址不正确！！！");
            return;
        }

        try {
            in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
            try {
                String line = null;
                int i = 0;
                String type = null;
                while ((line = in.readLine()) != null) {
                    type = getValue(line, 1);
                    if (type == null) {
                        System.out.println(line);
                        continue;
                    }
                    if (!unique.containsKey(type)) {
                        List<String> newList = new ArrayList<String>();
                        unique.put(type, newList);
                        typeSequence.add(type);
                    }
                    List<String> tempList = unique.get(type);
                    tempList.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        /*******************sort and write our xml*******************/
        try {
            out = new DataOutputStream(new FileOutputStream(file + "_new.xml"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            out.writeBytes("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            out.writeBytes("<resources>\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        MyComparator myComparator = new MyComparator();
        for (int i = 0; i < typeSequence.size(); i++) {
            List<String> list = unique.get(typeSequence.get(i));

            Collections.sort(list, new MyComparator());

            for (int j = 0; j < list.size(); j++) {
                String line = list.get(j);
                try {
                    out.writeBytes(line + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            out.writeBytes("</resources>\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getValue(String str, int groupIndex/*1 type,2 name,3 id*/) {
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group(groupIndex);
        }
        return null;
    }
}
