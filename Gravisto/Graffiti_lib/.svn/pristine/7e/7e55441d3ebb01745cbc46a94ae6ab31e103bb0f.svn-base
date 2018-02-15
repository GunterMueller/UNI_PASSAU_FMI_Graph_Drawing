// =============================================================================
//
//   StackQueueWorker.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.stackqueuelayout;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class StackQueueWorker extends Thread {
    private volatile boolean wantsStop;
    private AtomicReference<List<StackQueueJob>> nextJobs;

    public StackQueueWorker() {
        wantsStop = false;
        nextJobs = new AtomicReference<List<StackQueueJob>>();
    }

    public void setNextJobs(List<StackQueueJob> nextJobs) {
        this.nextJobs.set(nextJobs);
    }

    @Override
    public void run() {
        do {
            try {
                List<StackQueueJob> jobs = nextJobs.getAndSet(null);
                if (jobs != null) {
                    for (StackQueueJob job : jobs) {
                        job.execute();
                    }
                }
                sleep(1000000);
            } catch (InterruptedException e) {
            }
        } while (!wantsStop);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
