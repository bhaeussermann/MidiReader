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

public class StateChangeMidiEvent extends MidiEvent
{
    public static final int CONTROL_CHANGE_BANK_SELECT = 0,CONTROL_CHANGE_MODULATION = 1,CONTROL_CHANGE_BREATH_CONTROLLER = 3,CONTROL_CHANGE_FOOT_CONTROLLER = 4,
            CONTROL_CHANGE_PORTAMENTO_TIME = 5,CONTROL_CHANGE_DATA_ENTRY_MSB = 6,CONTROL_CHANGE_VOLUME = 7,CONTROL_CHANGE_BALANCE = 8,CONTROL_CHANGE_PAN = 10,
            CONTROL_CHANGE_EXPRESSION = 11,CONTROL_CHANGE_EFFECT_CONTROLLER_1 = 12,CONTROL_CHANGE_EFFECT_CONTROLLER_2 = 13,CONTROL_CHANGE_DAMPER_PEDAL = 64,
            CONTROL_CHANGE_PORTAMENTO_ON_OFF = 65,CONTROL_CHANGE_SOSTENUTO_ON_OFF = 66,CONTROL_CHANGE_SOFT_PEDAL_ON_OFF = 67,CONTROL_CHANGE_LEGATO_FOOT_SWITCH = 68,
            CONTROL_CHANGE_HOLD_2 = 69,CONTROL_CHANGE_SOUND_CONTROLLER_1 = 70,CONTROL_CHANGE_SOUND_CONTROLLER_2 = 71,CONTROL_CHANGE_SOUND_CONTROLLER_3 = 72,
            CONTROL_CHANGE_SOUND_CONTROLLER_4 = 73,CONTROL_CHANGE_SOUND_CONTROLLER_5 = 74,CONTROL_CHANGE_SOUND_CONTROLLER_6 = 75,CONTROL_CHANGE_SOUND_CONTROLLER_7 = 76,
            CONTROL_CHANGE_SOUND_CONTROLLER_8 = 77,CONTROL_CHANGE_SOUND_CONTROLLER_9 = 78,CONTROL_CHANGE_SOUND_CONTROLLER_10 = 79,CONTROL_CHANGE_PORTAMENTO = 84,
            CONTROL_CHANGE_EFFECT_1 = 91,CONTROL_CHANGE_EFFECT_2 = 92,CONTROL_CHANGE_EFFECT_3 = 93,CONTROL_CHANGE_EFFECT_4 = 94,CONTROL_CHANGE_EFFECT_5 = 95,
            CONTROL_CHANGE_DATA_INCREMENT = 96,CONTROL_CHANGE_DATA_DECREMENT = 97,CONTROL_CHANGE_ALL_SOUND_OFF = 120,CONTROL_CHANGE_RESET_ALL_CONTROLLERS = 121,
            CONTROL_CHANGE_LOCAL_ON_OFF = 122,CONTROL_CHANGE_ALL_NOTES_OFF = 123,CONTROL_CHANGE_OMNI_MODE_OFF = 124,CONTROL_CHANGE_OMNI_MODE_ON = 125,
            CONTROL_CHANGE_MONO_MODE = 126,CONTROL_CHANGE_POLY_MODE = 127;
    
    public enum StateChangeType { CONTROL_CHANGE, PROGRAM_CHANGE, CHANNEL_AFTER_TOUCH, PITCH_WHEEL_CHANGE }
    
    private int channelNumber;
    private StateChangeType stateChangeType;
    private int value1,value2;
    
    public StateChangeMidiEvent(int deltaTime,long totalTime,int channelNumber,StateChangeType stateChangeType,int value1,int value2)
    {
        super(deltaTime,totalTime);
        this.channelNumber = channelNumber;
        this.stateChangeType = stateChangeType;
        this.value1 = value1;
        this.value2 = value2;
    }
    
    public StateChangeMidiEvent(int deltaTime,long totalTime,int channelNumber,StateChangeType stateChangeType,int value)
    {
        this(deltaTime,totalTime,channelNumber,stateChangeType,value,0);
    }
    
    public int getChannelNumber()
    {
        return channelNumber;
    }
    
    public StateChangeType getStateChangeType()
    {
        return stateChangeType;
    }
    
    public int getValue1()
    {
        return value1;
    }
    
    public int getValue2()
    {
        return value2;
    }
    
    public String toString()
    {
        return super.toString()+"[channelNumber="+channelNumber+";stateChangeType="+stateChangeType+";value1="+value1+";value2="+value2+']';
    }
}
