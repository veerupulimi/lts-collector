package com.lts.core.ruptela;

import java.io.DataInputStream;
import java.io.IOException;

public class GpsData {
private byte recordLeftFlag = 0;
private byte numberOfrecords = 0;
private GpsElement[] gpsElements = null;

    public GpsElement[] getGpsElements() {
        return gpsElements;
    }

    private void addGpsElement(GpsElement element, int index){
        this.gpsElements[index] = element;
    }


    public void setGpsElements(GpsElement[] gpsElements) {
        this.gpsElements = gpsElements;
    }

    public byte getNumberOfrecords() {
        return numberOfrecords;
    }

    public void setNumberOfrecords(byte numberOfrecords) {
        this.numberOfrecords = numberOfrecords;
    }

    public byte getRecordLeftFlag() {
        return recordLeftFlag;
    }

    public void setRecordLeftFlag(byte recordLeftFlag) {
        this.recordLeftFlag = recordLeftFlag;
    }

    public void read(DataInputStream in) throws IOException{
        this.recordLeftFlag = in.readByte();
        this.numberOfrecords = in.readByte();
        this.gpsElements = new GpsElement[numberOfrecords];
        for(int i=0;i!=this.numberOfrecords;i++){
            GpsElement element = new GpsElement();
            element.read(in);
            this.addGpsElement(element, i);
        }
    }

    @Override
    public String toString(){
        String data = new String();
        data += "\nRecord Left flag: "+this.recordLeftFlag;
        data += "\nNumber Of record: "+this.numberOfrecords;
        for(int i=0;i!=gpsElements.length;i++){
            data += gpsElements[i].toString();
        }
        return data;
    }

}
