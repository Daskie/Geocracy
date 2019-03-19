package csc_cccix.geocracy;

import java.util.LinkedList;
import java.util.Queue;

import csc_cccix.geocracy.backend.Message;

public final class FrontToBack {

    public static synchronized void send(Message msg) {
        msgQueue.add(msg);
    }

    public static synchronized Message receive() {
        return msgQueue.poll();
    }

    private static Queue<Message> msgQueue = new LinkedList<>();

}
