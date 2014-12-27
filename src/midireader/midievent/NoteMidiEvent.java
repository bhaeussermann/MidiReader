//Copyright (c) 2014 Bernhard Haeussermann
//
//Permission is hereby granted, free of charge, to any person obtaining a copy
//of this software and associated documentation files (the "Software"), to deal
//in the Software without restriction, including without limitation the rights
//to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//copies of the Software, and to permit persons to whom the Software is
//furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice shall be included in all
//copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//SOFTWARE.

package midireader.midievent;

import midireader.Channel;

public class NoteMidiEvent extends MidiEvent
{
    public static final int NOTE_NUMBER_C0 = 0, NOTE_NUMBER_CS0 = 1, NOTE_NUMBER_D0 = 2, NOTE_NUMBER_DS0 = 3, NOTE_NUMBER_E0 = 4, NOTE_NUMBER_F0 = 5, NOTE_NUMBER_FS0 = 6, NOTE_NUMBER_G0 = 7, NOTE_NUMBER_GS0 = 8, NOTE_NUMBER_A0 = 9, NOTE_NUMBER_AS0 = 10, NOTE_NUMBER_B0 = 11,
        NOTE_NUMBER_C1 = 12, NOTE_NUMBER_CS1 = 13, NOTE_NUMBER_D1 = 14, NOTE_NUMBER_DS1 = 15, NOTE_NUMBER_E1 = 16, NOTE_NUMBER_F1 = 17, NOTE_NUMBER_FS1 = 18, NOTE_NUMBER_G1 = 19, NOTE_NUMBER_GS1 = 20, NOTE_NUMBER_A1 = 21, NOTE_NUMBER_AS1 = 22, NOTE_NUMBER_B1 = 23,
        NOTE_NUMBER_C2 = 24, NOTE_NUMBER_CS2 = 25, NOTE_NUMBER_D2 = 26, NOTE_NUMBER_DS2 = 27, NOTE_NUMBER_E2 = 28, NOTE_NUMBER_F2 = 29, NOTE_NUMBER_FS2 = 30, NOTE_NUMBER_G2 = 31, NOTE_NUMBER_GS2 = 32, NOTE_NUMBER_A2 = 33, NOTE_NUMBER_AS2 = 34, NOTE_NUMBER_B2 = 35,
        NOTE_NUMBER_C3 = 36, NOTE_NUMBER_CS3 = 37, NOTE_NUMBER_D3 = 38, NOTE_NUMBER_DS3 = 39, NOTE_NUMBER_E3 = 40, NOTE_NUMBER_F3 = 41, NOTE_NUMBER_FS3 = 42, NOTE_NUMBER_G3 = 43, NOTE_NUMBER_GS3 = 44, NOTE_NUMBER_A3 = 45, NOTE_NUMBER_AS3 = 46, NOTE_NUMBER_B3 = 47,
        NOTE_NUMBER_C4 = 48, NOTE_NUMBER_CS4 = 49, NOTE_NUMBER_D4 = 50, NOTE_NUMBER_DS4 = 51, NOTE_NUMBER_E4 = 52, NOTE_NUMBER_F4 = 53, NOTE_NUMBER_FS4 = 54, NOTE_NUMBER_G4 = 55, NOTE_NUMBER_GS4 = 56, NOTE_NUMBER_A4 = 57, NOTE_NUMBER_AS4 = 58, NOTE_NUMBER_B4 = 59,
        NOTE_NUMBER_C5 = 60, NOTE_NUMBER_CS5 = 61, NOTE_NUMBER_D5 = 62, NOTE_NUMBER_DS5 = 63, NOTE_NUMBER_E5 = 64, NOTE_NUMBER_F5 = 65, NOTE_NUMBER_FS5 = 66, NOTE_NUMBER_G5 = 67, NOTE_NUMBER_GS5 = 68, NOTE_NUMBER_A5 = 69, NOTE_NUMBER_AS5 = 70, NOTE_NUMBER_B5 = 71,
        NOTE_NUMBER_C6 = 72, NOTE_NUMBER_CS6 = 73, NOTE_NUMBER_D6 = 74, NOTE_NUMBER_DS6 = 75, NOTE_NUMBER_E6 = 76, NOTE_NUMBER_F6 = 77, NOTE_NUMBER_FS6 = 78, NOTE_NUMBER_G6 = 79, NOTE_NUMBER_GS6 = 80, NOTE_NUMBER_A6 = 81, NOTE_NUMBER_AS6 = 82, NOTE_NUMBER_B6 = 83,
        NOTE_NUMBER_C7 = 84, NOTE_NUMBER_CS7 = 85, NOTE_NUMBER_D7 = 86, NOTE_NUMBER_DS7 = 87, NOTE_NUMBER_E7 = 88, NOTE_NUMBER_F7 = 89, NOTE_NUMBER_FS7 = 90, NOTE_NUMBER_G7 = 91, NOTE_NUMBER_GS7 = 92, NOTE_NUMBER_A7 = 93, NOTE_NUMBER_AS7 = 94, NOTE_NUMBER_B7 = 95,
        NOTE_NUMBER_C8 = 96, NOTE_NUMBER_CS8 = 97, NOTE_NUMBER_D8 = 98, NOTE_NUMBER_DS8 = 99, NOTE_NUMBER_E8 = 100, NOTE_NUMBER_F8 = 101, NOTE_NUMBER_FS8 = 102, NOTE_NUMBER_G8 = 103, NOTE_NUMBER_GS8 = 104, NOTE_NUMBER_A8 = 105, NOTE_NUMBER_AS8 = 106, NOTE_NUMBER_B8 = 107,
        NOTE_NUMBER_C9 = 108, NOTE_NUMBER_CS9 = 109, NOTE_NUMBER_D9 = 110, NOTE_NUMBER_DS9 = 111, NOTE_NUMBER_E9 = 112, NOTE_NUMBER_F9 = 113, NOTE_NUMBER_FS9 = 114, NOTE_NUMBER_G9 = 115, NOTE_NUMBER_GS9 = 116, NOTE_NUMBER_A9 = 117, NOTE_NUMBER_AS9 = 118, NOTE_NUMBER_B9 = 119,
        NOTE_NUMBER_C10 = 120, NOTE_NUMBER_CS10 = 121, NOTE_NUMBER_D10 = 122, NOTE_NUMBER_DS10 = 123, NOTE_NUMBER_E10 = 124, NOTE_NUMBER_F10 = 125, NOTE_NUMBER_FS10 = 126, NOTE_NUMBER_G10 = 127;
    private static final double GEOM_R = Math.pow(2,1.0 / 12);

    
    public enum NoteEventType { NOTE_OFF, NOTE_ON, KEY_AFTER_TOUCH };
    
    private Channel channel;
    private NoteEventType noteEventType;
    private int noteNumber,velocity;
    
    public NoteMidiEvent(int deltaTime,long totalTime,Channel channel,NoteEventType noteEventType,int noteNumber,int velocity)
    {
        super(deltaTime,totalTime);
        this.channel = channel;
        this.noteEventType = noteEventType;
        this.noteNumber = noteNumber;
        this.velocity = velocity;
    }
    
    public Channel getChannel()
    {
        return channel;
    }
    
    public NoteEventType getNoteEventType()
    {
        return noteEventType;
    }
    
    public int getNoteNumber()
    {
        return noteNumber;
    }
    
    public static double getNoteFrequency(int noteNumber)
    {
        return 440 * Math.pow(GEOM_R,noteNumber - 69);
    }
    
    public double getNoteFrequency()
    {
        return getNoteFrequency(noteNumber);
    }
    
    public int getVelocity()
    {
        return velocity;
    }
    
    public String toString()
    {
        return super.toString()+"[channel="+channel+";noteNumber="+noteNumber+";noteFrequency="+getNoteFrequency()+";velocity="+velocity+']';
    }
}
