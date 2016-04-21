# BER-TLV
First in class BER-TLV parser for java lovers. 

TLV is a lightweight and very efficient data serialization format. Less verbose than XML or JSON, however, less flexible
when it comes to predefined schemas and data types. TLV is still very popular in the payment industry where it is used in
cards and transaction processing. It has also utility in IoT related projects, due to its lightweight and powerfull
mechanism to encode data.
For more info 
<BR>
[wikipedia](https://en.wikipedia.org/wiki/X.690)
<BR>
[emvlab](http://www.emvlab.org/)

**How to use**
<BR>
*ENCODING*
<BR>
Create a primitive TLV object.
```
  BerTlvBuilder.Primitive primitiveTlv = BerTlvBuilder.Primitive.create("01");
  primitiveTlv.setValue("010203");
  BerTlv berTlv = primitiveTlv.build();
  berTlv.logPretty();
```

Create a constructed TLV object
```
  // Notice the primitive TLV object is initialized with a BER-Primitive TAG.
  // Otherwise BerTlv.setValue(...) will throw an exception.
  BerTlvBuilder.Primitive primitiveTlv = BerTlvBuilder.Primitive.create("01");
  primitiveTlv.setValue("010203");
  BerTlv berTlv1 = primitiveTlv.build();

  primitiveTlv.setTag("03");
  primitiveTlv.setValue("040506");
  BerTlv berTlv2 = primitiveTlv.build();

  // Notice the enclosing TLV object is initialized with a BER-Constructed TAG.
  // Otherwise BerTlv.addInnerTlv(...) will throw an exception.
  BerTlvBuilder.Constructed constructedTlv = BerTlvBuilder.Constructed.create("21");
  constructedTlv.addInnerTlv(berTlv1);
  constructedTlv.addInnerTlv(berTlv2);
  BerTlv berTlv = constructedTlv.build();
  
  berTlv.logPretty();
```


*DECODING*
<BR>
Decode many primitive TLV objects
```
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
          tlvList.get(i).logPretty();
      }
  }
```

Decode to a constructed TLV object
```
  String tlvHexInput = "22190103010203210d010301020303030405060501010303040506";
  List<BerTlv> tlvList = TlvContainer.parse(tlvHexInput);
  if (tlvList != null && tlvList.size() > 0) {
      BerTlv berTlv = tlvList.get(0);
      berTlv.logPretty();
  }
```

You can create your own custom Tags beyond the EMV registered TAG. For this create an enum class that implements the
BerTagBase interface. See below

```
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

//Now use you custom tag enum like this

BerTlvBuilder.Primitive primitiveTlvBuilder1 = BerTlvBuilder.Primitive.create(CustomTag.PRIMITIVE_TAG_1);
primitiveTlvBuilder1.setValue("0x010203");
BerTlv berTlv1 = primitiveTlvBuilder1.build();
//berTlv1.logPretty();

BerTlvBuilder.Primitive primitiveTlvBuilder2 = BerTlvBuilder.Primitive.create(CustomTag.PRIMITIVE_TAG_3);
primitiveTlvBuilder2.setValue("0x040506");
BerTlv berTlv2 = primitiveTlvBuilder2.build();
//berTlv2.logPretty();

BerTlvBuilder.Constructed constructedTlvBuilder1 = BerTlvBuilder.Constructed.create(CustomTag.CONSTRUCTED_TAG_2);
constructedTlvBuilder1.addInnerTlv(berTlv1);
constructedTlvBuilder1.addInnerTlv(berTlv2);
BerTlv berTlv3 = constructedTlvBuilder1.build();
//berTlv3.logPretty();

//System.out.println(ByteUtils.byteArrayToHexString(berTlv3.asByteArray()));

BerTlvBuilder.Constructed constructedTlvBuilder2 = BerTlvBuilder.Constructed.create(CustomTag.CONSTRUCTED_TAG_4);
constructedTlvBuilder2.addInnerTlv(berTlv2);
constructedTlvBuilder2.addInnerTlv(berTlv1);
constructedTlvBuilder2.addInnerTlv(berTlv3);
BerTlv berTlv4 = constructedTlvBuilder2.build();
//berTlv4.logPretty();

BerTlvBuilder.Constructed constructedTlvBuilder3 = BerTlvBuilder.Constructed.create(CustomTag.CONSTRUCTED_TAG_6);
constructedTlvBuilder3.addInnerTlv(berTlv1);
constructedTlvBuilder3.addInnerTlv(berTlv3);
constructedTlvBuilder3.addInnerTlv(berTlv4);
BerTlv berTlv5 = constructedTlvBuilder3.build();
berTlv5.logPretty();
```
For more use cases you can check the junit module. There you will see other possible scenarios.


#######!Feel free to download, modify it and don't forget to pull request.

#### Enjoy
