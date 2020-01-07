package org.aidework.rpc.core.helper;

/**
 * 简单日志辅助工具
 */
public class SystemLogger {


    private static class LogHelperHolder{
        private static final SystemLogger instance=new SystemLogger();
    }

    private SystemLogger(){
    }
    public static void logNormal(String log)
    {
        System.out.println("[INFO] "+log);
    }
    public static void logError(String log){
        System.err.println("[ERROR] "+log);
    }
    public static void logWarning(String log){
        System.out.println("[WARNING] "+log);
    }

//    /**
//     * 建造器模式生成实例对象
//     * 用于控制对象实例化过程
//     * @param target
//     * @return
//     */
//    public static LogHelper builde(LogTarget target){
//        return new LogHelper(target);
//    }

    /**
     * 日志打印目标
     */
//    public enum LogTarget{
//        SYSTEM,// 系统控制台
//        FILE,// 文件
//        NETWORK,// 网络
//        DATABASE,// 数据库
//    }

    /**
     * 日志打印类
     */
//    private class LogPrinter{
//        private PrintStream printer;
//        public LogPrinter(LogHelper.LogTarget target){
//            switch (target){
//                case SYSTEM:
//                case FILE:
//                case NETWORK:
//                case DATABASE:
//                    printer=System.out;
//                    break;
//            }
//        }
//        public void print(String str){
//            printer.println(str);
//        }
//    }
}


