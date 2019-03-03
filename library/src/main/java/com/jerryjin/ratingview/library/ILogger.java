package com.jerryjin.ratingview.library;

/**
 * Author: Jerry
 * Generated at: 2019/2/16 18:13
 * WeChat: enGrave93
 * Description:
 */
public interface ILogger {

    // Cancel Observer mode for Logger, eventually.
    interface Master {
        void update(String tag);
    }

    interface Slave {
        void notifyMasters();
    }
}
