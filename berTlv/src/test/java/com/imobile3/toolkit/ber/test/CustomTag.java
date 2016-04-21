package com.imobile3.toolkit.ber.test;

import java.util.Arrays;

import com.imobile3.toolkit.ber.BerTagBase;

public enum CustomTag implements BerTagBase{
	PRIMITIVE_TAG_1(new byte[]{1}),
	CONSTRUCTED_TAG_2(new byte[]{(byte)0b00100010}),
	PRIMITIVE_TAG_3(new byte[]{3}),
	CONSTRUCTED_TAG_4(new byte[]{(byte)0b00100100}),
	PRIMITIVE_TAG_5(new byte[]{5}),
	CONSTRUCTED_TAG_6(new byte[]{(byte)0b00100110});
	
	byte[] mTagBytes;  
	
	CustomTag(byte[] tagBytes) {
		mTagBytes = tagBytes;
	}
	
	public CustomTag fromValue(byte[] tagBytes) {
		for (CustomTag tag : values()) {
			if(Arrays.equals(tag.getTagBytes(), tagBytes))
				return tag;
		}
		return null;
	}
	
	@Override
	public byte[] getTagBytes() {
		return mTagBytes;
	}
	
}
