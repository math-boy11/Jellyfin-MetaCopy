package com.mathboy11;

import org.apache.tika.Tika;

public class TikaUtil {
    private static final Tika TIKA = new Tika();

    public static Tika getTika() {
        return TIKA;
    }
}
