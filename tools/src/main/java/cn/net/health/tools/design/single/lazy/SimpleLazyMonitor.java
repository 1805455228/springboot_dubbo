package cn.net.health.tools.design.single.lazy;

/**
 * 1.  最简单的懒汉单例模式
 * 2.  将构造方法私有化，这样是防止在其他地方被实例化，就出现多个班长对象了
 * 3.  最后给外界提供一个方法，返回这个班长对象即可
 * 4. 不能保证线程安全问题
 */
public class SimpleLazyMonitor {

    private static SimpleLazyMonitor monitor = null;

    private SimpleLazyMonitor() {
    }

    public static SimpleLazyMonitor getMonitor() {
        //如果两个线程同时到了判断null的地步，就会打破单例模式
        if (monitor == null) {
            monitor = new SimpleLazyMonitor();
        }
        return monitor;
    }

    public static void main(String[] args) {
        new Thread(new ExecutorThread()).start();
        new Thread(new ExecutorThread()).start();
        System.out.println("end");
    }

}