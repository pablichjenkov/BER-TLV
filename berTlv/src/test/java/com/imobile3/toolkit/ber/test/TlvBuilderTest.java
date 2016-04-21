package com.imobile3.toolkit.ber.test;

import com.imobile3.toolkit.ber.BerTlv;
import com.imobile3.toolkit.ber.BerTlvBuilder;
import com.imobile3.toolkit.ber.ByteUtils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TlvBuilderTest {
    @Test
    public void createPrimitiveTag() throws Exception {

        String tlvHexExpected = "0103010203";

        String tagHexString = "01";
        BerTlvBuilder.Primitive primitiveTlv = BerTlvBuilder.Primitive.create(tagHexString);
        primitiveTlv.setValue("010203");
        BerTlv berTlv = primitiveTlv.build();

        //berTlv.logPretty();
        String tlvHexResult = ByteUtils.byteArrayToHexString(berTlv.asByteArray());

        assertEquals(tlvHexExpected, tlvHexResult);

    }

    @Test
    public void createConstructedTag() throws Exception {

        String tlvHexExpected = "210a01030102030303040506";

        String tag1HexString = "01";
        BerTlvBuilder.Primitive primitiveTlv = BerTlvBuilder.Primitive.create(tag1HexString);
        primitiveTlv.setValue("010203");
        BerTlv berTlv1 = primitiveTlv.build();

        String tag2HexString = "03";
        primitiveTlv.setTag(tag2HexString);
        primitiveTlv.setValue("040506");
        BerTlv berTlv2 = primitiveTlv.build();

        String tag3HexString = "21";
        BerTlvBuilder.Constructed constructedTlv = BerTlvBuilder.Constructed.create(tag3HexString);
        constructedTlv.addInnerTlv(berTlv1);
        constructedTlv.addInnerTlv(berTlv2);
        BerTlv berTlv = constructedTlv.build();

        //berTlv.logPretty();
        String tlvHexResult = ByteUtils.byteArrayToHexString(berTlv.asByteArray());

        assertEquals(tlvHexExpected, tlvHexResult);

    }

    @Test
    public void createPrimitiveTag_TlvLengthZero() throws Exception {
        String tlvHexExpected = "0100";

        String tagHexString = "01";
        BerTlvBuilder.Primitive primitiveTlv = BerTlvBuilder.Primitive.create(tagHexString);
        primitiveTlv.setValue(new byte[0]);
        BerTlv berTlv = primitiveTlv.build();

        //berTlv.logPretty();
        String tlvHexResult = ByteUtils.byteArrayToHexString(berTlv.asByteArray());

        assertEquals(tlvHexExpected, tlvHexResult);
    }

    @Test
    public void createConstructedTag_EnclosingTlvWithZeroLength() throws Exception {
        String tlvHexExpected = "210c010301020303030405060500";

        String tag1HexString = "01";
        BerTlvBuilder.Primitive primitiveTlv = BerTlvBuilder.Primitive.create(tag1HexString);
        primitiveTlv.setValue("010203");
        BerTlv berTlv1 = primitiveTlv.build();

        String tag2HexString = "03";
        primitiveTlv.setTag(tag2HexString);
        primitiveTlv.setValue("040506");
        BerTlv berTlv2 = primitiveTlv.build();

        String tag3HexString = "05";
        primitiveTlv.setTag(tag3HexString);
        primitiveTlv.setValue(new byte[0]);
        BerTlv berTlv3 = primitiveTlv.build();

        String tag4HexString = "21";
        BerTlvBuilder.Constructed constructedTlv = BerTlvBuilder.Constructed.create(tag4HexString);
        constructedTlv.addInnerTlv(berTlv1);
        constructedTlv.addInnerTlv(berTlv2);
        constructedTlv.addInnerTlv(berTlv3);
        BerTlv berConstructTlv1 = constructedTlv.build();

        //berTlv.logPretty();
        String tlvHexResult = ByteUtils.byteArrayToHexString(berConstructTlv1.asByteArray());

        assertEquals(tlvHexExpected, tlvHexResult);
    }

    @Test
    public void createConstructedTag_EnclosingConstructedTag() throws Exception {
        String tlvHexExpected = "22190103010203210d010301020303030405060501010303040506";

        BerTlvBuilder.Primitive primitiveTlv = BerTlvBuilder.Primitive.create("01");
        primitiveTlv.setValue("010203");
        BerTlv berTlv1 = primitiveTlv.build();

        primitiveTlv.setTag("03");
        primitiveTlv.setValue("040506");
        BerTlv berTlv2 = primitiveTlv.build();

        primitiveTlv.setTag("05");
        primitiveTlv.setValue("01");
        BerTlv berTlv3 = primitiveTlv.build();

        String tag4HexString = "21";
        BerTlvBuilder.Constructed constructedTlv = BerTlvBuilder.Constructed.create(tag4HexString);
        constructedTlv.addInnerTlv(berTlv1);
        constructedTlv.addInnerTlv(berTlv2);
        constructedTlv.addInnerTlv(berTlv3);
        BerTlv berConstructTlv1 = constructedTlv.build();

        constructedTlv.setTag("22");
        constructedTlv.addInnerTlv(berTlv1);
        constructedTlv.addInnerTlv(berConstructTlv1);
        constructedTlv.addInnerTlv(berTlv2);
        BerTlv berConstructTlv2 = constructedTlv.build();

        //berConstructTlv2.logPretty();
        String tlvHexResult = ByteUtils.byteArrayToHexString(berConstructTlv2.asByteArray());

        assertEquals(tlvHexExpected, tlvHexResult);
    }

    @Test
    public void createTlvObject_WithCustomTag() throws Exception {
        BerTlvBuilder.Primitive primitiveTlvBuilder = BerTlvBuilder.Primitive.create(CustomTag.PRIMITIVE_TAG_1);
        primitiveTlvBuilder.setValue("0x010203");
        BerTlv berTlv1 = primitiveTlvBuilder.build();
        //berTlv1.logPretty();

        primitiveTlvBuilder.setTag(CustomTag.PRIMITIVE_TAG_3);
        primitiveTlvBuilder.setValue("0x040506");
        BerTlv berTlv2 = primitiveTlvBuilder.build();
        //berTlv2.logPretty();

        BerTlvBuilder.Constructed constructedTlvBuilder = BerTlvBuilder.Constructed.create(CustomTag.CONSTRUCTED_TAG_2);
        constructedTlvBuilder.addInnerTlv(berTlv1);
        constructedTlvBuilder.addInnerTlv(berTlv2);
        BerTlv berTlv3 = constructedTlvBuilder.build();
        //berTlv3.logPretty();

        //System.out.println(ByteUtils.byteArrayToHexString(berTlv3.asByteArray()));

        constructedTlvBuilder.setTag(CustomTag.CONSTRUCTED_TAG_4);
        constructedTlvBuilder.addInnerTlv(berTlv2);
        constructedTlvBuilder.addInnerTlv(berTlv1);
        constructedTlvBuilder.addInnerTlv(berTlv3);
        BerTlv berTlv4 = constructedTlvBuilder.build();
        berTlv4.logPretty();
    }

}