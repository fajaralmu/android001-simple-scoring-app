package tasmi.rouf.com.json;

import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import tasmi.rouf.com.util.Constant;

public class MyJSON {

    public static String[] getObjFromArray2(String arrayJSON) {
        String[] objs = {};
        arrayJSON = arrayJSON.replace("},{", "ڑ");
        System.out.println(arrayJSON);
        if (arrayJSON.charAt(0) == '['
                && arrayJSON.charAt(arrayJSON.length() - 2) == ']') {
            objs = arrayJSON.split("ڑ");
        }
        return objs;
    }

    public static int lastCharOfProp(int mulai, String s) {
        int x = 0;
        int y = 0;
        for (int i = mulai; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '{') {
                x++;
            }
            if (c == '}' && x > 0) {
                x--;
            }
            if (c == '[') {
                y++;
            }
            if (c == ']' && y > 0) {
                y--;
            }
            if (x == 0 && y == 0) {
                return i;
            }
        }
        return mulai;
    }

    public static List<String> getObjFromArray(String json) {
        List<String> obj_list = new ArrayList<String>();
        boolean inobj = false;
        String nowObj = "";
        for (int i = 0; i < json.length() - 1; i++) {
            char c = json.charAt(i);
            if (c == '{') {
                nowObj = "";
                int nextI = lastCharOfProp(i, json);
                for (int y = i + 2; y < nextI - 1; y++) {
                    nowObj += json.charAt(y);
                }
                i = nextI++;

                obj_list.add(nowObj);
                continue;
            }
        }

        return obj_list;
    }

    public static List<String> getObjFromArra3y(String json) {
        List<String> obj_list = new ArrayList<>();
        boolean inobj = false;
        String nowObj = "";
        for (int i = 0; i < json.length() - 1; i++) {
            char c = json.charAt(i);
            if (c == '{' && !inobj) {
                inobj = true;
                continue;
            }
            if (inobj) {
                if (c == '}'
                        && (json.charAt(i + 1) == ']' || (json.charAt(i + 1) == ',' && json
                        .charAt(i + 2) == '{'))) {
                    inobj = false;
                    obj_list.add(nowObj);
                    nowObj = "";
                    continue;
                }
                nowObj += c;

            }

        }

        return obj_list;
    }

    public static List<String> extractObj(String obj) {
        boolean inproperty = false;
        String nowProp = "";
        String cleanQuotes = "";
        StringBuilder sb = new StringBuilder(obj);

        if (sb.charAt(0) == '{') {
            sb.deleteCharAt(0);
            if (sb.charAt(sb.length() - 1) == '}') {
                sb.deleteCharAt(sb.length() - 1);
            }
        }
        //   Log.i(Constant.tag,sb.toString());
        obj = sb.toString();
        Log.i(Constant.tag,"OBJ"+obj);

        for (int i = 0; i < obj.length(); i++) {
            char c = obj.charAt(i);
            if (c == '"') {
                if (i > 0 && obj.charAt(i - 1) == '\\') {
                    cleanQuotes += c;
                } else {

                }
            } else {
                cleanQuotes += c;
            }
        }

        List<String> listProp = new ArrayList<String>();
        inproperty = true;
        for (int i = 0; i < cleanQuotes.length(); i++) {
            char c = cleanQuotes.charAt(i);

            if (c == '{' || c == '[') {
                i = lastCharOfProp(i, cleanQuotes);
                continue;
            }
            if (c == ',') {
                listProp.add(nowProp);
                nowProp = "";
            } else {
                nowProp += c;
            }
            if (i == cleanQuotes.length() - 1) {
                listProp.add(nowProp);
            }
        }


        return listProp;
    }

    public static String[] propVal(String s) {

        String[] cc = new String[]{"0", "0"};
        String prop = "";
        String val = "";
        boolean nowPropName = true;
        if (s.contains(":")) {
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);

                if (c == ':') {
                    nowPropName = false;
                    continue;
                }
                if (nowPropName) {
                    prop += c;
                } else {
                    val += c;
                }
            }
        }
        cc[0] = prop;
        cc[1] = val;
     //   if(cc[0].equals("nilai"))
     //   Log.i(Constant.tag,"NILAI_PROP: "+ cc[1]);

        return cc;
    }


    private static String eliminateGet(String s) {
        s = s.substring(3);
        s = s.toLowerCase();
        return s;
    }

    private static String eliminateSet(String s) {
        s = s.substring(3);
        s = s.toLowerCase();
        return s;
    }

    private static boolean validate(String function) {
        if (function.contains("_")) {
            return false;
        }
        return true;
    }

    private static String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    public static Object getObj(Object o, List<String[]> props) {
        Class<? extends Object> c = o.getClass();
        Object obj = null;
        try {
            obj = c.newInstance();
            Method[] methods = c.getDeclaredMethods();
            for (String[] s : props) {
                String propName = capitalize(s[0]);
                for (int i = 0; i < methods.length; i++) {
                    String methodName = methods[i].getName();
                    if (!validate(methodName))
                        continue;
                    if (methodName.startsWith("get")) {
                        String prop = eliminateGet(methodName);
                        Class<?> returnType = methods[i].getReturnType();
                        final String functionName = "set" + capitalize(prop);
                        Method set = c.getMethod(functionName,
                                returnType);
                        if(prop.equals("namakelas"))
                            Log.i(Constant.tag,"RETURN TYPE:"+ returnType.getName());
                        if (prop.toLowerCase().equals(propName.toLowerCase())) {
                            String propToSet = s[1];
                            //  System.out.println(prop+":"+s[1]);
                            if (prop.toLowerCase().equals("nilai"))
                                Log.i(Constant.tag, "NILAI: " + s[1]);
                            if (returnType.equals(Integer.class)) {
                                Integer val = !propToSet.matches("-?\\d+") ? 0 : Integer.parseInt(propToSet);
                                set.invoke(obj, val);
                            } else
                                set.invoke(obj, propToSet);
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return obj;
    }
}
