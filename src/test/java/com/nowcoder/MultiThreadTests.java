package com.nowcoder;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

class MyThread extends Thread {
    private int tid;
    public MyThread(int tid) {
        this.tid = tid;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                Thread.sleep(1000);
                System.out.println(String.format("tid=%d:i=%d", tid, i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Consumer implements Runnable {
    private BlockingQueue<String> q;

    public Consumer(BlockingQueue<String> q) {
        this.q = q;
    }

    @Override
    public void run() {
        try {
            while(true){
                // keep taking from q
                System.out.println(Thread.currentThread().getName() + ":" + q.take());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Producer implements Runnable {
    private BlockingQueue<String> q;

    public Producer(BlockingQueue<String> q) {
        this.q = q;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 100; i++) {
                Thread.sleep(1000);
                q.put(String.valueOf(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class MultiThreadTests {

    // thread base
    private static void testThread() {
//        for (int i = 0; i < 5; i++) {
//            new MyThread(i).start();
//        }

        for (int i = 0; i < 5; i++) {
            // saoqi
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int j = 0; j < 10; j++) {
                            Thread.sleep(1000);
                            System.out.println(String.format(" finalI=%d:j=%d", finalI, j));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    // thread locker : synchronized
    private static Object obj = new Object();
    // if put on the method, than synchronized can lock the method
    // synchronized(obj) can lock related code block (one thread can run these codes)
    private static void testSynchronized1() {
        // only one thread can visit obj at one time
        synchronized (obj) {
            try {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(1000);
                    System.out.println(String.format("T3 i=%d", i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private static void testSynchronized2() {
        // only one thread can visit obj at one time
        synchronized (obj) {
            try {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(1000);
                    System.out.println(String.format("T4 i=%d", i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private static void testSynchronized() {
        // T3 and T4 are competing for the threadpool, so only one thread can run
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    testSynchronized1();
                    testSynchronized2();
                }
            }).start();
        }
    }

    // BlockingQueue
    private static void testBlockingQueue() {
        // two consumer keep consuming the thread produced and put by producer in the blockingQueue
        BlockingQueue<String> q = new ArrayBlockingQueue<>(10);
        new Thread(new Producer(q)).start();
        new Thread(new Consumer(q), "Consumer1").start();
        new Thread(new Consumer(q), "Consumer2").start();
    }

    // ThreadLocal
    private static int userID;
    private static ThreadLocal<Integer> threadLocalUserIds = new ThreadLocal<>();
    private static void testThreadLocal() {
        for (int i = 0; i < 10; i++) {
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        threadLocalUserIds.set(finalI);
                        Thread.sleep(1000);
                        System.out.println("ThreadLocal:" + threadLocalUserIds.get());
                        // all the output are distinct
                        // as different thread own one userID;
                        // like a map
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
        for (int i = 0; i < 10; i++) {
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        userID = finalI;
                        Thread.sleep(1000);
                        System.out.println("ThreadLocal:" + userID);
                        // all the output are the same
                        // as the userID are set by the Thread_9
                        // all the thread share one field
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }

    // thread pool
    private static void testSingleExecutor() {
        // single thread
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 10; i++) {
                        Thread.sleep(1000);
                        System.out.println(String.format("Executor1:%d", i));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        // print this
        service.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 10; i++) {
                        Thread.sleep(1000);
                        System.out.println(String.format("Executor1:%d", i));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        // then print this
    }
    private static void testMultiExecutor() {
        // single thread
        ExecutorService service = Executors.newFixedThreadPool(3);
        service.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 10; i++) {
                        Thread.sleep(1000);
                        System.out.println(String.format("Executor1:%d", i));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        service.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 10; i++) {
                        Thread.sleep(1000);
                        System.out.println(String.format("Executor1:%d", i));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        // print parallerly
        service.shutdown();
        while(!service.isTerminated()) {
            try {
                Thread.sleep(20);
                System.out.println("wait for terminated");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // thread-safety
    private static int counter = 0;
    private static AtomicInteger atomicInteger = new AtomicInteger(10);
    private static void testWithoutAtomic() {
        for (int i = 0; i < 5; i++) {
            // saoqi
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        for (int j = 0; j < 10; j++) {
                            counter++;
                            System.out.println(counter);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
    private static void testWithAtomic() {
        for (int i = 0; i < 5; i++) {
            // saoqi
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        for (int j = 0; j < 10; j++) {
                            System.out.println(atomicInteger.incrementAndGet());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    // future
    // use to communicate with thread pool extractor
    // can get return result, can wait for blocking, set timeout for wait, get Exception from thread
    private static void testFuture() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Integer> future = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Thread.sleep(2000);
                return 1;
            }
        });
        service.shutdown();

        try {
            // .get() : wait until finish, then return
//            System.out.println(future.get());
            System.out.println(future.get(100, TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
//        testSynchronized();
//        testBlockingQueue();
//        testThreadLocal();
//        testSingleExecutor();
//        testMultiExecutor();
//        testWithoutAtomic();
//        testWithAtomic();
        testFuture();
    }



}
