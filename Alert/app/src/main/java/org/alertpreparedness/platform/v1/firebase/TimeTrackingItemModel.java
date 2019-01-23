package org.alertpreparedness.platform.v1.firebase;

import java.io.Serializable;

/**
 * Created by Tj on 26/03/2018.
 */

public class TimeTrackingItemModel implements Serializable {
    private Long finish;

    private Long level;

    public TimeTrackingItemModel() {
    }

    public TimeTrackingItemModel(Long finish, Long start) {
        this.finish = finish;
        this.start = start;
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

    public Long getLevel() {
        return level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }
}
