package com.gs.rm.common.csv;

import lombok.Data;

/**
 * author: linjuntan
 * date: 2018/2/27
 */
@Data
public class Map {
    @CsvAlias(alias = "id", clazz = Integer.class)
    private Integer _id;

    private String name;

    @CsvAlias(clazz = Integer.class)
    private Integer width;

    @CsvAlias(clazz = Integer.class)
    private Integer height;

    @CsvAlias(clazz = Double.class)
    private Double blockWidth;

    @CsvAlias(clazz = Double.class)
    private Double blockHeight;

    @CsvAlias(clazz = Integer.class)
    private Integer configID;
}
