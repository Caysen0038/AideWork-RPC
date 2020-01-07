package org.aidework.rpc.core.helper;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class ReflectionHelper {
    private static String[] CLASS_PATH_PROP={
            "java.class.path",
            "java.ext.dirs",
            "sun.boot.class.path"};

    public static List<String> scanClassInPackage(String pkgName)   {
        List<String> list=new ArrayList<String>();
        String realPath=pkgName.replace(".",File.separator) +File.separator;
        try {
            List<File> classPathList=getClassPath();
            for(File classPath:classPathList)   {
                if(!classPath.exists()){
                    continue;
                }
                if(classPath.isDirectory()){
                    File dir=new File(classPath,realPath);
                    if(!dir.exists()){
                        continue;
                    }
                    scanDir(dir,list,classPath.getPath());
                }else{
                    FileInputStream fis=new FileInputStream(classPath);
                    JarInputStream jis=new JarInputStream(fis,false);
                    JarEntry e=null;
                    while((e=jis.getNextJarEntry())!=null)   {
                        String eName=e.getName();
                        if(eName.startsWith(realPath)&&!eName.endsWith("/"))   {
                            list.add(eName.replace('/','.')
                                    .substring(0,eName.length()-6));
                        }
                        jis.closeEntry();
                    }
                    jis.close();
                }
            }
        }catch(Exception e)   {
            throw new RuntimeException(e);
        }
        return list;
    }
    private static void scanDir(File dir,List<String> list,String classPath){
        for(File file:dir.listFiles())   {
            if(file.isFile()){
                String clsName=file.getPath();
                clsName=clsName.substring(0,clsName.length()-6);
                list.add(clsName.replace(classPath+File.separator,"")
                        .replace(File.separator,"."));
            }else{
                scanDir(file,list,classPath);
            }
        }

    }
    private static List<File> getClassPath(){
        List<File> ret=new ArrayList<File>();
        String delim=":";
        if(System.getProperty("os.name").indexOf("Windows")!=-1)
            delim=";";
        for(String pro:CLASS_PATH_PROP)   {
            String[] pathes=System.getProperty(pro).split(delim);
            for(String path:pathes)
                ret.add(new File(path));
        }
        return ret;
    }
}
