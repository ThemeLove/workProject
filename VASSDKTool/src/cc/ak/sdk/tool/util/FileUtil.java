package cc.ak.sdk.tool.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileUtil {
	
	/**
	 * 替换文件内容
	 * @param oldWord
	 * @param newWord
	 * @param dir
	 * @throws Exception
	 */
	public static void replaceDirectioryContent(String oldWord, String newWord, File dir) throws Exception {
		for(File file : dir.listFiles()) {
			if(file.isDirectory()) {
				replaceDirectioryContent(oldWord, newWord, file);
			} else {
				replaceFileContent(oldWord, newWord, file);
			}
		}
	}
	
	public static void replaceFileContent(String oldWord, String newWord, File file) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String temp = reader.readLine();
		StringBuffer buffer = new StringBuffer();
		while(temp != null) {
			temp = temp.replaceAll(oldWord, newWord);
			buffer.append(temp);
			buffer.append("\r\n");
			temp = reader.readLine();
		}
		reader.close();
		
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		writer.write(buffer.toString().toCharArray());
		writer.flush();
		writer.close();
	}
	
	/**
	 * 删除文件
	 */
	public static void delete(String path) {
		File file = new File(path);
		if(!file.exists()) {
			return;
		}
		
		if(file.isDirectory()) {
			File[] files = file.listFiles();
			for(File f : files) {
				delete(f.getPath());
			}
		}
		file.delete();
	}
	
	/**
	 * 复制目录
	 * @param sourceDir
	 * @param targetDir
	 */
	public static void copyDirectiory(String sourceDir, String targetDir) throws Exception {
		File source = new File(sourceDir);
		if(!source.exists()) {
			return;
		}
		
		//新建目标目录
        (new File(targetDir)).mkdirs();
        //获取源文件夹当下的文件或目录
        File[] file = source.listFiles();
    	for(int i=0; i < file.length; i++) {
            if(file[i].isFile()){
                //源文件
                File sourceFile=file[i];
                //目标文件
                File targetFile = new File(new File(targetDir).getAbsolutePath()+File.separator+file[i].getName());
                copyFile(sourceFile, targetFile);
            }
            
            if(file[i].isDirectory()){
                //准备复制的源文件夹
                String dir1 = sourceDir + File.separator + file[i].getName();
                //准备复制的目标文件夹
                String dir2 = targetDir + File.separator + file[i].getName();
                copyDirectiory(dir1, dir2);
            }
        }
    }
	
	/**
	 * 复制文件
	 * @param sourcefile
	 * @param targetFile
	 * @throws IOException
	 */
	public static void copyFile(File sourcefile,File targetFile) throws IOException{
        //新建文件输入流并对它进行缓冲
        FileInputStream input = new FileInputStream(sourcefile);
        BufferedInputStream inbuff = new BufferedInputStream(input);
        
        //新建文件输出流并对它进行缓冲
        FileOutputStream out = new FileOutputStream(targetFile);
        BufferedOutputStream outbuff = new BufferedOutputStream(out);
        
        //缓冲数组
        byte[] b=new byte[1024*5];
        int len=0;
        while((len=inbuff.read(b))!=-1){
            outbuff.write(b, 0, len);
        }
        
        //刷新此缓冲的输出流
        outbuff.flush();
        
        //关闭流
        inbuff.close();
        outbuff.close();
        out.close();
        input.close();
    }
	
}
