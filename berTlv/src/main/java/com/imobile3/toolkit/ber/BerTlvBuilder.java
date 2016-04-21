package com.imobile3.toolkit.ber;

import java.util.ArrayList;
import java.util.List;

public class BerTlvBuilder {

    private BerTag mBerTag;
    private BerTlv mTlv;

    private BerTlvBuilder(BerTag berTag) {
        mBerTag = berTag;
    }

    static BerTlvBuilder create(BerTag berTag) {
        return new BerTlvBuilder(berTag);
    }

    public void setBerTag(BerTag berTag) {
        mBerTag = berTag;
        mTlv = null;
    }

    BerTlvBuilder setValue(byte[] value) {
        if (mBerTag == null) {
            throw new RuntimeException("No Tag in this Tlv builder, assign Tag first.");
        }
        if (mBerTag.isConstructed()) {
            throw new RuntimeException("This is a constructed Tlv so no value can be set.");
        }
        mTlv = new BerTlv(mBerTag, value);
        return this;
    }

    BerTlvBuilder setValue(String valueAsHexString) {
        if (mBerTag == null) {
            throw new RuntimeException("No Tag in this Tlv builder, assign Tag first.");
        }
        if (mBerTag.isConstructed()) {
            throw new RuntimeException("This is a constructed Tlv so no value can be set.");
        }
        byte[] valueBytes = ByteUtils.hexStringToByteArray(valueAsHexString);
        if (valueBytes == null) {
            throw new RuntimeException(valueAsHexString + " is not a valid Hex String.");
        }
        mTlv = new BerTlv(mBerTag, valueBytes);
        return this;
    }

    BerTlvBuilder addInnerTlv(BerTlv innerTlv) {
        if (mBerTag == null) {
            throw new RuntimeException("No Tag in this Tlv builder, assign Tag first.");
        }
        if (!mBerTag.isConstructed()) {
            throw new RuntimeException("This is a non constructed Tlv so no subTlv can be added.");
        }
        if (mTlv == null) {
            List<BerTlv> subTlvs = new ArrayList<>();
            mTlv = new BerTlv(mBerTag, subTlvs);
        }
        mTlv.addTlv(innerTlv);
        return this;
    }

    BerTlvBuilder setInnerTlvList(List<BerTlv> innerTlvList) {
        if (mBerTag == null) {
            throw new RuntimeException("No Tag in this Tlv builder, assign Tag first.");
        }
        if (!mBerTag.isConstructed()) {
            throw new RuntimeException("This is a non constructed Tlv so no subTlv can be added.");
        }
        mTlv = new BerTlv(mBerTag, innerTlvList);
        return this;
    }

    BerTlv build() {
        return mTlv;
    }

    public static class Constructed implements Tlv, ConstructedTlv {
        BerTlvBuilder mBerTlvBuilder;

        Constructed(BerTlvBuilder berTlvBuilder) {
            mBerTlvBuilder = berTlvBuilder;
        }

        public static Constructed create(byte[] tagBytes) {
            BerTag berTag = new BerTag(tagBytes);
            if (berTag.isConstructed()) {
                BerTlvBuilder berTlvBuilder = new BerTlvBuilder(berTag);
                Constructed constructed = new Constructed(berTlvBuilder);
                return constructed;
            }
            return null;
        }

        public static Constructed create(String hexTagBytes) {
            byte[] tagBytes = ByteUtils.hexStringToByteArray(hexTagBytes);
            if (tagBytes == null)
                return null;
            return create(tagBytes);
        }

        public static Constructed create(BerTagBase tagBase) {
            BerTag berTag = new BerTag(tagBase.getTagBytes());
            if (berTag.isConstructed()) {
                BerTlvBuilder berTlvBuilder = new BerTlvBuilder(berTag);
                Constructed constructed = new Constructed(berTlvBuilder);
                return constructed;
            }
            return null;
        }

        @Override
        public void setTag(String tagBytesHexString) {
            byte[] tagBytes = ByteUtils.hexStringToByteArray(tagBytesHexString);
            if (tagBytes == null)
                throw new RuntimeException(tagBytesHexString + " is not a valid Hex String");
            setTag(tagBytes);
        }

        @Override
        public <T extends Enum<T> & BerTagBase> void setTag(T enumTagBase) {
            setTag(enumTagBase.getTagBytes());
        }

        @Override
        public void setTag(byte[] tagBytes) {
            BerTag berTag = new BerTag(tagBytes);
            if (!berTag.isConstructed())
                throw new RuntimeException("This tag is not constructed");
            mBerTlvBuilder.setBerTag(new BerTag(tagBytes));
        }

        @Override
        public BerTlvBuilder setInnerTlvList(List<BerTlv> innerTlvList) {
            if (mBerTlvBuilder != null) {
                mBerTlvBuilder.setInnerTlvList(innerTlvList);
            }
            return mBerTlvBuilder;
        }

        @Override
        public BerTlvBuilder addInnerTlv(BerTlv innerTlv) {
            if (mBerTlvBuilder != null) {
                mBerTlvBuilder.addInnerTlv(innerTlv);
            }
            return mBerTlvBuilder;
        }

        @Override
        public BerTlv build() {
            return mBerTlvBuilder.build();
        }
    }

    public static class Primitive implements Tlv, PrimitiveTlv {
        BerTlvBuilder mBerTlvBuilder;

        Primitive(BerTlvBuilder berTlvBuilder) {
            mBerTlvBuilder = berTlvBuilder;
        }

        public static Primitive create(String hexTagBytes) {
            byte[] tagBytes = ByteUtils.hexStringToByteArray(hexTagBytes);
            if (tagBytes == null)
                return null;
            return create(tagBytes);
        }

        public static Primitive create(byte[] tagBytes) {
            BerTag berTag = new BerTag(tagBytes);
            if (!berTag.isConstructed()) {
                BerTlvBuilder berTlvBuilder = new BerTlvBuilder(berTag);
                Primitive primitive = new Primitive(berTlvBuilder);
                return primitive;
            }
            return null;
        }

        public static Primitive create(BerTagBase tagBase) {
            BerTag berTag = new BerTag(tagBase.getTagBytes());
            if (!berTag.isConstructed()) {
                BerTlvBuilder berTlvBuilder = new BerTlvBuilder(berTag);
                Primitive primitive = new Primitive(berTlvBuilder);
                return primitive;
            }
            return null;
        }

        @Override
        public void setTag(byte[] tagBytes) {
            BerTag berTag = new BerTag(tagBytes);
            if (berTag.isConstructed())
                throw new RuntimeException("This tag is not primitive");
            mBerTlvBuilder.setBerTag(new BerTag(tagBytes));
        }

        @Override
        public void setTag(String tagBytesHexString) {
            byte[] tagBytes = ByteUtils.hexStringToByteArray(tagBytesHexString);
            if (tagBytes == null)
                throw new RuntimeException(tagBytesHexString + " is not a valid Hex String");
            setTag(tagBytes);
        }

        @Override
        public <T extends Enum<T> & BerTagBase> void setTag(T enumTagBase) {
            setTag(enumTagBase.getTagBytes());
        }

        @Override
        public BerTlvBuilder setValue(byte[] value) {
            if (mBerTlvBuilder != null) {
                mBerTlvBuilder.setValue(value);
            }
            return mBerTlvBuilder;
        }

        @Override
        public BerTlvBuilder setValue(String valueAsHexString) {
            if (mBerTlvBuilder != null) {
                mBerTlvBuilder.setValue(valueAsHexString);
            }
            return mBerTlvBuilder;
        }

        @Override
        public BerTlv build() {
            return mBerTlvBuilder.build();
        }
    }

    interface Tlv {
        void setTag(byte[] tagBytes);
        void setTag(String tagBytesHexString);
        <T extends Enum<T> & BerTagBase> void setTag(T enumTagBase);
        BerTlv build();
    }

    interface ConstructedTlv {
        BerTlvBuilder addInnerTlv(BerTlv innerTlv);
        BerTlvBuilder setInnerTlvList(List<BerTlv> innerTlvList);
    }

    interface PrimitiveTlv {
        BerTlvBuilder setValue(byte[] value);
        BerTlvBuilder setValue(String valueAsHexString);
    }
}
