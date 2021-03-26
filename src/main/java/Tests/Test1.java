package Tests;

import OS.OrtOS;
import OS.UserOsAPI;
import Tasks.Task;
import Tasks.TaskPayload;

public class Test1 {

    OrtOS ortOs;
    TaskA1 taskA1 = new TaskA1();
    TaskA2 taskA2 = new TaskA2();
    TaskA3 taskA3 = new TaskA3();
    TaskA4 taskA4 = new TaskA4();
    TaskA5 taskA5 = new TaskA5();

    public Test1(OrtOS ortOS)
    {
        this.ortOs = ortOS;
    }

    public void start()
    {
        ortOs.startOS(taskA1);
    }

    static int gTask1Count = 0;

    static MyLogger logger = new MyLogger(Test1.class);

    public class TaskA1 extends Task
    {
        public TaskA1() {
            super(1, 1, null);
            this.payload = new TaskPayload(this) {
                boolean done = false;
                @Override
                public void run() {
                    done = false;
                    logger.STask(1);
                    if (gTask1Count == 0) {
                        gTask1Count = 1;
                        logger.ATask(2);
                        ortOs.activateTask(taskA2);
                    } else {
                        gTask1Count = 0;
                    }
                    logger.ETask(1);
                    done = true;
                    ortOs.terminateTask();
                }

                public boolean done() {
                    return done;
                }
            };
        }
    };

    public class TaskA2 extends Task// 4)
    {
        public TaskA2() {
            super(2, 1 ,null);
            this.payload = new TaskPayload(this)
            {
                boolean done = false;
                @Override
        public void run() {
                    done = false;
            logger.STask(2);
            logger.ATask(4);
            ortOs.activateTask(taskA4);
            logger.ATask(1);
            ortOs.activateTask(taskA1);
            logger.ATask(5);
            ortOs.activateTask(taskA5);
            logger.ETask(2);
            done = true;
            ortOs.terminateTask();
        }
                public boolean done() {
                    return done;
                }
            };
        }
    };

    public class TaskA3 extends Task// 4)
    {
        public TaskA3() {
            super(3, 2, null);
            this.payload = new TaskPayload(this) {
                boolean done = false;
                @Override
                public void run() {
                    done = false;
                    logger.STask(3);
                    logger.ETask(3);
                    done = true;
                    ortOs.terminateTask();
                }

                public boolean done() {
                    return done;
                }
            };
        }
    };

    public class TaskA4 extends Task// 4)
    {
        public TaskA4() {
            super(4, 4, null);

            this.payload = new TaskPayload(this) {
                boolean done = false;
                @Override
                public void run() {
                    done = false;
                    logger.STask(4);
                    logger.ATask(3);
                    ortOs.activateTask(taskA3);
                    logger.ETask(4);
                    done = true;
                    ortOs.terminateTask();
                }

                public boolean done() {
                    return done;
                }
            };
        }
    };

    public class TaskA5 extends Task// 5)
    {
        public TaskA5() {
            super(5, 5, null);

            this.payload = new TaskPayload(this) {
                boolean done = false;
                @Override
                public void run() {
                    done = false;
                    logger.STask(5);
                    logger.ATask(4);
                    ortOs.activateTask(taskA4);
                    ortOs.activateTask(taskA4);
                    logger.ETask(5);
                    done = true;
                    ortOs.terminateTask();
                }

                public boolean done() {
                    return done;
                }
            };
        }
    }
}
