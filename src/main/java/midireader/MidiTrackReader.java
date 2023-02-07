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

package midireader;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import midireader.midievent.MetaMidiEvent;
import midireader.midievent.MidiEvent;
import midireader.midievent.NoteMidiEvent;
import midireader.midievent.StateChangeMidiEvent;
import midireader.midievent.SystemExclusiveMidiEvent;
import midireader.midievent.MetaMidiEvent.MetaEventType;
import midireader.util.FileHelpers;

public class MidiTrackReader implements Iterable<MidiEvent>,Closeable
{
    private static final byte[] EXPECTED_TRACK_START = new byte[] { 0x4D, 0x54, 0x72, 0x6B };
    
    private InputStream stream;
    private int trackNumber;
    private MidiTrackInfo trackInfo = null;
    private MidiEvent previousVoiceCategoryMidiEvent = null;
    private long runningTotalTime = 0;
    
    public MidiTrackReader(InputStream stream,int trackNumber)
    {
        this.stream = stream;
        this.trackNumber = trackNumber;
    }
    
    public MidiTrackInfo getMidiTrackInfo()
    {
        if (trackInfo==null)
            throw new IllegalStateException("readMidiTrackInfo() must be called before getMidiTrackInfo() can be called!");
        return trackInfo;
    }
    
    public Iterator<MidiEvent> iterator()
    {
        if (trackInfo==null)
            throw new IllegalStateException("readMidiTrackInfo() must be called before getMidiTrackInfo() can be called!");
        try
        {
            return new MidiEventIterator();
        } 
        catch (IOException e)
        {
            throw new RuntimeException(e);
        } 
        catch (MidiFileFormatException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public void close() throws IOException
    {
        stream.close();
    }
    
    
    private class MidiEventIterator implements Iterator<MidiEvent>
    {
        private MidiEvent nextMidiEvent;
        
        public MidiEventIterator() throws IOException, MidiFileFormatException
        {
            nextMidiEvent = previousVoiceCategoryMidiEvent;
        }
        
        public boolean hasNext()
        {
            return nextMidiEvent!=null;
        }

        public MidiEvent next()
        {
            MidiEvent currentMidiEvent = nextMidiEvent;
            if (currentMidiEvent instanceof StateChangeMidiEvent)
                updateChannelInfo((StateChangeMidiEvent) currentMidiEvent);
            if ((currentMidiEvent instanceof MetaMidiEvent) && (((MetaMidiEvent) currentMidiEvent).getMetaEventType()==MetaMidiEvent.MetaEventType.TRACK_END))
                nextMidiEvent = null;
            else
            {
                try
                {
                    nextMidiEvent = readNextMidiEvent();
                } 
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                } 
                catch (MidiFileFormatException e)
                {
                    throw new RuntimeException(e);
                }
            }
            return currentMidiEvent;
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
    
    
    protected MidiTrackInfo readMidiTrackInfo(MidiFileInfo midiFileInfo) throws IOException,MidiFileFormatException
    {
        byte[] bytes = new byte[255];
        FileHelpers.readBytes(stream,bytes,EXPECTED_TRACK_START.length);
        if (! FileHelpers.bytesMatch(EXPECTED_TRACK_START,bytes))
            throw new MidiFileFormatException("Invalid file format (bad track-section header)");
        
        trackInfo = new MidiTrackInfo(trackNumber);
        FileHelpers.readBytes(stream,bytes,4);
        trackInfo.setTrackLengthBytes(FileHelpers.getUnsignedLong(bytes,4));
        
        MidiEvent nextMidiEvent;
        do
        {
            nextMidiEvent = readNextMidiEvent();
            if (nextMidiEvent instanceof MetaMidiEvent)
            {
                MetaMidiEvent nextMetaMidiEvent = (MetaMidiEvent) nextMidiEvent;
                switch (nextMetaMidiEvent.getMetaEventType())
                {
                    case TRACK_NAME : { 
                        String trackName = nextMetaMidiEvent.getContentAsString();
                        trackInfo.setTrackName(trackName);
                        if (trackNumber==0)
                            midiFileInfo.setMainTrackName(trackName); 
                        break;
                    }
                    case TIME_SIGNATURE : {
                        byte[] eventContent = nextMetaMidiEvent.getContent();
                        midiFileInfo.setTimeSignature(new TimeSignature(eventContent[0],1<<eventContent[1]));
                        midiFileInfo.setTicksPerMetronomeClick(eventContent[2]);
                        break;
                    }
                    case SET_TEMPO : midiFileInfo.setMicrosecondsPerQuarterNote(FileHelpers.getUnsignedLong(nextMetaMidiEvent.getContent())); break;
                    default : break;
                }
            }
            else if (nextMidiEvent instanceof StateChangeMidiEvent)
                updateChannelInfo((StateChangeMidiEvent) nextMidiEvent);
        }
        while ((! (nextMidiEvent instanceof NoteMidiEvent)) && ((! (nextMidiEvent instanceof MetaMidiEvent)) || (((MetaMidiEvent) nextMidiEvent).getMetaEventType()!=MetaEventType.TRACK_END)));
        return trackInfo;
    }
    
    private void updateChannelInfo(StateChangeMidiEvent midiEvent)
    {
        Channel channel = trackInfo.getChannel(midiEvent.getChannelNumber());
        if (channel==null)
            trackInfo.addChannel(channel = new Channel(trackNumber,midiEvent.getChannelNumber()));
        if ((midiEvent.getStateChangeType()==StateChangeMidiEvent.StateChangeType.CONTROL_CHANGE) && (midiEvent.getValue1()==StateChangeMidiEvent.CONTROL_CHANGE_VOLUME))
            channel.setVolume(midiEvent.getValue2());
    }
    
    private MidiEvent readNextMidiEvent() throws IOException, MidiFileFormatException
    {
        int deltaTime = 0;
        for (int deltaTimeByteCount=0; ; deltaTimeByteCount++)
        {
            int nextByte = stream.read();
            if (nextByte==-1)
                throw new MidiFileFormatException("Unexpected end of file!");
            if (FileHelpers.byteHasMsbSet(nextByte))
                deltaTime = (deltaTime<<7) + (nextByte ^ FileHelpers.MSB_MASK);
            else
            {
                deltaTime = (deltaTime<<7) + nextByte;
                break;
            }
            if (deltaTimeByteCount==4)
                throw new MidiFileFormatException("Delta-time sequence length must not exceed 4");
        }
        runningTotalTime+=deltaTime;
        
        int nextByte = stream.read();
        if (FileHelpers.byteHasMsbSet(nextByte))
        {
            int channelNumber = nextByte & 0x0F;
            int command = nextByte>>4;
            if (command<=10)
            {
                NoteMidiEvent.NoteEventType noteEventType;
                switch (command)
                {
                    case 8 : noteEventType = NoteMidiEvent.NoteEventType.NOTE_OFF; break;
                    case 9 : noteEventType = NoteMidiEvent.NoteEventType.NOTE_ON; break;
                    default : noteEventType = NoteMidiEvent.NoteEventType.KEY_AFTER_TOUCH; break;
                }
                int noteNumber = stream.read();
                int velocity = stream.read();
                return previousVoiceCategoryMidiEvent = new NoteMidiEvent(deltaTime,runningTotalTime,new Channel(trackNumber,channelNumber),noteEventType,noteNumber,velocity);
            }
            else if (command<=14)
            {
                StateChangeMidiEvent.StateChangeType eventType;
                boolean twoValues = false;
                switch (command)
                {
                    case 11 : {
                        eventType = StateChangeMidiEvent.StateChangeType.CONTROL_CHANGE;
                        twoValues = true;
                        break;
                    }
                    case 12 : eventType = StateChangeMidiEvent.StateChangeType.PROGRAM_CHANGE; break;
                    case 13 : eventType = StateChangeMidiEvent.StateChangeType.CHANNEL_AFTER_TOUCH; break;
                    default : {
                        eventType = StateChangeMidiEvent.StateChangeType.PITCH_WHEEL_CHANGE;
                        twoValues = true;
                        break;
                    }
                }
                int value1 = stream.read();
                return previousVoiceCategoryMidiEvent = (twoValues 
                        ? new StateChangeMidiEvent(deltaTime,runningTotalTime,channelNumber,eventType,value1,stream.read()) 
                        : new StateChangeMidiEvent(deltaTime,runningTotalTime,channelNumber,eventType,value1));
            }
            else if (channelNumber==0)
            {
                int b;
                while ((b = stream.read())!=-1)
                    if (b==0xF7)
                        break;
                if (b==-1)
                    throw new MidiFileFormatException("Unexpected end of file");
                previousVoiceCategoryMidiEvent = null;
                return new SystemExclusiveMidiEvent(deltaTime,runningTotalTime);
            }
            else
            {
                int controlChangeCode = stream.read();
                MetaMidiEvent.MetaEventType eventType;
                switch (controlChangeCode)
                {
                    case 0 : eventType = MetaMidiEvent.MetaEventType.TRACK_SEQ_NUMBER; break;
                    case 1 : eventType = MetaMidiEvent.MetaEventType.TEXT; break;
                    case 2 : eventType = MetaMidiEvent.MetaEventType.COPYRIGHT_INFO; break;
                    case 3 : eventType = MetaMidiEvent.MetaEventType.TRACK_NAME; break;
                    case 4 : eventType = MetaMidiEvent.MetaEventType.TRACK_INSTRUMENT_NAME; break;
                    case 5 : eventType = MetaMidiEvent.MetaEventType.LYRIC; break;
                    case 6 : eventType = MetaMidiEvent.MetaEventType.MARKER; break;
                    case 7 : eventType = MetaMidiEvent.MetaEventType.CUE_POINT; break;
                    case 0x2F : eventType = MetaMidiEvent.MetaEventType.TRACK_END; break;
                    case 0x51 : eventType = MetaMidiEvent.MetaEventType.SET_TEMPO; break;
                    case 0x58 : eventType = MetaMidiEvent.MetaEventType.TIME_SIGNATURE; break;
                    case 0x59 : eventType = MetaMidiEvent.MetaEventType.KEY_SIGNATURE; break;
                    case 0x7F : eventType = MetaMidiEvent.MetaEventType.SEQUENCER_INFO; break;
                    default : eventType = MetaMidiEvent.MetaEventType.UNKNOWN; break;
                }
                int byteCount = stream.read();
                byte[] bytes = new byte[byteCount];
                stream.read(bytes);
                return new MetaMidiEvent(deltaTime,runningTotalTime,eventType,bytes);
            }
        }
        else // Probably running-status; consult previous Midi Event.
        {
            if (previousVoiceCategoryMidiEvent==null)
                throw new MidiFileFormatException("Unexpected running-status byte");
            if (previousVoiceCategoryMidiEvent instanceof NoteMidiEvent)
            {
                NoteMidiEvent previousNoteMidiEvent = (NoteMidiEvent) previousVoiceCategoryMidiEvent;
                return previousVoiceCategoryMidiEvent = new NoteMidiEvent(deltaTime,runningTotalTime,previousNoteMidiEvent.getChannel(),previousNoteMidiEvent.getNoteEventType(),nextByte,stream.read());
            }
            else
            {
                StateChangeMidiEvent previousStateChangeMidiEvent = (StateChangeMidiEvent) previousVoiceCategoryMidiEvent;
                boolean twoValues = (previousStateChangeMidiEvent.getStateChangeType()==StateChangeMidiEvent.StateChangeType.CONTROL_CHANGE) || (previousStateChangeMidiEvent.getStateChangeType()==StateChangeMidiEvent.StateChangeType.PITCH_WHEEL_CHANGE);
                return previousVoiceCategoryMidiEvent = twoValues
                        ? new StateChangeMidiEvent(deltaTime,runningTotalTime,previousStateChangeMidiEvent.getChannelNumber(),previousStateChangeMidiEvent.getStateChangeType(),nextByte,stream.read())
                        : new StateChangeMidiEvent(deltaTime,runningTotalTime,previousStateChangeMidiEvent.getChannelNumber(),previousStateChangeMidiEvent.getStateChangeType(),nextByte);
            }
        }
    }
}
