package prv.simple.db.basic;

public class Util {

    /**
     * 获得所在线程的id
     * 
     * @return type {@code long}} Thread.currentThread().getId();
     */
    public static long getCurrentId() {
        return Thread.currentThread().getId();
    }

    /**
     * 
     * @param alias
     *            多链接的
     * @return
     */
    public static String getCurrentId(String alias) {
        StringBuilder sb = new StringBuilder(alias).append("-").append(getCurrentId());
        return sb.toString();
    }
}
