
package com.m6.gocook.util.model;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.SyncStateContract.Columns;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ModelUtils {

    /**
     * 将cursor数据转为 列表数据,除了_id,其它数据类型只能是String <br/>
     * 本方法不会关闭cursor,请在使用完成后注意关闭cursor
     * 
     * @param cursor
     * @param nameAlias 返回列表中map key值，可以为空
     * @return
     */
    public static List<Map<String, Object>> cursor2MapList(Cursor cursor, String[] nameAlias) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        String[] columnNames = cursor == null ? null : cursor
                .getColumnNames();
        final int count = columnNames == null ? 0 : columnNames.length;

        if (count == 0) {
            return list;
        }
        final int[] indexs = new int[count];
        final String[] keys = new String[count]; 
        for (int i = 0; i < count; i++) {
            indexs[i] = cursor.getColumnIndexOrThrow(columnNames[i]);
            keys[i] = nameAlias == null || TextUtils.isEmpty(nameAlias[i]) ? columnNames[i] : nameAlias[i];
        }
        
        if (!cursor.moveToFirst()) {
            return list;
        }
        
        do {
            Map<String, Object> item = new HashMap<String, Object>();
            for (int i = 0; i < count; i++) {
                String name = columnNames[i];
                String key = keys[i];
                if (Columns._ID.equals(name)) {
                    item.put(key, cursor.getInt(indexs[i]));
                } else {
                    item.put(key, cursor.getString(indexs[i]));
                }
            }

            list.add(item);
        } while (cursor.moveToNext());

        return list;
    }
    
    /**
     * 将cursor数据转为 列表数据,除了_id,其它数据类型只能是String <br/>
     * 本方法不会关闭cursor,请在使用完成后注意关闭cursor
     * 
     * @param cursor
     * @param nameAlias 返回列表中map key值，可以为空
     * @param keyName 要排重的列名
     * @return
     */
    public static List<Map<String, Object>> cursor2MapListWithoutDuplicatedByKey(Cursor cursor, String[] nameAlias, String keyName) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        StringBuilder sb = new StringBuilder();
        boolean duplicatedFlag = false;
        
        String[] columnNames = cursor == null ? null : cursor
                .getColumnNames();
        final int count = columnNames == null ? 0 : columnNames.length;
        
        if (count == 0) {
            return list;
        }
        final int[] indexs = new int[count];
        final String[] keys = new String[count]; 
        for (int i = 0; i < count; i++) {
            indexs[i] = cursor.getColumnIndexOrThrow(columnNames[i]);
            keys[i] = nameAlias == null || TextUtils.isEmpty(nameAlias[i]) ? columnNames[i] : nameAlias[i];
        }
        
        if (!cursor.moveToFirst()) {
            return list;
        }
        
        do {
            Map<String, Object> item = new HashMap<String, Object>();
            for (int i = 0; i < count; i++) {
                String name = columnNames[i];
                String key = keys[i];
                if (Columns._ID.equals(name)) {
                    item.put(key, cursor.getInt(indexs[i]));
                } else {
                    String value = cursor.getString(indexs[i]);
                    if(name.equals(keyName)) {
                        if(sb.indexOf(value) != -1) {
                            duplicatedFlag = true;
                            break;
                        }
                        sb.append(value + ",");
                    }
                    item.put(key, value);
                }
            }
            if(!duplicatedFlag) {
                list.add(item);
            }
            duplicatedFlag = false;
        } while (cursor.moveToNext());
        
        return list;
    }
    
    /**
     * map 转 json
     * 
     * @param map
     * @return
     */
    public static JSONObject map2Json(Map<String, Object> map) {
        JSONObject json = new JSONObject();
        if (map != null) {
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                try {
                    String key = iterator.next();

                    if (map.containsKey(key)) {

                        Object o = map.get(key);
                        if (o == null) {
                            continue;
                        }

                        if (o instanceof Map) {
                            json.put(key, map2Json((Map<String, Object>) o));
                        } else if (o instanceof List) {
                            List list = (List) o;
                            final int length = list.size();
                            JSONArray array = new JSONArray();

                            for (int i = 0; i < length; i++) {
                                array.put(map2Json((Map<String, Object>) list.get(i)));
                            }

                            json.put(key, array);
                        } else if (o instanceof String) {
//                            json.put(key, Strings.htmlDecoder((String) o));
                            json.put(key, (String) o);
                        } else {
                            json.put(key, o);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return json;
    }
    
    /**
     * List<Map<String, Object> 转 JSONArray
     * @param list
     * @return
     */
    public static JSONArray ListMap2JSONArray(List<Map<String, Object>> list) {
        JSONArray array = new JSONArray();
        if(list == null || list.isEmpty()) {
            return array;
        }
        
        int size = list.size();
        for(int i = 0; i < size; i++) {
            JSONObject json = map2Json(list.get(i));
            if(json != null) {
                array.put(json);
            }
        }
        return array;
    }

    /**
     * json 转 map
     * 
     * @param json
     * @return
     */
    public static Map<String, Object> json2Map(JSONObject json) {
        return json2Map(json, false);
    }
    
    /**
     * json 转 map
     * @param json
     * @param all2String 如果是true,会把所有value转成String
     * @return
     */
    public static Map<String, Object> json2Map(JSONObject json, boolean all2String) {
        Map<String, Object> map = new HashMap<String, Object>();

        if (json != null) {
            Iterator<String> iterator = json.keys();

            while (iterator.hasNext()) {
                try {
                    String key = iterator.next();

                    if (!json.isNull(key)) {

                        Object o = json.get(key);
                        if (o == null) {
                            continue;
                        }
                        if (o instanceof JSONObject) {
                            map.put(key, json2Map((JSONObject) o, all2String));
                        } else if (o instanceof JSONArray) {
                            JSONArray array = (JSONArray) o;
                            final int length = array.length();
                            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                            for (int i = 0; i < length; i++) {
                                list.add(json2Map(array.getJSONObject(i), all2String));
                            }

                            map.put(key, list);
                        } else if (o instanceof String) {
//                            map.put(key, Strings.htmlDecoder((String) o));
                            map.put(key, (String) o);
                        } else {
                            map.put(key, all2String ? o.toString() : o);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return map;
    }
    
    /**
     * Map 转 ContentValues
     * @param map
     * @return
     */
    public static ContentValues map2ContentValues(Map<String, Object> map) {
        ContentValues values = new ContentValues();
        if (map != null) {
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                try {
                    String key = iterator.next();
                    
                    if (map.containsKey(key)) {

                        Object o = map.get(key);
                        if (o == null) {
                            continue;
                        }

                        if (o instanceof String) {
                            values.put(key, (String) o);
                        } else if (o instanceof Integer) {
                            values.put(key, (Integer) o);
                        } else if (o instanceof Float) {
                            values.put(key, (Float) o);
                        } else if(o instanceof Boolean) {
                            values.put(key, (Boolean) o);
                        } else if(o instanceof Long) {
                            values.put(key, (Long) o);
                        } else if(o instanceof Short) {
                            values.put(key, (Short) o);
                        } else if(o instanceof Byte) {
                            values.put(key, (Byte) o);
                        } else if(o instanceof Double) {
                            values.put(key, (Double) o);
                        } else if(o instanceof byte[]) {
                            values.put(key, (byte[]) o);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return values;
    }

    /**
     * 返回map中值<br/>
     * 
     * @param map
     * @param key
     * @return
     */
    public static Object getValue(Map<String, Object> map, String key) {
        if (map == null || TextUtils.isEmpty(key)) {
            return null;
        }
        return map.get(key);
    }

    /**
     * 返回int
     * 
     * @param map
     * @param key
     * @param defaultValue
     * @return
     */
    public static int getIntValue(Map<String, Object> map, String key, int defaultValue) {
        Object o = getValue(map, key);
        if (o != null && o instanceof Integer) {
            return ((Integer) o).intValue();
        }
        return defaultValue;
    }

    /**
     * 返回long
     * 
     * @param map
     * @param key
     * @param defaultValue
     * @return
     */
    public static long getLongValue(Map<String, Object> map, String key, long defaultValue) {
        Object o = getValue(map, key);
        if (o != null && o instanceof Long) {
            return ((Long) o).longValue();
        }
        return defaultValue;
    }

    /**
     * 返回float
     * 
     * @param map
     * @param key
     * @param defaultValue
     * @return
     */
    public static float getFloatValue(Map<String, Object> map, String key, float defaultValue) {
        Object o = getValue(map, key);
        if (o != null && o instanceof Float) {
            return ((Float) o).floatValue();
        }
        return defaultValue;
    }

    /**
     * 返回double
     * 
     * @param map
     * @param key
     * @param defaultValue
     * @return
     */
    public static double getDoubleValue(Map<String, Object> map, String key, double defaultValue) {
        Object o = getValue(map, key);
        if (o != null && o instanceof Double) {
            return ((Double) o).doubleValue();
        }
        return defaultValue;
    }

    /**
     * 返回boolean
     * 
     * @param map
     * @param key
     * @param defaultValue
     * @return
     */
    public static boolean getBooleanValue(Map<String, Object> map, String key, boolean defaultValue) {
        Object o = getValue(map, key);
        if (o != null && o instanceof Boolean) {
            return ((Boolean) o).booleanValue();
        }
        return defaultValue;
    }
    
    /**
     * 返回String
     * 
     * @param map
     * @param key
     * @return
     */
    public static String getStringValue(Map<String, Object> map, String key) {
        Object o = getValue(map, key);
        if (o != null) {
            return o.toString();
        }
        return "";
    }
    
    /**
     * 返回Map<String, Object>,注意如果非Map<String, Object>会报错
     * @param map
     * @param key
     * @return
     */
    public static Map<String, Object> getMapValue(Map<String, Object> map, String key) {
        Object o = getValue(map, key);
        if (o != null && o instanceof Map<?, ?>) {
            return (Map<String, Object>)o;
        }
        return null;
    }
    
    /**
     * 返回List<Map<String, Object>>,注意如果非List<Map<String, Object>>会报错
     * @param map
     * @param key
     * @return
     */
    public static List<Map<String, Object>> getListMapValue(Map<String, Object> map, String key) {
        Object o = getValue(map, key);
        if (o != null && o instanceof List<?>) {
            return (List<Map<String, Object>>)o;
        }
        return null;
    }
}
