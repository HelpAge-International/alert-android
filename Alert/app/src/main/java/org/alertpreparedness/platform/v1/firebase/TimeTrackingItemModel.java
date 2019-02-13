package org.alertpreparedness.platform.v1.firebase;

import java.io.Serializable;
import org.alertpreparedness.platform.v2.models.TimeTrackingItem;

/**
 * Created by Tj on 26/03/2018.
 */

public class TimeTrackingItemModel implements Serializable {

    private Long finish;

    private Long start;

    public TimeTrackingItemModel() {
    }

    public TimeTrackingItemModel(Long finish, Long start) {
        this.finish = finish;
        this.start = start;
    }


    public TimeTrackingItemModel(final TimeTrackingItem timeTrackingItem) {
        this.finish = timeTrackingItem.getFinish().getMillis();
        this.start = timeTrackingItem.getStart().getMillis();
    }

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
