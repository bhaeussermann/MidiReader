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

import java.util.Collection;
import java.util.HashMap;

public class MidiFileInfo
{
    public enum MidiFileFormat { SingleTrack, MultipleTracksSynchronous, MultipleTracksAsynchronous };
    
    private String fileName,mainTrackName;
    private MidiFileFormat fileFormat;
    private int numberOfTracks;
    private TimeSignature timeSignature;
    private int ticksPerQuarterNote,ticksPerMetronomeClick;
    private long microsecondsPerQuarterNote,microsecondsPerTick;
    private HashMap<Integer,MidiTrackInfo> trackMap = new HashMap<Integer,MidiTrackInfo>();
    
    protected MidiFileInfo(String fileName,MidiFileFormat fileFormat,int numberOfTracks,int ticksPerQuarterNote)
    {
        this.fileName = fileName;
        this.fileFormat = fileFormat;
        this.numberOfTracks = numberOfTracks;
        this.ticksPerQuarterNote = ticksPerQuarterNote;
    }
    
    protected MidiFileInfo(MidiFileFormat fileFormat,int numberOfTracks,int ticksPerQuarterNote)
    {
        this(null,fileFormat,numberOfTracks,ticksPerQuarterNote);
    }
    
    public String getFileName()
    {
        return fileName;
    }
    
    public String getMainTrackName()
    {
        return mainTrackName;
    }
    
    protected void setMainTrackName(String newTrackName)
    {
        mainTrackName = newTrackName;
    }
    
    public MidiFileFormat getFileFormat()
    {
        return fileFormat;
    }
    
    public int getNumberOfTracks()
    {
        return numberOfTracks;
    }
    
    public TimeSignature getTimeSignature()
    {
        return timeSignature;
    }
    
    protected void setTimeSignature(TimeSignature newTimeSignature)
    {
        timeSignature = newTimeSignature;
    }
    
    public long getMicrosecondsPerQuarterNote()
    {
        return microsecondsPerQuarterNote;
    }
    
    protected void setMicrosecondsPerQuarterNote(long newMicrosecondsPerQuarterNote)
    {
        microsecondsPerQuarterNote = newMicrosecondsPerQuarterNote;
        microsecondsPerTick = microsecondsPerQuarterNote / ticksPerQuarterNote;
    }
    
    public int getTicksPerQuarterNote()
    {
        return ticksPerQuarterNote;
    }
    
    public int getTicksPerMetronomeClick()
    {
        return ticksPerMetronomeClick;
    }
    
    protected void setTicksPerMetronomeClick(int newTicksPerMetronomeClick)
    {
        ticksPerMetronomeClick = newTicksPerMetronomeClick;
    }
           
    public long getMicrosecondsPerTick()
    {
        return microsecondsPerTick;
    }
    
    public Collection<MidiTrackInfo> getTrackInfos()
    {
        return trackMap.values();
    }
    
    public MidiTrackInfo getTrackInfo(int trackNumber)
    {
        return trackMap.get(trackNumber);
    }
    
    protected void addTrack(MidiTrackInfo newTrack)
    {
        trackMap.put(newTrack.getTrackNumber(),newTrack);
    }
    
    public String toString()
    {
        return getClass().getName()+"[fileName="+fileName+";mainTrackName="+mainTrackName+";timeSignature="+timeSignature+";ticksPerQuarterNote="+ticksPerQuarterNote+";ticksPerMetronomeClick="+ticksPerMetronomeClick+";microsecondsPerQuarterNote="+microsecondsPerQuarterNote+";microsecondsPerTick="+microsecondsPerTick+";trackMap="+trackMap+']';
    }
}
