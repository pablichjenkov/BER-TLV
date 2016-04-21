package com.imobile3.toolkit.ber.test;

import java.util.ArrayList;
import java.util.List;
import com.imobile3.toolkit.ber.BerTlv;
import com.imobile3.toolkit.ber.ByteUtils;
import com.imobile3.toolkit.ber.TlvContainer;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TlvDecoderTest {

    @Test
    public void parseSimplePrimitiveTlv() throws Exception {
        String tlvHexInput = "0103010203";

        List<BerTlv> tlvList = TlvContainer.parse(tlvHexInput);
        if (tlvList != null && tlvList.size() > 0) {
            BerTlv berTlv = tlvList.get(0);

            //berTlv.logPretty();
            assertEquals(tlvHexInput, ByteUtils.byteArrayToHexString(berTlv.asByteArray()));
        }
    }

    @Test
    public void parseSimplePrimitive_TlvWithZeroLength() throws Exception {
        String tlvHexInput1 = "0103010203";
        String tlvHexInput2 = "0100";

        List<BerTlv> tlvList = TlvContainer.parse(tlvHexInput1 + tlvHexInput2);
        if (tlvList != null && tlvList.size() > 1) {
            BerTlv berTlv1 = tlvList.get(0);
            BerTlv berTlv2 = tlvList.get(1);
            //berTlv1.logPretty();
            //berTlv2.logPretty();
            assertEquals(tlvHexInput1 + tlvHexInput2, ByteUtils.byteArrayToHexString(berTlv1.asByteArray())
                    + ByteUtils.byteArrayToHexString(berTlv2.asByteArray()));
        }
    }

    @Test
    public void parseListPrimitiveTlv() throws Exception {
        List<String> inputList = new ArrayList<>();
        inputList.add("0103010203");
        inputList.add("0203040506");
        inputList.add("0303070809");
        inputList.add("04030a0b0c");

        String inputListString = "";
        for(String s : inputList)
            inputListString = inputListString.concat(s);

        List<BerTlv> tlvList = TlvContainer.parse(inputListString);

        if (tlvList != null && tlvList.size() > 0) {
            for(int i=0; i<tlvList.size(); i++) {
                //tlvList.get(i).logPretty();
                assertEquals(inputList.get(i), ByteUtils.byteArrayToHexString(tlvList.get(i).asByteArray()));
            }
        }
    }

    @Test
    public void parseSimpleConstructedTlv() throws Exception {
        String tlvHexInput = "22190103010203210d010301020303030405060501010303040506";

        List<BerTlv> tlvList = TlvContainer.parse(tlvHexInput);
        if (tlvList != null && tlvList.size() > 0) {
            BerTlv berTlv = tlvList.get(0);

            //berTlv.logPretty();
            assertEquals(tlvHexInput, ByteUtils.byteArrayToHexString(berTlv.asByteArray()));
        }

    }

    @Test
    public void parseSimpleConstructedTlv_WithTlvZeroLength() throws Exception {
        String tlvHexInput = "221b0103010203210d0103010203030304050605010103030405060700";

        List<BerTlv> tlvList = TlvContainer.parse(tlvHexInput);
        if (tlvList != null && tlvList.size() > 0) {
            BerTlv berTlv = tlvList.get(0);

            berTlv.logPretty();
            assertEquals(tlvHexInput, ByteUtils.byteArrayToHexString(berTlv.asByteArray()));
        }

    }

}
