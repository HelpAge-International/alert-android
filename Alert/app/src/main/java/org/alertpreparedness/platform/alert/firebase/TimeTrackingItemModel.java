package org.alertpreparedness.platform.alert.firebase;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Tj on 26/03/2018.
 */

public class TimeTrackingItemModel implements Serializable {
    private Long finish;

    public TimeTrackingItemModel() {
    }

    private Long start;

    public Long getFinish() {
        return finish;
    }

    public void setFinish(Long finish) {
        this.finish = finish;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }
}
