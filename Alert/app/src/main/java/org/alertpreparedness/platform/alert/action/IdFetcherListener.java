package org.alertpreparedness.platform.alert.action;

import java.util.List;

/**
 * Created by Tj on 01/03/2018.
 */

public interface IdFetcherListener {
    void onIdResult(List<String> ids);
}