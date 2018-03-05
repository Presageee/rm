package com.gs.rm.common.csv;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.reflect.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * author: linjuntan
 * date: 2018/2/27
 */
@Slf4j
public class CsvReader<T> {
    private volatile Class<T> clazz = null;

    /**
     *
     * @param file
     * @return
     * @throws Exception
     */
    public List<T> getListByCsv(File file) throws Exception {
        return getListByCsv(file, getClazz());
    }

    /**
     *
     * @param file
     * @param clazz
     * @return
     * @throws Exception
     */
    public List<T> getListByCsv(File file, Class<T> clazz) throws Exception {
        List<String> lines = Files.readAllLines(file.toPath(), Charset.forName("UTF-8"));
        if (lines.size() == 0) {
            return new ArrayList<>();
        }

        String fieldStr = lines.get(0);
        if (fieldStr == null || "".equals(fieldStr)) {
            throw new CsvReadException("field string is blank or null");
        }

        List<Field> fieldList = getCsvPojoFields(fieldStr.split(","), clazz);

        List<T> objects = new ArrayList<>();

        lines.stream().skip(2).forEach(value -> {
            try {
                newInstance(value, fieldList, clazz, objects);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });

        return objects;
    }

    @SuppressWarnings("unchecked")
    private void newInstance(String values, List<Field> fieldList, Class<T> clazz, List<T> objs) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        if (values == null || "".equals(values)) {
            return;
        }

        String[] valueArray = values.split(",");
        T t = clazz.newInstance();

        for (int i = 0; i < valueArray.length; i++) {
            Field field = fieldList.get(i);

            field.setAccessible(true);
            if (!field.isAnnotationPresent(CsvAlias.class)) {
                field.set(t, valueArray[i]);
            } else {
                //Long, Integer, Float, Double
                Class typeClazz = field.getDeclaredAnnotation(CsvAlias.class).clazz();
                Constructor constructor = typeClazz.getConstructor(String.class);
                field.set(t, constructor.newInstance(valueArray[i]));
            }
        }

        objs.add(t);
    }

    private List<Field> getCsvPojoFields(String[] fields, Class<T> clazz) throws NoSuchFieldException {
        Field[] allFields = clazz.getDeclaredFields();
        List<Field> fieldList = new ArrayList<>(fields.length);

        for (String str : fields) {
            //BOM标记
            str = str.replaceAll(String.valueOf('\uFEFF'), "");
            try {
                Field field = clazz.getDeclaredField(str);
                fieldList.add(field);
            } catch (NoSuchFieldException e) {
                log.warn(" >>> field not match, try csv alias annotation");
                boolean isFind = false;
                for (Field field : allFields) {
                    if (!field.isAnnotationPresent(CsvAlias.class)) {
                        continue;
                    }

                    if (str.equals(field.getDeclaredAnnotation(CsvAlias.class).alias())) {
                        fieldList.add(field);
                        isFind = true;
                        log.info(" >>> field find by csv alias annotation");
                        break;
                    }
                }

                if (!isFind) {
                    throw e;
                }
            }
        }

        return fieldList;
    }

    @SuppressWarnings("unchecked")
    private Class<T> getClazz() {
        if (clazz == null) {
            synchronized (this) {
                if (clazz == null) {
                    Type genType = getClass().getGenericSuperclass();
                    Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
                    clazz = (Class<T>) params[0];
                }
            }
        }
        return clazz;
    }
}
