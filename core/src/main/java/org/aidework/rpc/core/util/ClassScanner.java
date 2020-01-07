package org.aidework.rpc.core.util;

import org.aidework.rpc.core.helper.ReflectionHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Use java reflection to scan class info from current classloader.
 * ClassScanner'method all static,so scan method should be invoke one time normally.
 * All class info will be save as a list in class field,of course the filed is static.
 * There is a recommend to invoke the clear method after invoker sure the work has finished.
 */
public class ClassScanner {
    private static List<String> list;
    private static boolean scanned;

    /**
     * Constructor are not allowed
     */
    private ClassScanner(){}

    /**
     * To determine whether or not scanned class from current classloader
     * @return if has been scanned return true otherwise return false
     */
    public static boolean isScanned(){
        return scanned;
    }

    /**
     *  Scan class info from current classloader
     *  All class info will be put in list and save it until jvm be closed or clear method be invoked
     */
    public static void scan(){
        list= ReflectionHelper.scanClassInPackage("");
        scanned=true;
    }

    /**
     * Get class info what in the specified package
     * All class info will be put a list and return to invoker
     * @param pack package's name
     * @return a list containing class info if the package exists and package is not empty
     */
    public static List<String> getPackClassList(String pack){
        if(!isScanned()){
            scan();
        }
        List<String> newList=new ArrayList<>();
        for(String s:list){
            if(s.startsWith(pack)){
                newList.add(s);
            }
        }
        return newList;
    }

    /**
     * Get all class info as list
     * @return a list containing all class info
     */
    public static List<String> getClassList(){
        if(!isScanned()){
            scan();
        }
        return list;
    }

    /**
     * Clear the list containing all class info
     * List will be clear,set list reference is null and scanned is false
     */
    public static void clear(){
        list.clear();
        list=null;
        scanned=false;
    }
}
