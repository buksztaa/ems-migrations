package com.emsmigrations;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adambuksztaler on 08/06/16.
 */
public class TestPrintStream extends PrintStream{

    List<String> lines = new ArrayList<>();

    public TestPrintStream(OutputStream out) {
        super(out);
    }

    @Override
    public void println(String x) {
        lines.add(x);
    }
}
