package com.lts.core.ruptela;

import java.io.DataInputStream;
import java.io.IOException;

import org.jboss.logging.Logger;

public class GpsElement {
private static final Logger logger = Logger.getLogger(FmPro3Data.class);
private int timestamp = 0;
private byte timestampExtension = 0;
private byte priority = 0;
private double longitude=0;
private double latitude = 0;
private double altitude = 0;
private double angle = 0;
private byte satelites = 0;
private int speed = 0;
private double hdop = 0;
private byte gsmSignal = 0;
private IOElement io = new IOElement();

    public void read(DataInputStream in) throws IOException{
        this.timestamp = in.readInt();
        this.timestampExtension = in.readByte();
        this.priority = in.readByte();
        this.setLongitude(in.readInt());
        this.setLatitude(in.readInt());
        this.setAltitude(in.readShort());
        this.setAngle(in.readByte(),in.readByte());
        this.satelites = in.readByte();
        logger.info("satelites: "+this.hdop);
        this.speed = in.readShort();
        logger.info("speed: "+this.hdop);
        this.setHdop(in.readByte());
        logger.info("hdop: "+this.hdop);
      //  this.setGsmSignal(in.readByte());
        this.io.read(in);
   }

    @Override
    public String toString(){
        String data = new String();
        data += "\nTimestamp: "+this.timestamp;
        data += "\nTimestamp Extension : "+this.timestampExtension;
        data += "\nPriority: "+this.priority;
        data += "\nLongitude: "+this.longitude;
        data += "\nLatitude: "+this.latitude;
        data += "\nAltitude: "+this.altitude;
        data += "\nAngle: "+this.angle;
        data += "\nSatelites: "+this.satelites;
        data += "\nSpeed: "+this.speed;
        data += "\nHDOP: "+this.hdop;
        return data;
    }

    public byte getGsmSignal() {
        return gsmSignal;
    }

    public void setGsmSignal(byte gsmSignal) {
        this.gsmSignal = gsmSignal;
    }

    public String getAltitude() {
        return new Double(altitude).toString();
    }

    public void setAltitude(short altitude) {
        this.altitude = (double)altitude/10;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(byte first,byte second) {
        char anUnsignedShort = 0;
        int firstByte = (0x000000FF & ((int)first));
        int secondByte = (0x000000FF & ((int)second));
	anUnsignedShort  = (char) (firstByte << 8 | secondByte);
        int _angle = (int)anUnsignedShort;
        this.angle = (double)_angle/100;
    }

    public double getHdop() {
        return hdop;
    }

    public void setHdop(byte hdop) {
        this.hdop = (double)hdop/10;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = (double)latitude/10000000;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = (double)longitude/10000000;
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public String getSatelites() {
        return new Integer((int)satelites).toString();
    }

    public void setSatelites(byte satelites) {
        this.satelites = satelites;
    }

    public double getSpeed() {
        return (double)speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public long getTimestamp() {
        long t = (((long)timestamp*(long)1000)+(long)this.timestampExtension);
        return t;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public byte getTimestampExtension() {
        return timestampExtension;
    }

    public void setTimestampExtension(byte timestampExtension) {
        this.timestampExtension = timestampExtension;
    }

    public String getInputs(){
        String inputs = new String();
        String ioString = io.toString();
        if(!ioString.equalsIgnoreCase("")){
            inputs = "{{300,"+this.hdop+"},"+ioString+"}";
        }else{
            inputs = "{{300,"+this.hdop+"}}";
        }
        return inputs;
    }

    public IOElement getIOElement(){
        return this.io;
    }

}
