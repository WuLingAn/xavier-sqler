package prv.simple.db.basic;

import java.util.Map;

import org.junit.Test;

public class UtilTest {
    // 当前用户组中所有线程
    @Test
    public void allThread() {
        Thread t1 = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t1.setName("thread1");
        t1.start();
        ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();

        System.out.println(currentGroup.getName());
        int noThreads = currentGroup.activeCount();
        Thread[] lstThreads = new Thread[noThreads];
        currentGroup.enumerate(lstThreads);
        for (int i = 0; i < noThreads; i++) {
            System.out.println("线程号：" + i + " = " + lstThreads[i].getName() + ",id:" + lstThreads[i].getId());
        }
    }

    // JVM中线程
    @Test
    public void JVMAllThread() {
        Thread t1 = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t1.setName("thread1");
        t1.start();
        for (Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
            Thread thread = entry.getKey();

            StackTraceElement[] stackTraceElements = entry.getValue();

            if (thread.equals(Thread.currentThread())) {
                continue;
            }

            System.out.println("线程： " + thread.getName() + ",id:" + thread.getId() + "\n");
            for (StackTraceElement element : stackTraceElements) {
                System.out.println("\t" + element + "\n");
            }
            System.out.println("************");
        }
    }
}
