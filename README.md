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
  //Create a builder and it will create your TLV object.

  BerTlvBuilder.Primitive primitiveTlvBuilder = BerTlvBuilder.Primitive.create("01");
  primitiveTlvBuilder.setValue("010203");
  BerTlv berTlv = primitiveTlvBuilder.build();
  berTlv.logPretty();
```

Create a constructed TLV object
```
  // Notice the primitive TLV object is initialized with a BER-Primitive TAG.
  // Otherwise BerTlv.setValue(...) will throw an exception.

  BerTlvBuilder.Primitive primitiveTlvBuilder = BerTlvBuilder.Primitive.create("01");
  primitiveTlvBuilder.setValue("010203");
  BerTlv berTlv1 = primitiveTlvBuilder.build();

  //Reuse the already created builder.

  primitiveTlvBuilder.setTag("03");
  primitiveTlvBuilder.setValue("040506");
  BerTlv berTlv2 = primitiveTlvBuilder.build();

  // Now put them into a Constructed TLV object.
  // Notice the enclosing TLV object is initialized with a BER-Constructed TAG.
  // Otherwise BerTlv.addInnerTlv(...) will throw an exception.

  BerTlvBuilder.Constructed constructedTlvBuilder = BerTlvBuilder.Constructed.create("21");
  constructedTlvBuilder.addInnerTlv(berTlv1);
  constructedTlvBuilder.addInnerTlv(berTlv2);
  BerTlv berTlv = constructedTlvBuilder.build();
  
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
//berTlv4.logPretty();

constructedTlvBuilder.setTag(CustomTag.CONSTRUCTED_TAG_6);
constructedTlvBuilder.addInnerTlv(berTlv1);
constructedTlvBuilder.addInnerTlv(berTlv3);
constructedTlvBuilder.addInnerTlv(berTlv4);
BerTlv berTlv5 = constructedTlvBuilder.build();
berTlv5.logPretty();
```
For more use cases you can check the junit module. There you will see other possible scenarios.


#######!Feel free to download, modify it and don't forget to pull request.

#### Enjoy
