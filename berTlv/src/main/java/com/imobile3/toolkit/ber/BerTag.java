package com.imobile3.toolkit.ber;

import java.util.Arrays;

class BerTag {

    private final byte[] mBytes;
    
    //TODO: Validate Tag data for all constructors.
    public BerTag(byte[] tagBytes) {
        mBytes = tagBytes;
    }

    public BerTag(byte[] buf, int offset) {
        this(buf, offset, getTagBytesCount(buf, offset));
    }

    public BerTag(byte[] buf, int offset, int length) {
        byte[] temp = new byte[length];
        System.arraycopy(buf, offset, temp, 0, length);
        mBytes = temp;
    }

    public BerTag(byte firstByte) {
        mBytes = new byte[] {firstByte};
    }

    public BerTag(byte firstByte, byte secondByte) {
        mBytes = new byte[] {firstByte, secondByte};
    }

    public BerTag(byte firstByte, byte secondByte, byte firth) {
        mBytes = new byte[] {firstByte, secondByte, firth};
    }

    public BerTag(int value) {
        byte[] tagBytes = ByteUtils.intToByteArray(value);
        mBytes = tagBytes;
    }

    public boolean isConstructed() {
        return (mBytes[0] & 0x20) != 0;
    }

    public boolean isLongTag() {
        return (mBytes[0] & 0x1F) == 0x1F;
    }

    public int getTagLength() {
        return mBytes.length;
    }

    public byte[] asByteArray(){
        return mBytes;
    }

    public static int getTagBytesCount(byte[] buf, int offset) {
        if((buf[offset++] & 0x1F) == 0x1F) {
            int len = 2;
            while((buf[offset++] & 0x80) == 0x80) {
                len++;
            }
            return len;
        }
        return 1;
    }

    public static boolean isLastTagByte(byte b) {
        return (b & 0x80) == 0x00;
    }

    public static boolean isValidTag(byte tag, int byteNumber) {
        switch (byteNumber) {
            case 0:
                // intentional fall-through
            case 1:
                if (tag == 0x00) return false;
                break;
            case 2:
                if (tag == 0x1E) return false;
                break;
            default:
                break;
        }

        return true;
    }

    public <T extends Enum<T> & BerTagBase> T toTagBase(Class<T> enumClazz) {
    	for(T enumObj : enumClazz.getEnumConstants()) {
    		if(Arrays.equals(enumObj.getTagBytes(), mBytes)) {
    			return enumObj;
    		}
    	}
    	return null;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BerTag berTag = (BerTag) o;

        return Arrays.equals(mBytes, berTag.asByteArray());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(mBytes);
    }

    @Override
    public String toString() {
        return (isConstructed() ? "+ " : "- ")+ ByteUtils.byteArrayToHexString(mBytes);
    }
}
