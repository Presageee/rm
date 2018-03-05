package com.gs.rm.common.csv;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

/**
 * author: linjuntan
 * date: 2018/2/27
 */
public class CsvReaderTest {
    @org.junit.Test
    public void getListByCsv() throws Exception {
        File file = new File("D:\\workspace2\\map.csv");
        CsvReader<Map> csvReader = new CsvReader<>();
        List<Map> maps = csvReader.getListByCsv(file, Map.class);

        Assert.assertNotNull(maps);
    }

    @org.junit.Test
    public void getListByCsv1() throws Exception {
    }

}