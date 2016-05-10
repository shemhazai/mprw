package com.github.shemhazai.mprw.utils;

import java.io.IOException;
import java.text.ParseException;

public interface DataCollector {
	public void collect() throws IOException, ParseException;
}
