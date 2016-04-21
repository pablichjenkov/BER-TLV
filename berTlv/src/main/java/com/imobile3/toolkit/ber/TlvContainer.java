package com.imobile3.toolkit.ber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TlvContainer {
    public static final String TAG = TlvContainer.class.getSimpleName();

    private List<BerTlv> mTlvList;

    public TlvContainer() {
        mTlvList = new ArrayList<>();
    }

    public void addTlv(BerTlv tlv) {
        if (mTlvList == null) {
            mTlvList = new ArrayList<>();
        }
        mTlvList.add(tlv);
    }

    public void removeTlv(BerTlv tlv) {
        if (mTlvList == null) {
            return;
        }
        mTlvList.remove(tlv);
    }

    public void setTlvList(List<BerTlv> tlvList) {
        mTlvList = tlvList;
    }

    public List<BerTlv> getTlvList() {
        return mTlvList;
    }

    public <T extends Enum<T> & BerTagBase> BerTlv find(T enumTagBase){
        if(mTlvList != null && mTlvList.size() > 0) {
            for(BerTlv tlv : mTlvList){
                BerTlv tlvResult = tlv.find(enumTagBase);
                if(tlvResult != null) {
                    return tlvResult;
                }
            }
        }
        return null;
    }

    public BerTlv find(BerTag tag){
        if(mTlvList != null && mTlvList.size() > 0) {
            for(BerTlv tlv : mTlvList){
                BerTlv tlvResult = tlv.find(tag);
                if(tlvResult != null) {
                    return tlvResult;
                }
            }
        }
        return null;
    }

    public static List<BerTlv> parse(String hexTlvContainerString) {
    	byte[] tlvContainerBytes = ByteUtils.hexStringToByteArray(hexTlvContainerString);
    	if (tlvContainerBytes == null)
    		return null;
    	return parse(tlvContainerBytes, 0, tlvContainerBytes.length);
    }
    
    public static List<BerTlv> parse(final byte[] source, int start, int end) {
        List<BerTlv> berList = new ArrayList<>();
        int index = start;
        while(index < end){
            BerTag berTag = new BerTag(source, index);
            index += berTag.getTagLength();
            
            BerLen berLen = BerLen.decodeLength(source, index);
            index += berLen.getNumOfBytesToEncodedLength();
            
            BerTlvBuilder tlvBuilder = BerTlvBuilder.create(berTag);
            
            //Check for 0 length tags
            if(berLen.getValue() == 0) {
            	tlvBuilder.setValue(new byte[0]);
            	berList.add(tlvBuilder.build());
            	continue;
            }
            
            //Check for tags with length greater than original array
            if((index + berLen.getValue()) > end) {
            	throw new RuntimeException("Expected length of " + berLen.getValue() + ", found " + (end - index));
            }
            	
            if(berTag.isConstructed()) {
                //Let recursion do the job
                tlvBuilder.setInnerTlvList(parse(source, index, index + berLen.getValue()));

            } else {
                tlvBuilder.setValue(Arrays.copyOfRange(source, index, index + berLen.getValue()));

            }

            berList.add(tlvBuilder.build());
            index += berLen.getValue();
        }
        return berList;
    }

    public static List<BerTlv> getAllPrimitiveTagAsList(List<BerTlv> berTlvList) {
        List<BerTlv> list = new ArrayList<>();
        for(BerTlv tlv : berTlvList) {
            list.addAll(getAllPrimitiveTagAsList(tlv));
        }
        return list;
    }

    public static List<BerTlv> getAllPrimitiveTagAsList(BerTlv tlv) {
        List<BerTlv> list = new ArrayList<>();
        if(tlv.isConstructed()) {
            list.addAll(getAllPrimitiveTagAsList(tlv.getInnerTlvs()));
        } else {
            list.add(tlv);
        }
        return list;
    }

    public static <T extends Enum<T> & BerTagBase> Map<T, byte[]> getAllPrimitiveTagAsMap(Class<T> enumClass
    		, List<BerTlv> berTlvList) {
        Map<T, byte[]> mapResult = new LinkedHashMap<>();
        for (BerTlv tlv : berTlvList)
            mapResult.putAll(getAllPrimitiveTagAsMap(enumClass, tlv));
        return mapResult;
    }

    public static <U extends Enum<U> & BerTagBase> Map<U, byte[]> getAllPrimitiveTagAsMap(Class<U> enumClass, BerTlv tlv) {
        Map<U, byte[]> mapResult = new LinkedHashMap<>();
        if (tlv.isConstructed()) {
            mapResult.putAll(getAllPrimitiveTagAsMap(enumClass, tlv.getInnerTlvs()));
        } else {
            mapResult.put(tlv.getTagBase(enumClass), tlv.getValue());
        }
        return mapResult;
    }

}
