package com.soonsoft.uranus.core.common.event;

import java.util.LinkedList;
import java.util.function.Consumer;

/**
 * AsyncSimpleEventListener
 */
public class AsyncSimpleEventListener<E> extends SimpleEventListener<E> {

    public AsyncSimpleEventListener() {

    }

    public AsyncSimpleEventListener(String name) {
        super(name);
    }

    @Override
    public void trigger(E event) {
         LinkedList<Consumer<E>> handlerList = getHandlerList();
         if(handlerList != null) {
            TriggerTask<E> task = new TriggerTask<>(handlerList, event);
            // TODO 将task放入线程池 
         }
    }

    @Override
    protected LinkedList<Consumer<E>> getHandlerList() {
        LinkedList<Consumer<E>> handlerList = super.getHandlerList();
        if(handlerList == null || handlerList.isEmpty()) {
            return null;
        }
        LinkedList<Consumer<E>> copyHandlerList = new LinkedList<>();
        copyHandlerList.addAll(handlerList);
        return copyHandlerList;
    }
    
    static final class TriggerTask<E> implements Runnable {
        private LinkedList<Consumer<E>> handlerList;
        private E event;

        private TriggerTask(LinkedList<Consumer<E>> handlerList, E event) {
            this.handlerList = handlerList;
            this.event = event;
        }

        @Override
        public void run() {
            for(Consumer<E> handler : handlerList) {
                handler.accept(event);
            }
        }
    }
}