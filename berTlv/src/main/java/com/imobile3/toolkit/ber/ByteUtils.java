package com.imobile3.toolkit.ber;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class ByteUtils {

    private ByteUtils(){}

    private static ByteBuffer mBuffer = ByteBuffer.allocate(Long.SIZE);

    public static byte[] fromStringToUtf8ByteArray(String text){
        try {
            return text.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String fromByteArrayToUtf8String(byte[] bytes){
        try {
            return new String(bytes, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] concatAll(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    public static byte[] subArray(final byte[] data, final int offset, final int length) {
        // Check if dataLength in not bigger than the remaining data
        if (length > data.length) return new byte[]{};

        byte[] subArray = new byte[length];
        for (int i = 0; i < length; i++) {
            subArray[i] = data[offset + i];
        }
        return subArray;
    }

    public static byte[] listToArray(List<Byte> data) {
        byte[] bytes = new byte[data.size()];
        int offset = 0;
        for (byte b : data) {
            bytes[offset++] = b;
        }
        return bytes;
    }

    public static String byteArrayToHexString(final byte[] array) {
        if (array == null) return null;
        return byteArrayToHexString(array, 0, array.length);
    }

    public static String byteArrayToHexString(final byte[] array, final int offset, final int length) {
        if (array == null) return null;
        final StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < length; i++) {
            final String hex = Integer.toHexString(0xFF & array[i + offset]);
            if (hex.length() == 1) {
                hexString.append("0");
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static byte[] hexStringToByteArray(String hex) {
        if(hex == null) return null;

        if(hex.startsWith("0x") || hex.startsWith("0X")) {
            hex = hex.substring(2);
        }
        int len = hex.length();
        if (len == 0 || hex.isEmpty() || len % 2 != 0) return null;

        final byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    public static int byteArrayToInt(final byte[] b) {
        if (b.length < 2) {
            return b[0] & 0xFF;
        }
        if (b.length < 3) {
            return b[1] & 0xFF | (b[0] & 0xFF) << Byte.SIZE;
        }
        if (b.length < 4) {
            return b[2] & 0xFF | (b[1] & 0xFF) << Byte.SIZE | (b[0] & 0xFF) << 2 * Byte.SIZE;
        }
        return b[3] & 0xFF | (b[2] & 0xFF) << Byte.SIZE | (b[1] & 0xFF) << 2 * Byte.SIZE
                | (b[0] & 0xFF) << 3 * Byte.SIZE;
    }

    public static byte[] intToByteArray(int iValue){
        byte[] array = BigInteger.valueOf(iValue).toByteArray();
        if (array[0] == 0) {
            byte[] tmp = new byte[array.length - 1];
            System.arraycopy(array, 1, tmp, 0, tmp.length);
            array = tmp;
        }
        return array;
    }

    public static byte[] longToByteArray(long x) {
        mBuffer.putLong(0, x);
        return mBuffer.array();
    }

    public static long byteArrayToLong(byte[] bytes) {
        mBuffer.put(bytes, 0, bytes.length);
        mBuffer.flip();//need flip
        return mBuffer.getLong();
    }

    public static byte combine(final int first, final int second) {
        return (byte) (second & 0x0000FFFF | (first & 0x0000FFFF) << 4);
    }

    public static int minNumberOfBytesNeededToStoreInt(final int integer) {
        return minNumberOfBytesNeededToStoreInt(integer, Byte.SIZE);
    }

    /**
     * Calculate the minimum number of bytes needed to the integer (without leading 0s) if only {@code space} bits can
     * be written to each byte.
     *
     * @param integer
     *            The integer that needs to be stored in a byte array.
     * @param space
     *            The number of bits available in each byte for storage.
     * @return The minimum number of bytes this integer could be stored in.
     */
    public static int minNumberOfBytesNeededToStoreInt(final int integer, final int space) {
        int bytesNeeded = 0;
        int remainingInt = integer;
        for (int i = 0; i < 4; i++) {
            remainingInt = remainingInt >> space;
            bytesNeeded++;
            if (remainingInt == 0) {
                return bytesNeeded;
            }
        }
        return bytesNeeded;
    }

    public static int fromBcd(byte b) {//returns an int < 99
        if ((b & 0xFF) > 0x99)
            throw new RuntimeException("Value greater than 99 is not a valid BCD encoded byte");

        int units = b & 0x0F;
        int tens = b >> 4;
        return ((tens * 10) + units);
    }

    public static String fromBcdToString(byte b) {
        return Byte.toString((byte)fromBcd(b));
    }

    /**
     * Converts a standard decimal value to a Binary Coded Decimal value.
     * <p/>
     * For example, this will convert (long)10 to (byte)0x10.
     */
    public static byte[] toBcd(long number, int bytes) {
        byte[] outBytes = new byte[bytes];
        for (int i = 0; (i < bytes) && (number > 0); i++) {
            outBytes[bytes-i-1] = toBcd((int)(number%100));
            number /= 100;
        }
        return outBytes;
    }

    public static byte toBcd(int number) {//assume number < 100
        if (number > 99)
            throw new RuntimeException("Integer greater than 99 does not fit in one BCD byte");

        int lsb = number % 10;
        int msb = number/10;
        return (byte)((msb << 4 | lsb) & 0xFF);
    }

}
