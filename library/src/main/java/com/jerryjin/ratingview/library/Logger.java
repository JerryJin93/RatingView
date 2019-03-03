package com.jerryjin.ratingview.library;

import android.support.annotation.IntDef;
import android.util.Log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Author: Jerry
 * Generated at: 2019/2/16 17:34
 * WeChat: enGrave93
 * Description: A convenient way to write logs.
 */
@SuppressWarnings("WeakerAccess")
public class Logger {

    public static final int TYPE_DEBUG = 0;
    public static final int TYPE_ERROR = 1;
    public static final int TYPE_INFO = 2;
    public static final int TYPE_VERBOSE = 3;
    public static final int TYPE_WARN = 4;
    public static final int TYPE_WHAT_A_TERRIBLE_FAILURE = 5;
    private static final String VOID_STRING = "";
    private static final String SPACE = " ";
    private static Map<String, Set<ILogger.Slave>> map = new HashMap<>();
    private static Set<ILogger.Slave> slaves = new HashSet<>();
    private String tag;

    private Logger() {
    }

    public static void registerObservable(ILogger.Slave slave) {
        slaves.add(slave);
    }

    public static void registerObservable(ILogger.Slave slave, String category) {
        Set<ILogger.Slave> slaves = map.get(category);
        if (slaves != null && slaves.size() > 0) {
            slaves.add(slave);
        } else {
            slaves = new HashSet<>();
            slaves.add(slave);
            map.put(category, slaves);
        }
    }

    public static void unregisterObservable(ILogger.Slave slave) {
        slaves.remove(slave);
    }

    public static void unregisterObservable(ILogger.Slave slave, String category) {
        Set<ILogger.Slave> slaves = map.get(category);
        if (slaves != null && slaves.size() > 0) {
            slaves.remove(slave);
            if (slaves.size() == 0) {
                slaves = null;
                map.remove(category);
                if (map.size() == 0) {
                    map = null;
                }
            }
        }
    }

    public static Logger create() {
        return LoggerHolder.instance;
    }

    public static Logger create(Class<?> cls) {
        Logger logger = LoggerHolder.instance;
        logger.setTag(cls.getCanonicalName());
        return logger;
    }

    public static Logger create(String tag) {
        Logger logger = LoggerHolder.instance;
        logger.setTag(tag);
        return logger;
    }

    public static void print(@LogType int type, String tag, String... messages) {
        print(type, tag, getPrintContent(messages), null);
    }

    public static void print(@LogType int type, String tag, Throwable tr, String... messages) {
        print(type, tag, getPrintContent(messages), tr);
    }

    private static String getPrintContent(String... messages) {
        String toPrint;
        if (messages != null && messages.length > 0) {
            int len = messages.length;
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < len; i++) {
                builder.append(messages[i]);
                if (i != len - 1) {
                    builder.append(SPACE);
                }
            }
            toPrint = builder.toString();

        } else {
            toPrint = VOID_STRING;
        }
        return toPrint;
    }

    private static void print(@LogType int type, String tag, String toPrint, Throwable tr) {
        switch (type) {
            case TYPE_DEBUG:
                if (tr != null) {
                    Log.d(tag, toPrint, tr);
                } else {
                    Log.d(tag, toPrint);
                }
                break;
            case TYPE_ERROR:
                if (tr != null) {
                    Log.e(tag, toPrint, tr);
                } else {
                    Log.e(tag, toPrint);
                }
                break;
            case TYPE_INFO:
                if (tr != null) {
                    Log.i(tag, toPrint, tr);
                } else {
                    Log.i(tag, toPrint);
                }
                break;
            case TYPE_VERBOSE:
                if (tr != null) {
                    Log.v(tag, toPrint, tr);
                } else {
                    Log.v(tag, toPrint);
                }

                break;
            case TYPE_WARN:
                if (tr != null) {
                    Log.w(tag, toPrint, tr);
                } else {
                    Log.w(tag, toPrint);
                }
                break;
            case TYPE_WHAT_A_TERRIBLE_FAILURE:
                if (tr != null) {
                    Log.wtf(tag, toPrint, tr);
                } else {
                    Log.wtf(tag, toPrint);
                }
                break;
            default:
                if (tr != null) {
                    Log.e(tag, toPrint, tr);
                } else {
                    Log.e(tag, toPrint);
                }
                break;
        }
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
    @IntDef({TYPE_DEBUG, TYPE_ERROR, TYPE_INFO, TYPE_VERBOSE, TYPE_WARN, TYPE_WHAT_A_TERRIBLE_FAILURE})
    public @interface LogType {
    }

    private static class LoggerHolder {
        private static final Logger instance = new Logger();
    }
}
