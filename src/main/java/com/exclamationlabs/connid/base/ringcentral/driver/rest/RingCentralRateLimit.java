package com.exclamationlabs.connid.base.ringcentral.driver.rest;

import org.apache.commons.codec.binary.StringUtils;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.time.Instant;
import java.time.Duration;
import java.util.LinkedList;

public class RingCentralRateLimit {
    private LinkedList<Instant> queue = new LinkedList();
    private String remaining;

    protected void enqueue(long expiration) {
        clear();
        checkRemaining();
        queue.add(Instant.now().plusSeconds(expiration));
    }

    protected void sleep() throws InterruptedException {
        if(queue.size() == 0){
            Thread.sleep(60000);
        }
        while(queue.size() > 0 && !Duration.between(Instant.now(),queue.peek()).isNegative()){
            Thread.sleep(2000);
        }
        queue.remove();
    }
    private void clear() {
        while(queue.size() > 0 && Duration.between(Instant.now(),queue.peek()).isNegative()){
            queue.remove();
        }
    }

    public void setRemaining(String remaining) {
        this.remaining = remaining;
    }

    protected void checkRemaining(){
        if(StringUtils.equals(remaining,"0")) {
            try {
                sleep();
            } catch (InterruptedException e) {
                throw new ConnectorException(e);
            }
        }
    }
}
