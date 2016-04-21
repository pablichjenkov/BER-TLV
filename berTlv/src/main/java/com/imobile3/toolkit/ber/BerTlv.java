package com.imobile3.toolkit.ber;

import java.util.ArrayList;
import java.util.List;

public class BerTlv {
    private static String TAG = BerTlv.class.getSimpleName();

    private final BerTag mTag;
    private BerLen mLen;
    private final byte[] mValue;
    protected final List<BerTlv> mInnerTlvs;

    /**
     * Creates constructed TLV
     *
     * @param tag   tag
     * @param innerTlvs  set of nested TLVs
     */
    BerTlv(BerTag tag, List<BerTlv> innerTlvs) {
        mTag = tag;
        mInnerTlvs = innerTlvs;
        mValue = null;
    }

    /**
     * Creates primitive TLV
     *
     * @param tag   tag
     * @param value value as byte[]
     */
    BerTlv(BerTag tag, byte[] value) {
        mTag = tag;
        mLen = BerLen.newInstance(value.length);
        mValue = value;
        mInnerTlvs = null;
    }

    public BerTag getTag() {
        return mTag;
    }

    public <T extends Enum<T> & BerTagBase> T getTagBase(Class<T> clazz){
        return mTag.toTagBase(clazz);
    }

    public void addTlv(BerTlv berTlv) {
        if(isPrimitive()) throw new IllegalStateException("addTlv non valid on PRIMITIVE Tag");
        mInnerTlvs.add(berTlv);
    }

    public void removeTlv(BerTlv subTlv){
        if(isPrimitive()) throw new IllegalStateException("removeTlv non valid on PRIMITIVE Tag");
        mInnerTlvs.remove(subTlv);
    }

    public List<BerTlv> getInnerTlvs() {
        if(isPrimitive()) throw new IllegalStateException("getInnerTlvs non valid on PRIMITIVE Tag");
        return mInnerTlvs;
    }

    public byte[] getValue() {
        if (isPrimitive()) return mValue;
        throw new IllegalStateException("getValue() non valid on Constructed Tag");
    }

    public boolean isPrimitive() {
        return !mTag.isConstructed();
    }

    public boolean isConstructed() {
        return mTag.isConstructed();
    }

    public <T extends Enum<T> & BerTagBase> BerTlv find(T tagBase) {
    	BerTagBase tagBaseResult = mTag.toTagBase(tagBase.getDeclaringClass());
        if (tagBaseResult == tagBase) {
            return this;
        }

        if (isConstructed() && mInnerTlvs != null) {
            for (BerTlv tlv : mInnerTlvs) {
                BerTlv tlvResult = tlv.find(tagBase);
                if (tlvResult != null) return tlvResult;
            }
        }
        return null;
    }

    public <T extends Enum<T> & BerTagBase> List<BerTlv> findAll(T tagBase) {
        List<BerTlv> list = new ArrayList<>();
        if(isConstructed()) {
            if(tagBase.equals(getTagBase(tagBase.getDeclaringClass()))) {
                list.add(this);

            } else {//We assume a Tag will never contain its same tagDescriptor. Otherwise this else would not exist
                for(BerTlv tlv : mInnerTlvs){
                    list.addAll(tlv.findAll(tagBase));
                }
            }

        } else if (tagBase.equals(getTagBase(tagBase.getDeclaringClass()))) {
            list.add(this);
        }
        return list;
    }

    public BerTlv find(BerTag tag) {
        if(tag.equals(getTag())) {
            return this;
        }

        if(isConstructed()) {
            for (BerTlv tlv : mInnerTlvs) {
                BerTlv ret = tlv.find(tag);
                if (ret != null) return ret;
            }
        }
        return null;
    }

    public List<BerTlv> findAll(BerTag tag) {
        List<BerTlv> list = new ArrayList<>();
        if(isConstructed()) {
            if(tag.equals(mTag)) {
                list.add(this);

            } else {//We assume a Tag will never contain its same tag. Otherwise this 'else{}' would not exist
                for(BerTlv tlv : mInnerTlvs){
                    list.addAll(tlv.findAll(tag));
                }
            }

        } else if (tag.equals(mTag)) {
            list.add(this);
        }
        return list;
    }

    public byte[] getInnerTlvsAsByteArray(){
        byte[] innerTlvBytes = new byte[0];
        for(BerTlv tlv : mInnerTlvs){
            //Let recursion do the job
            innerTlvBytes = ByteUtils.concatAll(innerTlvBytes, tlv.asByteArray());
        }
        mLen = BerLen.newInstance(innerTlvBytes.length);
        return innerTlvBytes;
    }

    public byte[] asByteArray(){
        if(isPrimitive()) {
            return ByteUtils.concatAll(mTag.asByteArray(), mLen.asByteArray(), mValue);
        }
        byte[] innerTlvBytes = getInnerTlvsAsByteArray();
        mLen = BerLen.newInstance(innerTlvBytes.length);
        return ByteUtils.concatAll(mTag.asByteArray(), mLen.asByteArray(), innerTlvBytes);
    }

    public void logPretty() {
        logPretty(0);
    }

    private void logPretty(int space) {
        String margin = "";
        for(int i = 0; i<space; i++)
            margin = margin.concat("    ");
        if(isConstructed()) {
            getInnerTlvsAsByteArray();
            System.out.println(margin + ByteUtils.byteArrayToHexString(mTag.asByteArray())
                + " " + ByteUtils.byteArrayToHexString(mLen.asByteArray()));
            for(BerTlv tlv : mInnerTlvs) {
                tlv.logPretty(space + 1);
            }

        } else {
        	System.out.println(margin + ByteUtils.byteArrayToHexString(mTag.asByteArray()) + " "
                    + ByteUtils.byteArrayToHexString(mLen.asByteArray()) + " "
                    + ByteUtils.byteArrayToHexString(mValue));
        }
    }

}
