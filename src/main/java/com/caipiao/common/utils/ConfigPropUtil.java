package com.caipiao.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * tlwcloud
 *
 * @author : xiaoyinandan
 * @date : 2020/12/7 09:55
 */
@Slf4j
public class ConfigPropUtil {
    private static Properties config;
    private static Properties getConfig() {
        if (config == null) {
            config = new Properties();
            FileInputStream is = null;

            try {
                String PRO_CLASS_PATH = System.getProperty("PRO_CLASS_PATH");
                if (PRO_CLASS_PATH == null) {
                    URL url = ConfigPropUtil.class.getClassLoader().getResource("context.properties");
                    config.load(url.openStream());
                } else {
                    is = new FileInputStream(PRO_CLASS_PATH + File.separator + "context.properties");
                    config.load(is);
                }
            } catch (Exception var11) {
                log.error(var11.getMessage(), var11);
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException var10) {
                }

            }
        }

        return config;
    }
    public static String getProperty(String key) {
        return getValue(getConfig().getProperty(key));
    }

    public static String getDefProperty(String key, String key2) {
        String value = getProperty(key);
        if (value == null) {
            value = getProperty(key2);
        }

        return value;
    }

    public static String getProperty(String key, String defaultValue) {
        String value = getValue(getConfig().getProperty(key));
        return value != null && !"".equals(value.trim()) ? value : defaultValue;
    }

    public static boolean getBooleanProperty(String name) {
        return getBooleanProperty(name, false);
    }

    public static boolean getBooleanProperty(String name, boolean defaultValue) {
        String value = getValue(getConfig().getProperty(name));
        return value != null && !"".equals(value.trim()) ? Boolean.valueOf(value) : defaultValue;
    }

    public static String[] getStringsProperty(String name) {
        String str = getProperty(name);
        return StringUtils.isBlank(str) ? null : str.split("[,; ]");
    }

    public static List<String> getStrListProperty(String name) {
        String[] strs = getStringsProperty(name);
        if (strs == null) {
            return new ArrayList();
        } else {
            List<String> list = new ArrayList();
            String[] arr$ = strs;
            int len$ = strs.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                String str = arr$[i$];
                list.add(str);
            }

            return list;
        }
    }

    public static int getIntProperty(String name) {
        return getIntProperty(name, 0);
    }

    public static int getIntProperty(String name, String name2, int defaultValue) {
        String value1 = getProperty(name);
        return value1 == null ? getIntProperty(name2, defaultValue) : getIntProperty(name, defaultValue);
    }

    public static int getIntProperty(String name, int defaultValue) {
        String value = getValue(getConfig().getProperty(name));
        return value != null && !"".equals(value.trim()) ? Integer.parseInt(value) : defaultValue;
    }

    public static long getLongProperty(String name) {
        return getIntProperty(name, 0);
    }

    public static long getLongProperty(String name, long defaultValue) {
        String value = getValue(getConfig().getProperty(name));
        if (value != null && !"".equals(value.trim())) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException var5) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    private static String getValue(String value) {
        if (value != null && !"".equals(value.trim())) {
            Pattern pattern = Pattern.compile("\\$\\{([^\\}]+)\\}", 2);
            Matcher m = pattern.matcher(value);
            return m.matches() ? config.getProperty(m.group(1)) : value;
        } else {
            return value;
        }
    }

}
