package com.thingple.uhf;

public interface TagInfoBuilder {

	TagInfoBuilder data(byte[] raw);
	
	TagInfo build();
}
