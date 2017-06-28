package com.lts.core.ruptela;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.jboss.logging.Logger;

public class IOElement {
private static final Logger logger = Logger.getLogger(IOElement.class);
private int eventId = 0;
private HashMap<Integer,BigInteger> ioMap = new HashMap<Integer,BigInteger>();
private static final Set<Integer> signetValues = new HashSet<Integer>(Arrays.asList(
     new Integer[] {301,32}
));
private static final Set<Integer> compositeValues = new HashSet<Integer>(Arrays.asList(
     new Integer[] {123,124,125,126,127,128,129}
));
public IOElement(){

}

public int getEventId(){
    return this.eventId;
}
/**
 * ID are changed regarding on the input table in database
 * 
 * 
 * @param id that we want to change
 * @return changed id
 */
private int changeInputId(int id){
    //if id == 6, it's modem temperature,
    //returning 301, because 301 is database id, for this input
    if(id == 6){
        return 301;
    }
    return id;
}

private boolean itIsSignedValue(int id){
    if(signetValues.contains(id)){
        return true;
    }
    return false;
}


public void read(DataInputStream in) throws IOException{
    this.eventId = in.readUnsignedByte();
    int howMuchOneByteInputs = in.readUnsignedByte();
    for(int i=0;i!=howMuchOneByteInputs;i++){
        int id = changeInputId(in.readUnsignedByte());
        byte[] buffer = new byte[1];
        in.read(buffer);
        BigInteger b = null;
        if(!itIsSignedValue(id)){
            b = new BigInteger(1,buffer);
        }else{
           Short s1 = new Short(buffer[0]);
           b = new BigInteger(s1.toString());
        }
        ioMap.put(id, b);
    }

    int howMuchTwoBytesInputs = in.readUnsignedByte();
    for(int i=0;i!=howMuchTwoBytesInputs;i++){
        int id = in.readUnsignedByte();
        byte[] buffer = new byte[2];
        in.read(buffer);
        BigInteger b = new BigInteger(1,buffer);
        ioMap.put(id, b);
    }

    int howMuchFourBytesInputs = in.readUnsignedByte();
    for(int i=0;i!=howMuchFourBytesInputs;i++){
        int id = in.readUnsignedByte();
        byte[] buffer = new byte[4];
        in.read(buffer);
        BigInteger b = new BigInteger(1,buffer);
        ioMap.put(id, b);
    }

    int howMuchEightBytesInputs = in.readUnsignedByte();
    for(int i=0;i!=howMuchEightBytesInputs;i++){
        int id = in.readUnsignedByte();
        byte[] buffer = new byte[8];
        in.read(buffer);
        BigInteger b = new BigInteger(1,buffer);
        ioMap.put(id, b);
    }
}
    @Override
    public String toString(){
        String _return = "";
        for (Integer id : ioMap.keySet()) {
            logger.info("{"+id + "," + ioMap.get(id)+"}");
                if(!itsCompositeValue(id)){
                    _return += "{"+id + "," + ioMap.get(id)+"},";
                }else{
                    _return += getCompositeValue(id);
                }
        }
        if(_return.length()!=0){
            return _return.substring(0,_return.length()-1);
        }
        else return "";
    }
    public int size(){
        return ioMap.size();
    }

    private String getCompositeValue(int _id){
        int id = 0;
        String value = "";
        if(_id == 123){ //Vehicle identification number
            value += getAsciiEncodedText(ioMap.get(_id).toString(16)); //123
            value += getAsciiEncodedText(ioMap.get(_id+1).toString(16));//124
            value += getAsciiEncodedText(ioMap.get(_id+2).toString(16));//125
            id = 323;
        }
        if(_id == 126){
            value += getAsciiEncodedText(ioMap.get(_id).toString(16)); //123
            value += getAsciiEncodedText(ioMap.get(_id+1).toString(16));//124
            id = 324;
        }
        if(_id == 128){
            value += getAsciiEncodedText(ioMap.get(_id).toString(16)); //123
            value += getAsciiEncodedText(ioMap.get(_id+1).toString(16));//124
            id = 325;
        }
        if(id == 0){
            return "";
        }
        return "{"+id + "," + value+"},";
    }
	
    /*
     Returning ASCII VALUE, converted to numeric, because in database inputs, can be only numeric.
    */	
    private String getAsciiEncodedText(String hexString){
    String value_ = "";
    if(hexString.length() == 1 || hexString == null || hexString.isEmpty()){
        return "0";
    }
    for(int i=0;i<hexString.length();i=i+2){
        Integer intas = Integer.parseInt((hexString.substring(i, i+2)),16);
        if(intas == 0){
            continue;
        }
        String v = "";
        if(intas.toString().length()==2){
            value_ += "0"+intas.toString();
        }else if(intas.toString().length()==1){
            value_ += "00"+intas.toString();
        }
        else{
            value_ += intas.toString();
        }
    }
        return value_;
    }

    private boolean itsCompositeValue(int id){
        if(compositeValues.contains(id)){
            return true;
        }
            return false;
    }
}
