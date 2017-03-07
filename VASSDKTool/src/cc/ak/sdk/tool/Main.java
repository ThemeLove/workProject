package cc.ak.sdk.tool;

import cc.ak.sdk.tool.bean.Channel;
import cc.ak.sdk.tool.bean.Game;
import cc.ak.sdk.tool.bean.GameChannel;
import cc.ak.sdk.tool.bean.Keystore;
import cc.ak.sdk.tool.bean.Operation;
import cc.ak.sdk.tool.parser.ChannelXMLParser;
import cc.ak.sdk.tool.parser.GameChannelXMLParser;
import cc.ak.sdk.tool.parser.GameXMLParser;
import cc.ak.sdk.tool.parser.KeystoreXMLParser;
import cc.ak.sdk.tool.util.FileUtil;
import cc.ak.sdk.tool.util.StreamGobbler;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.imageio.ImageIO;
import javax.swing.plaf.SliderUI;

public class Main
{

    public static String BASE_PATH = "./";

    public static String GAMES_PATH;

    public static String CONFIG_PATH;

    public static Game game = null;

    public static OutputFormat xmlFormat;

    public static List<GameChannel> gameChannelList;

    public static String mainActivityName = "";

    public static void main(String[] args) throws Exception
    {
        // 初始化路径
        BASE_PATH = System.getProperty("user.dir");
        GAMES_PATH = BASE_PATH + File.separator + "games";
        CONFIG_PATH = BASE_PATH + File.separator + "config";

        // 初始化xmlFormat
        xmlFormat = new OutputFormat("    ", true, "UTF-8");

        // 加载游戏列表
        String gamesXmlPath = GAMES_PATH + File.separator + "games.xml";
        Map<String, Game> gameMap = GameXMLParser.parser(gamesXmlPath);
        // 选择游戏
        String gameId = keybordIn("请选择一个游戏(输入gameId)：");
        game = gameMap.get(gameId);
        while (game == null)
        {
            gameId = keybordIn("您输入的gameId有误，请从输：");
            game = gameMap.get(gameId);
        }

        // 加载渠道列表
        String gameChannelXmlPath = GAMES_PATH + File.separator + game.getFolder() + File.separator + "channel.xml";
        Map<String, GameChannel> gameChannelMap = GameChannelXMLParser.parser(gameChannelXmlPath);
        // 选择渠道
        String channelId = keybordIn("请选择渠道(多个用逗号隔开，all为所有)：");
        gameChannelList = new ArrayList<GameChannel>();
        if ("all".equals(channelId))
        {
            gameChannelList.addAll(gameChannelMap.values());
        }
        else
        {
            for (String cid : channelId.split(","))
            {
                GameChannel gameChannel = gameChannelMap.get(cid);
                if (gameChannel != null)
                {
                    gameChannelList.add(gameChannel);
                }
                else
                {
                    System.out.println("渠道" + cid + "不存在");
                }
            }
        }
        if (gameChannelList.size() == 0)
        {
            System.out.println("没有找到您输入的渠道");
            System.exit(0);
            return;
        }
        
        // 清空work目录
        System.out.println("程序初始化");
        String workPath = BASE_PATH + File.separator + "work";
        FileUtil.delete(workPath);
        new File(workPath).mkdir();
        
        /////////////////////////////////////////
        //将控制台输出写入文件
//        File f=new File(workPath + File.separator + "out.txt");  
//        f.createNewFile();
//        FileOutputStream fileOutputStream = new FileOutputStream(f);  
//        PrintStream printStream = new PrintStream(fileOutputStream);  
//        System.setOut(printStream);
        /////////////////////////////////////////

        String bakPath = workPath + File.separator + "bak";
        String apktoolPath = BASE_PATH + File.separator + "apktool";
        File apktool = new File(apktoolPath);
        exeCmd("java -jar -Xms512m -Xmx512m apktool.jar if framework-res.apk", apktool);

        String gamePath = GAMES_PATH + File.separator + game.getFolder() + File.separator + "game.apk";
        String unZipCommand = String.format("java -jar -Xms512m -Xmx512m apktool.jar d -o %s %s", new String[] {
                bakPath, gamePath});
        System.out.println("解压APK，执行命令：" + unZipCommand);
        exeCmd(unZipCommand, apktool);

        // 遍历渠道
        for (GameChannel gameChannel : gameChannelList)
        {
            System.out.println("开始打包，渠道【" + gameChannel.getId() + "】");
            String sdkPath = CONFIG_PATH + File.separator + "sdk" + File.separator + gameChannel.getId();
            String sdkConfigPath = sdkPath + File.separator + "config.xml";
            Channel channel = ChannelXMLParser.parser(sdkConfigPath);

            // 清空temp目录
            String tempPath = workPath + File.separator + "temp";
            FileUtil.delete(tempPath);

            // 复制原始文件
            FileUtil.copyDirectiory(bakPath, tempPath);

            // 修改包名
            System.out.println("修改包名");
            String packageName = renamePackage(gameChannel, tempPath);

            // System.out.println("修改lunchModle属性");
            // renameMainActiviyLunchModle(gameChannel, tempPath);

            // 合并资源
            System.out.println("合并资源");
            List<Operation> operations = channel.getOperations();
            if (operations != null)
            {
                executeOperation(sdkPath, tempPath, operations);
            }

            // 闪屏
             if (gameChannel.isSplash()) {
             System.out.println("添加闪屏图片");
             addSplashImg(tempPath, sdkPath);
             }

            // 合并角标
            if (gameChannel.getIcon() != null)
            {
                System.out.println("添加角标");
                megerIcon(gameChannel, sdkPath, tempPath);
            }

            // 生成R.jar到lib
            System.out.println("生成R文件");
            String aaptPath = apktoolPath + File.separator + "aapt";
            String resPath = tempPath + File.separator + "res";
            String androidPath = apktoolPath + File.separator + "android.jar";
            String manifestPath = tempPath + File.separator + "AndroidManifest.xml";
            String rPath = workPath + File.separator + "r";
            File r = new File(rPath);
            r.mkdir();
            String aaptCommand = String.format("%s p -f -m -J %s -S %s -I %s -M %s", new String[] {aaptPath, rPath,
                    resPath, androidPath, manifestPath});
            exeCmd(aaptCommand, apktool);

            String packagePath = rPath + File.separator + packageName.replaceAll("\\.", "\\/");
            String javaPath = packagePath + File.separator + "R.java";
            exeCmd("javac -source 1.7 -target 1.7 -encoding UTF-8 " + javaPath, apktool);
            FileUtil.delete(javaPath);

            String libPath = tempPath + File.separator + "lib";
            File libDir = new File(libPath);
            if (!libDir.exists())
            {
                libDir.mkdirs();
            }
            String jarPath = libPath + File.separator + "R.jar";
            exeCmd("jar cvf " + jarPath + " *", r);
            FileUtil.delete(rPath);

            // jar转为smali
            System.out.println("jar转smali");
            jar2Smali(apktool, tempPath, libDir);

            // 合并meta-data
            Map<String, String> metaDataMap = gameChannel.getMetaDatas();
            if (metaDataMap != null)
            {
                System.out.println("合并meta-data");
                String xmlPath = tempPath + File.separator + "AndroidManifest.xml";
                SAXReader saxReader = new SAXReader();
                Document document = saxReader.read(new File(xmlPath));
                Element root = document.getRootElement();
                Element application = root.element("application");
                Namespace namespace = root.getNamespaceForPrefix("android");

                for (String key : metaDataMap.keySet())
                {
                    Element metaData = DocumentHelper.createElement("meta-data");
                    QName name = new QName("name", namespace);
                    metaData.addAttribute(name, key);
                    QName value = new QName("value", namespace);
                    metaData.addAttribute(value, metaDataMap.get(key));
                    application.add(metaData);
                }
                XMLWriter writer = new XMLWriter(new FileWriter(new File(xmlPath)), xmlFormat);
                writer.write(document);
                writer.close();
            }
            // 闪屏
            if (gameChannel.isSplash())
            {
                System.out.println("添加闪屏Activity");
                addSplashActivity(tempPath, packageName);
            }

            // 合并AndroidManifest.xml
            System.out.println("合并AndroidManifest.xml");
            File androidManifestFile = new File(tempPath + File.separator + "AndroidManifest.xml");
            megerAndroidManifest(sdkPath, androidManifestFile);


            // 替换
            Map<String, String> channelParams = gameChannel.getChannelParams();
            if (channelParams == null)
            {
                channelParams = new HashMap<String, String>();
            }
            channelParams.put("PACKAGENAME", packageName);
            for (String key : channelParams.keySet())
            {
                FileUtil.replaceFileContent("\\{" + key + "\\}", channelParams.get(key), androidManifestFile);
            }

            // 创建vassdk_config.xml
            System.out.println("创建vassdk_config.xml");
            createVasSDKConfigXML(gameChannel, channel, tempPath);

            // 创建产出目录
            String outPath = BASE_PATH + File.separator + "out" + File.separator + gameChannel.getId();
            File out = new File(outPath);
            if (!out.exists())
            {
                out.mkdirs();
            }

            String publicXmlPath = tempPath + File.separator + "res" + File.separator + "values" + File.separator
                    + "public.xml";
            //TODO 添加分dex方法
//            mutiSmali();
            
            
            // 打包
            System.out.println("APK打包");
            String tempApkPath = outPath + File.separator + "temp.apk";
            String zipCommand = String.format("java -jar apktool.jar b -o %s %s", new String[] {tempApkPath, tempPath});
            exeCmd(zipCommand, apktool);

            // 签名
            System.out.println("生成签名包");
            if (gameChannel.getKeystore() == null)
            {
                gameChannel.setKeystore("vas"); // 默认keystore
            }
            String keystoreDir = CONFIG_PATH + File.separator + "keystore" + File.separator + gameChannel.getKeystore();
            String keystorePath = keystoreDir + File.separator + gameChannel.getKeystore() + ".keystore";
            String signApkPath = outPath + File.separator + "sign.apk";

            String xmlPath = keystoreDir + File.separator + "config.xml";
            Keystore keystore = KeystoreXMLParser.parser(xmlPath);
            String signCommand = String.format(
                    "jarsigner -digestalg SHA1 -sigalg SHA1withRSA -keystore %s -storepass %s -keypass %s -signedjar %s %s %s",
                    new String[] {keystorePath, keystore.getPasword(), keystore.getAliasPwd(), signApkPath,
                            tempApkPath, keystore.getAlias()});
            exeCmd(signCommand, apktool);
            FileUtil.delete(tempApkPath);

            // 优化
            System.out.println("生成最终包");
            String zipalignPath = apktoolPath + File.separator + "zipalign";
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
            Date currentTime = new Date();
            String dateTime = format.format(currentTime);
            String finalApkPath = outPath + File.separator + game.getFolder() + "_" + gameChannel.getId() + dateTime + ".apk";
            File finalApk = new File(finalApkPath);
            if (finalApk.exists())
            {
                finalApk.delete();
            }
            String zaCommand = String.format("%s -v 4 %s %s", new String[] {zipalignPath, signApkPath, finalApkPath});
            exeCmd(zaCommand, apktool);
            FileUtil.delete(signApkPath);

            System.out.println("打包完成，渠道【" + gameChannel.getId() + "】");

        }

        System.out.println("打包程序执行完成");

    }

    /**
     * 执行分dex方法
     */
    private static void mutiSmali()
    {

        try
        {
            String smalipyPath = "D:\\workspace_eclipse_javaee\\VASSDKTool\\apktool\\smali.py";
            Process pr = Runtime.getRuntime().exec("C:\\Python27\\python.exe " + smalipyPath);
            BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;
            while ((line = in.readLine()) != null)
            {
                System.out.println(line);
            }
            in.close();
            pr.waitFor();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        System.out.println("分dex完成");

    }

    // 合并角标
    private static void megerIcon(GameChannel gameChannel, String sdkPath, String tempPath) throws Exception
    {
        // 获得启动图标名称
        String xmlPath = tempPath + File.separator + "AndroidManifest.xml";
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(new File(xmlPath));
        Element root = document.getRootElement();
        Element application = root.element("application");
        String iconName = application.attributeValue("icon");
        if (iconName != null)
        {
            iconName = iconName.replaceAll("@drawable/", "") + ".png";
        }

        System.out.println("启动图标名称：" + iconName);

        // 遍历res中的启动图标
        String resPath = tempPath + File.separator + "res";
        File res = new File(resPath);
        for (File file : res.listFiles())
        {
            File iconFile = new File(file.getPath() + File.separator + iconName);
            if (iconFile.exists())
            {
                int size = 32;
                String iconMarkPath = sdkPath + File.separator + "icon" + File.separator;
                if (file.getName().indexOf("drawable-ldpi") != -1)
                {
                    size = 32;
                    iconMarkPath += "drawable-ldpi" + File.separator + gameChannel.getIcon() + ".png";
                }
                else if (file.getName().indexOf("drawable-mdpi") != -1)
                {
                    size = 48;
                    iconMarkPath += "drawable-mdpi" + File.separator + gameChannel.getIcon() + ".png";
                }
                else if (file.getName().indexOf("drawable-hdpi") != -1)
                {
                    size = 72;
                    iconMarkPath += "drawable-hdpi" + File.separator + gameChannel.getIcon() + ".png";
                }
                else if (file.getName().indexOf("drawable-xhdpi") != -1)
                {
                    size = 96;
                    iconMarkPath += "drawable-xhdpi" + File.separator + gameChannel.getIcon() + ".png";
                }
                else if (file.getName().indexOf("drawable-xxhdpi") != -1)
                {
                    size = 144;
                    iconMarkPath += "drawable-xxhdpi" + File.separator + gameChannel.getIcon() + ".png";
                }
                else if (file.getName().indexOf("drawable-xxxhdpi") != -1)
                {
                    size = 512;
                    iconMarkPath += "drawable-xxxhdpi" + File.separator + gameChannel.getIcon() + ".png";
                }
                else
                { // 默认为drawable-xxhdpi
                    size = 144;
                    iconMarkPath += "drawable-xxhdpi" + File.separator + gameChannel.getIcon() + ".png";
                }

                File iconMark = new File(iconMarkPath);
                if (iconMark.exists())
                {
                    Positions positions = Positions.TOP_LEFT;
                    if ("rb".equals(gameChannel.getIcon()))
                    {
                        positions = Positions.BOTTOM_RIGHT;
                    }
                    else if ("rt".equals(gameChannel.getIcon()))
                    {
                        positions = Positions.TOP_RIGHT;
                    }
                    else if ("lb".equals(gameChannel.getIcon()))
                    {
                        positions = Positions.BOTTOM_LEFT;
                    }

                    System.out.println("角标 size = " + size + ",iconMarkPath = " + iconMarkPath);
                    System.out.println("iconFile.getPath() " + iconFile.getPath());
                     // 将角标图片压缩
                     Thumbnails.of(iconMark.getPath()).size(size,
                     size).keepAspectRatio(true).toFile(iconMark.getPath());
                    // 将icon图片压缩
                    // Thumbnails.of(iconFile.getPath()).size(size,
                    // size).keepAspectRatio(true).toFile(iconFile.getPath());
                    // 添加角标
                    Thumbnails.of(iconFile.getPath()).size(size, size).watermark(positions, ImageIO.read(iconMark), 1f).toFile(
                            iconFile.getPath());
                    Thread.sleep(200);// 不加延时，hdpi有时角标水印没有加上
                }
            }
        }
    }

    private static void jar2Smali(File apktool, String tempPath, File libDir) throws Exception
    {
        for (File file : libDir.listFiles())
        {
            if (file.getName().endsWith(".jar"))
            {
                String outFilePath = file.getPath().replace(".jar", ".dex");
                String jar2DexCommand = "java -jar -Xms512m -Xmx512m dx.jar --dex --output=" + outFilePath + " "
                        + file.getPath();
                exeCmd(jar2DexCommand, apktool);
                FileUtil.delete(file.getPath());

                String smaliPath = tempPath + File.separator + "smali";
                String dex2SmaliCommand = "java -jar baksmali.jar -o " + smaliPath + " " + outFilePath;
                exeCmd(dex2SmaliCommand, apktool);
                FileUtil.delete(outFilePath);
            }
        }

        if (libDir.listFiles().length == 0)
        {
            libDir.delete();
        }
    }

    // 处理合并
    private static void executeOperation(String sdkPath, String tempPath, List<Operation> operations) throws Exception
    {
        for (Operation operation : operations)
        {
            if ("replaceManifest".equals(operation.getType()))
            {
                String manifestPath = tempPath + File.separator + "AndroidManifest.xml";
                File manifestFile = new File(manifestPath);
                FileUtil.replaceFileContent(operation.getFrom(), operation.getTo(), manifestFile);
            }
            else
            {
                String fromPath = sdkPath + File.separator + operation.getFrom();
                String toPath = tempPath + File.separator + operation.getTo();
                FileUtil.copyDirectiory(fromPath, toPath);
            }
        }
    }

    /**
     * @param tempLibPath
     */
    private static void listTempLibFile(String tempLibPath)
    {

    }

    // 合并AndroidManifest
    private static void megerAndroidManifest(String sdkPath, File androidManifestFile) throws Exception
    {
        String manifestName = "landscape";
        try
        {
            manifestName = game.getOrientation().toLowerCase();
        }
        catch (Exception ex)
        {
        }

        String fromPath = sdkPath + File.separator + "manifest" + File.separator + manifestName + ".xml";
        File fromFile = new File(fromPath);
        if (!fromFile.exists())
        {
            fromPath = sdkPath + File.separator + "manifest" + File.separator + "landscape.xml";
        }

        SAXReader fromSaxReader = new SAXReader();
        Document fromDoc = fromSaxReader.read(new File(fromPath));
        Element fromRoot = fromDoc.getRootElement();
        Element permissionConfig = fromRoot.element("permissionConfig");
        Element applicationConfig = fromRoot.element("applicationConfig");

        SAXReader toSaxReader = new SAXReader();
        Document toDoc = toSaxReader.read(androidManifestFile);
        Element toRoot = toDoc.getRootElement();
        Element application = toRoot.element("application");
        List<Element> permissions = toRoot.elements("uses-permission");

        if (permissionConfig != null)
        {
            out: for (Element permission : permissionConfig.elements())
            {
                if (permissions != null)
                {
                    for (Element hasPermission : permissions)
                    {
                        if (permission.attributeValue("name").equals(hasPermission.attributeValue("name")))
                        {
                            continue out;
                        }
                    }
                }
                toRoot.add(permission.createCopy());
            }
        }

        if (applicationConfig != null)
        {
            for (Element appElement : applicationConfig.elements())
            {
                application.add(appElement.createCopy());
            }
        }

        XMLWriter writer = new XMLWriter(new FileWriter(androidManifestFile), xmlFormat);
        writer.write(toDoc);
        writer.close();
    }

    // 添加闪屏图片
    private static void addSplashImg(String tempPath, String sdkPath) throws Exception
    {
        String splashImgName = "landscape";
        try
        {
            splashImgName = game.getOrientation().toLowerCase();
        }
        catch (Exception ex)
        {
        }
        String splashImgPath = sdkPath + File.separator + "splash" + File.separator + splashImgName + ".png";
        File splashImgFile = new File(splashImgPath);
        if (!splashImgFile.exists())
        {
            System.out.println("该渠道为配置闪屏图片");
            return;
        }

        String drawablePath = tempPath + File.separator + "res" + File.separator + "drawable-hdpi";
        File drawableFile = new File(drawablePath);
        if (!drawableFile.exists())
        {
            drawableFile.mkdirs();
        }
        String drawableSplashPath = drawablePath + File.separator + "vas_sdk_splash.png";
        File drawableSplashFile = new File(drawableSplashPath);
        FileUtil.copyFile(splashImgFile, drawableSplashFile);
    }

    // 添加闪屏Activity
    private static void addSplashActivity(String tempPath, String packageName) throws Exception
    {
        String xmlPath = tempPath + File.separator + "AndroidManifest.xml";
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(new File(xmlPath));
        Element root = document.getRootElement();

        Element main = null;
        Element launcher = null;
        Element splashActivity = null;
        String oldMainActivityName = null;
        Element application = root.element("application");
        List<Element> activitys = application.elements("activity");
        System.out.println("activitys == " + activitys.size());
        out: for (Element activity : activitys)
        { // 遍历找到启动的Activity
            Element intentFilter = activity.element("intent-filter");//没有循环main这个一定要放在第一个intent-filter里面
            if (intentFilter != null)
            {
                for (Element filter : intentFilter.elements())
                {
                    System.out.println(filter.getName() + ":" + filter.attributeValue("name"));
                    if ("action".equals(filter.getName())
                            && "android.intent.action.MAIN".equals(filter.attributeValue("name")))
                    {
                        main = filter.createCopy();
                    }
                    else if ("category".equals(filter.getName())
                            && "android.intent.category.LAUNCHER".equals(filter.attributeValue("name")))
                    {
                        launcher = filter.createCopy();
                    }
                    System.out.println("main : " + main);
                    System.out.println("launcher : " + launcher);
                    if (main != null && launcher != null)
                    {

                        mainActivityName = activity.attribute("name").getValue(); // 获取启动Activity的名字
                        System.out.println("mainActivityName : " + mainActivityName);

                        // 获取旧启动的Activity名称，用于替换闪屏
                        oldMainActivityName = activity.attributeValue("name");
                        if (oldMainActivityName.startsWith("."))
                        {
                            oldMainActivityName = packageName + oldMainActivityName;
                        }

//                        splashActivity = activity.createCopy();
                        // 替换成渠道的闪屏名称
                        // splashActivity.attribute("name").setValue("com.vas.quicksdk.SplashActivity");
//                        splashActivity.attribute("name").setValue("com.vas.vassdk.VasSdkSplashActivity");

                        // 移除启动元素
                        if (intentFilter.elements().size() == 2)
                        {
                            activity.remove(intentFilter);
                        }
                        else
                        {
                            intentFilter.remove(main);
                            intentFilter.remove(launcher);
                        }
                        activity.remove(intentFilter);
                        break out;
                    }
                }
            }
        }
        
//        if (splashActivity != null)
//        {
//            Element intentFilter = splashActivity.element("intent-filter");
//            intentFilter.clearContent();
//            intentFilter.add(main);
//            intentFilter.add(launcher);
//            application.add(splashActivity);
//        }
        
        /***************************************/
        out: for (Element activity : activitys)
        {
           String name = activity.attributeValue("name"); 
           if("com.vas.vassdk.VasSdkSplashActivity".equals(name)){
               splashActivity = activity.createCopy();
               break out;
           }
        }
        
        if (splashActivity != null)
        {
            Element intentFilter = splashActivity.addElement("intent-filter");
            if(intentFilter !=null){
                intentFilter.clearContent();
                intentFilter.add(main);
                intentFilter.add(launcher);
                application.add(splashActivity);
            }
        }
        /***************************************/

        XMLWriter writer = new XMLWriter(new FileWriter(new File(xmlPath)), xmlFormat);
        writer.write(document);
        writer.close();

//        String smaliPath = tempPath + File.separator + "smali";
//        // 如果使用quick打包
//        // String splashSmaliPath = smaliPath + File.separator + "com" +
//        // File.separator + "vas" + File.separator
//        // + "quicksdk" + File.separator + "SplashActivity.smali";
//        // 如果是VasSDK单接
//        String splashSmaliPath = smaliPath + File.separator + "com" + File.separator + "vas" + File.separator
//                + "vassdk" + File.separator + "VasSdkSplashActivity.smali";
//        File splashSmali = new File(splashSmaliPath);
//        //替换闪屏VasSdkSplashActivity里面startactivity名称
//        FileUtil.replaceFileContent("\\{VasSDK_Game_Activity\\}", oldMainActivityName, splashSmali);
    }

    // 重命名包名
    private static String renamePackage(GameChannel gameChannel, String tempPath) throws Exception
    {
        String xmlPath = tempPath + File.separator + "AndroidManifest.xml";
        System.out.println("xmlPath = " + xmlPath);
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(new File(xmlPath));

        // 获得当前的包名
        Element root = document.getRootElement();
        String oldPackageVal = root.attribute("package").getValue(); // 旧包名
        if (gameChannel.getSuffix() == null)
        {
            return oldPackageVal;
        }

        System.out.println("oldPackageVal = " + oldPackageVal);
        String newPackageVal = "";

        if (gameChannel.getSuffix() == null)
        {// 打包的时候注意修改包名

        }
        else
        {
            String suffix = gameChannel.getSuffix();
            if (oldPackageVal.endsWith(".pptv"))
            {// 如果包名最后为.pptv
                if (suffix.equalsIgnoreCase(".pptv"))
                {
                    newPackageVal = oldPackageVal; // 新包名
                }
                else
                {
                    int lastIndexOf = oldPackageVal.lastIndexOf(".");
                    newPackageVal = oldPackageVal.substring(0, lastIndexOf) + suffix;
//                    newPackageVal = oldPackageVal + suffix;//直接拼，不截取
                }
            }
            else
            {
                newPackageVal = oldPackageVal + suffix; // 新包名
            }

        }

        File xml = new File(xmlPath);
        FileUtil.replaceFileContent(oldPackageVal, newPackageVal, xml);

        String smaliPath = tempPath + File.separator + "smali";
        String oldPath = oldPackageVal.replaceAll("\\.", "\\/");
        String newPath = newPackageVal.replaceAll("\\.", "\\/");

        if (!oldPath.equals(newPath))
        {
            // 修改目录名
            String oldSmaliPath = smaliPath + File.separator + oldPath;
            String newSmaliPath = smaliPath + File.separator + newPath;
            String tempSmaliPath = BASE_PATH + File.separator + "work" + File.separator + "tempSmali";
            FileUtil.copyDirectiory(oldSmaliPath, tempSmaliPath);
            FileUtil.delete(oldSmaliPath);
            FileUtil.copyDirectiory(tempSmaliPath, newSmaliPath);
            FileUtil.delete(tempSmaliPath);

            // 替换文件中的包名
            File smaliDir = new File(smaliPath);
            FileUtil.replaceDirectioryContent(oldPath, newPath, smaliDir);
        }

        System.out.println("newPackageVal = " + newPackageVal);

        return newPackageVal;
    }

    // 创建vassdk_config.xml
    private static void createVasSDKConfigXML(GameChannel gameChannel, Channel channel, String tempPath)
            throws Exception
    {
        Element rootElement = DocumentHelper.createElement("config");
        Document document = DocumentHelper.createDocument(rootElement);
        Map<String, String> channelParams = gameChannel.getChannelParams();
        if (channelParams != null)
        {
            for (String key : channelParams.keySet())
            {
                String value = channelParams.get(key);
                addParamElement(rootElement, key, value);
            }
        }

        addParamElement(rootElement, "VAS_CTYPE", gameChannel.getId());

        if (game.getGameId() != null)
        {
            addParamElement(rootElement, "VAS_GAMEID", game.getGameId());
        }

        if (game.getChannelId() != null)
        {
            addParamElement(rootElement, "VAS_CHANNELID", game.getChannelId());
        }

        if (game.getPlatpormId() != null)
        {
            addParamElement(rootElement, "VAS_PLATFORMID", game.getPlatpormId());
        }

        if (game.getSubPlatformId() != null)
        {
            addParamElement(rootElement, "VAS_SUBPLATFORMID", game.getSubPlatformId());
        }

        if (game.getSubChannelId() != null)
        {
            addParamElement(rootElement, "VAS_SUBCHANNEL_ID", game.getSubChannelId());
        }

        if (game.getUrl() != null)
        {
            addParamElement(rootElement, "AK_URL", game.getUrl());
        }

        if (game.getStatisticUrl() != null)
        {
            addParamElement(rootElement, "AK_STATISTIC_URL", game.getStatisticUrl());
        }

//        if (game.getGameMainName() != null)
//        {
            // addParamElement(rootElement, "MAIN_ACTIVITY_NAME",
            // game.getGameMainName());
            addParamElement(rootElement, "MAIN_ACTIVITY_NAME", mainActivityName);
//        }

        if (game.getDebug() != null)
        {
            addParamElement(rootElement, "VAS_DEBUG", game.getDebug());
        }

        // 是否竖屏
        if (game.getOrientation() != null && "PORTRAIT".equals(game.getOrientation().toUpperCase()))
        {
            addParamElement(rootElement, "ORIENTATION", "PORTRAIT");
        }

        List<String> applications = channel.getApplications();
        if (applications != null)
        {
            Element applicationsElement = DocumentHelper.createElement("applications");
            for (String application : applications)
            {
                Element applicationElement = DocumentHelper.createElement("application");
                applicationElement.addAttribute("name", application);
                applicationsElement.add(applicationElement);
            }
            rootElement.add(applicationsElement);
        }

        Map<String, String> plugins = channel.getPlugins();
        if (plugins != null)
        {
            Element pluginsElement = DocumentHelper.createElement("plugins");
            for (String key : plugins.keySet())
            {
                Element pluginElement = DocumentHelper.createElement("plugin");
                pluginElement.addAttribute("name", plugins.get(key));
                pluginElement.addAttribute("type", key);
                pluginsElement.add(pluginElement);
            }
            rootElement.add(pluginsElement);
        }

        // 另一种输出方式，记得要调用flush()方法,否则输出的文件中显示空白
        String assetsPath = tempPath + File.separator + "assets";
        File assets = new File(assetsPath);
        if (!assets.exists())
        {
            assets.mkdir();
        }

        String configNomalXmlPath = assetsPath + File.separator + "vassdk_config_nomal.xml";
        XMLWriter xmlWriter = new XMLWriter(new FileWriter(configNomalXmlPath), xmlFormat);
        xmlWriter.write(document);
        xmlWriter.flush();
        // 关闭流
        xmlWriter.close();

        createEncryptConfigXml(assetsPath, configNomalXmlPath);
    }

    /**
     * 将vassdk_config.xml进行加密
     */
    private static void createEncryptConfigXml(String assetsPath, String configNomalXmlPath)
    {
        String configXmlPath = assetsPath + File.separator + "vassdk_config.xml";
        KeyGenerator kg = null;
        FileOutputStream f = null;
        ObjectOutputStream b = null;
        BufferedInputStream in = null;
        CipherOutputStream out = null;
        try
        {
            kg = KeyGenerator.getInstance("DESede");
            // 指定密钥长度,长度越高,加密强度越大
            kg.init(168);
            // 产生密钥
            Key key = kg.generateKey();
            // 将key序列化到程序assets目录下
            f = new FileOutputStream(assetsPath + File.separator + "vas.dat");
            b = new ObjectOutputStream(f);
            b.writeObject(key);

            // 加密要用Cipher来实现
            Cipher cipher = Cipher.getInstance("DESede");
            // 输入流
            cipher.init(Cipher.ENCRYPT_MODE, key);
            in = new BufferedInputStream(new FileInputStream(configNomalXmlPath));// 读取nomal文件进行加密
            // 输出流
            out = new CipherOutputStream(new BufferedOutputStream(new FileOutputStream(configXmlPath)), cipher);
            int i;
            do
            {
                i = in.read();
                if (i != -1)
                    out.write(i);
            }
            while (i != -1);
        }
        catch (Exception e)
        {
            System.out.println("创建加密vassdk_config.xml失败");
        }
        finally
        {
            try
            {
                if (f != null)
                {
                    f.close();
                    f = null;
                }
                if (b != null)
                {
                    b.close();
                    b = null;
                }
                if (in != null)
                {
                    in.close();
                    in = null;
                }
                if (out != null)
                {
                    out.close();
                    out = null;
                }

                deleteVasConfigNomal(configNomalXmlPath);

            }
            catch (Exception e2)
            {
                e2.printStackTrace();
            }

        }

    }

    private static void deleteVasConfigNomal(String xmlPath)
    {
        // 加密完成删除vassdk_config_nomal.xml
        File configNomalFile = new File(xmlPath);
        if (configNomalFile.exists())
        {
            System.out.println("路径：" + xmlPath);
            System.out.println("configNomalFile 存在");
            boolean delete = configNomalFile.delete();
            if (!delete)
            {
                System.gc();
                configNomalFile.delete();
            }
            System.out.println("configNomalFile delete : " + delete);
        }
    }

    private static void addParamElement(Element rootElement, String name, String value)
    {
        Element mapElement = DocumentHelper.createElement("param");
        mapElement.addAttribute("name", name);
        mapElement.addAttribute("value", value);
        rootElement.add(mapElement);
    }

    // 键盘输入
    public static String keybordIn(String msg)
    {
        System.out.print(msg);
        Scanner sc = new Scanner(System.in);
        return sc.next();
    }

    // 执行命令
    private static void exeCmd(String command, File dir) throws Exception
    {
        showLog(Runtime.getRuntime().exec(command, null, dir));
    }

    private static void showLog(Process process) throws Exception
    {
        StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR");
        errorGobbler.start();
        StreamGobbler outGobbler = new StreamGobbler(process.getInputStream(), "STDOUT");
        outGobbler.start();
        process.waitFor();
    }

    private static String renameInstallLocation(GameChannel gameChannel, String tempPath) throws Exception
    {

        String xmlPath = tempPath + File.separator + "AndroidManifest.xml";
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(new File(xmlPath));

        // 获得installLocation
        Element root = document.getRootElement();
        String oldInstallLocation = root.attribute("installLocation").getValue(); // 旧installLocation

        System.out.println("oldInstallLocation = " + oldInstallLocation);

        String newInstallLocation = "auto";// 新installLocation

        File xml = new File(xmlPath);
        FileUtil.replaceFileContent(oldInstallLocation, newInstallLocation, xml);

        String smaliPath = tempPath + File.separator + "smali";
        String oldPath = oldInstallLocation.replaceAll("\\.", "\\/");
        String newPath = newInstallLocation.replaceAll("\\.", "\\/");

        if (!oldPath.equals(newPath))
        {
            // 修改目录名
            String oldSmaliPath = smaliPath + File.separator + oldPath;
            String newSmaliPath = smaliPath + File.separator + newPath;
            String tempSmaliPath = BASE_PATH + File.separator + "work" + File.separator + "tempSmali";
            FileUtil.copyDirectiory(oldSmaliPath, tempSmaliPath);
            FileUtil.delete(oldSmaliPath);
            FileUtil.copyDirectiory(tempSmaliPath, newSmaliPath);
            FileUtil.delete(tempSmaliPath);

            // 替换文件中的包名
            File smaliDir = new File(smaliPath);
            FileUtil.replaceDirectioryContent(oldPath, newPath, smaliDir);
        }

        System.out.println("newInstallLocation = " + newInstallLocation);

        return newInstallLocation;
    }

    private static String renameMainActiviyLunchModle(GameChannel gameChannel, String tempPath) throws Exception
    {

        String newMainActiviyLunchModle = "standard";
        String oldMainActiviyLunchModle = "";

        String xmlPath = tempPath + File.separator + "AndroidManifest.xml";
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(new File(xmlPath));
        Element root = document.getRootElement();
        Element application = root.element("application");
        List<Element> activitys = application.elements("activity");

        out: for (Element activity : activitys)
        {// 遍历activity得到游戏的主Activity
            String activityName = activity.attribute("name").getValue();// 获取activity的名称
            if (activityName.equalsIgnoreCase("ssjt.xiaohuoban.company.com.startgame.MainActivity"))
            {
                oldMainActiviyLunchModle = activity.attribute("launchMode").getValue();
                System.out.println("oldMainActiviyLunchModle = " + oldMainActiviyLunchModle);
                break out;
            }
        }

        File xml = new File(xmlPath);
        FileUtil.replaceFileContent(oldMainActiviyLunchModle, newMainActiviyLunchModle, xml);

        String smaliPath = tempPath + File.separator + "smali";
        String oldPath = oldMainActiviyLunchModle.replaceAll("\\.", "\\/");
        String newPath = newMainActiviyLunchModle.replaceAll("\\.", "\\/");

        if (!oldPath.equals(newPath))
        {
            // 修改目录名
            String oldSmaliPath = smaliPath + File.separator + oldPath;
            String newSmaliPath = smaliPath + File.separator + newPath;
            String tempSmaliPath = BASE_PATH + File.separator + "work" + File.separator + "tempSmali";
            FileUtil.copyDirectiory(oldSmaliPath, tempSmaliPath);
            FileUtil.delete(oldSmaliPath);
            FileUtil.copyDirectiory(tempSmaliPath, newSmaliPath);
            FileUtil.delete(tempSmaliPath);

            // 替换文件中的包名
            File smaliDir = new File(smaliPath);
            FileUtil.replaceDirectioryContent(oldPath, newPath, smaliDir);
        }

        System.out.println("newMainActiviyLunchModle = " + newMainActiviyLunchModle);

        return newMainActiviyLunchModle;
    }

}
