package com.imobile3.toolkit.ber;

import java.math.BigInteger;

class BerLen {

    public static final int MAX_SIZE_BYTES = 3;
    // Size in bytes of the Apdu
    private int value;

    private BerLen(int value) {
        this.value = value;
    }

    public static BerLen newInstance(int value) {
        return new BerLen(value);
    }

    public int getValue() {
        return value;
    }

    public static BerLen decodeLength(byte[] data, int lengthOffset) {
        if ((data[lengthOffset] & 0xFF) == 0x81) {
            // Bitwise operation for proper conversion
            int value = data[lengthOffset + 1] & 0xff;
            return new BerLen(value);
        } else if ((data[lengthOffset] & 0xFF) == 0x82) {
            byte[] lengthArray = ByteUtils.subArray(data, lengthOffset + 1, 2);
            int value = new BigInteger(lengthArray).intValue();
            return new BerLen(value);
        }
        return new BerLen(data[lengthOffset]);
    }

    public int getNumOfBytesToEncodedLength() {
        int length;
        if (value < 0x80) {
            length = 1;
        } else if (value <= 0xff) {
            length = 2;
        } else {
            length = 3;
        }
        return length;
    }

    public static int getLengthBytesCount(byte buf[], int offset) {
        int len = buf[offset] & 0xff;
        if ((len & 0x80) == 0x80) {
            return 1 + (len & 0x7f);
        }
        return 1;
    }

    public byte[] asByteArray(){
        byte[] lenBytes = new byte[getNumOfBytesToEncodedLength()];
        encodeData(lenBytes, 0);
        return lenBytes;
    }

    public void encodeData(final byte[] bytes, int offset) {
        if (value <= 127) {
            bytes[offset] = (byte) value;
        } else if (value <= 255) {
            bytes[offset++] = (byte) 0x81;
            bytes[offset++] = (byte) value;
        } else {
            bytes[offset++] = (byte) 0x82;
            byte[] valueInBytes = BigInteger.valueOf(value).toByteArray();
            for (int i = 0; i < valueInBytes.length; i++) {
                if (i == 0 && valueInBytes[i] == 0) {
                    // The first is 0 and can be discarded.
                } else {
                    bytes[offset++] = valueInBytes[i];
                }
            }
        }
    }

}
