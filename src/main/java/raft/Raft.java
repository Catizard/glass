package raft;

import org.jetbrains.annotations.NotNull;
import org.omg.CORBA.INTERNAL;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class Raft {
    private ReentrantLock mu;
    /* TODO
        peers: 集群中其他的服务器对象
        persister: 持久化对象

     */
    private int me;
    private volatile int dead;

    //everyone, persistent
    public int term;
    public int votedFor;
    public ArrayList<Object> log;//TODO: 为 entry 构造类型
    //everyone, volatile
    public int commitIndex;
    //Lasttick
    public int lastApplied;
    public int waitTime;
    public int deltaTime;
    public RAFT_ROLE role;
    //for leader, volatile
    public ArrayList<Integer> nextIndex;
    public ArrayList<Integer> matchIndex;
    //TODO applyCh: 传输消息的通道
    //for snapshot
    public int lastIncludedIndex;
    public int lastIncludedTerm;

    public boolean isLeader() {
        return role == RAFT_ROLE.LEADER;
    }

    public int getTerm() {
        return term;
    }

    public void kill() {
        dead = 1;
    }
    public boolean killed() {
        int z = dead;
        return z == 1;
    }

    private Raft() { }
    @NotNull
    public static Raft build(int me) {
        //TODO complete build function
        Raft rf = new Raft();
        rf.mu = new ReentrantLock();
        //peers & persister
        rf.me = me;

        rf.term = 0;
        //generate a seed
        rf.waitTime = 150;
        rf.waitTime += 300;
        rf.log = new ArrayList<Object>();

        //TODO 有了 peers 之后,nextIndex 和 matchIndex 初始化时就把大小初始好
        rf.nextIndex = new ArrayList<Integer>();
        rf.matchIndex = new ArrayList<Integer>();

        //TODO dummy entry
        rf.log.add(new Object());
        //rf.applyCh = ?

        //run a thread in order to start raft's life circle

        return rf;
    }

}
