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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import midireader.MidiFileInfo.MidiFileFormat;
import midireader.midievent.MidiEvent;
import midireader.util.FileHelpers;
import midireader.util.Pair;

public class MidiReader implements Iterable<MidiEvent>,Closeable
{
    private static final byte[] EXPECTED_MIDI_HEADER = new byte[] { 0x4D, 0x54, 0x68, 0x64, 0x00, 0x00, 0x00, 0x06 };
    
    private InputStream stream;
    private String filePath = null;
    private MidiFileInfo midiFileInfo = null;
    private MidiTrackReader[] trackReaders = null;
    
    public MidiReader(InputStream stream)
    {
        this.stream = stream;
    }
    
    public MidiReader(InputStream stream,String filePath)
    {
        this(stream);
        this.filePath = filePath;
    }
    
    public MidiReader(File file) throws FileNotFoundException
    {
        this(new FileInputStream(file),file.getPath());
    }
    
    public MidiReader(String filePath) throws FileNotFoundException
    {
        this(new FileInputStream(filePath),filePath);
    }
    
    public MidiFileInfo getMidiFileInfo() throws IOException, MidiFileFormatException
    {
        if (midiFileInfo==null)
            readMidiFileInfo();
        return midiFileInfo;
    }
    
    public Iterator<MidiEvent> iterator()
    {
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
        if (trackReaders!=null)
            for (MidiTrackReader nextReader : trackReaders)
                if (nextReader!=null)
                    nextReader.close();
        stream.close();
    }
    
    
    private class MidiEventIterator implements Iterator<MidiEvent>
    {
        private ArrayList<Pair<Iterator<MidiEvent>,MidiEvent>> tracks;
        private MidiEvent nextMidiEvent = null;
        
        public MidiEventIterator() throws IOException, MidiFileFormatException
        {
            getMidiFileInfo();
            tracks = new ArrayList<Pair<Iterator<MidiEvent>,MidiEvent>>(trackReaders.length);
            for (MidiTrackReader nextReader : trackReaders)
            {
                Iterator<MidiEvent> it = nextReader.iterator();
                MidiEvent midiEvent = it.hasNext() ? it.next() : null;
                tracks.add(new Pair<Iterator<MidiEvent>,MidiEvent>(it,midiEvent));
            }
            next();
        }
        
        public boolean hasNext()
        {
            return nextMidiEvent!=null;
        }

        public MidiEvent next()
        {
            MidiEvent currentMidiEvent = nextMidiEvent;
            Pair<Iterator<MidiEvent>,MidiEvent> pairOfNextEvent = null;
            for (Pair<Iterator<MidiEvent>,MidiEvent> nextPair : tracks)
                if ((nextPair.second!=null) && ((pairOfNextEvent==null) || (nextPair.second.getTotalTime()<pairOfNextEvent.second.getTotalTime())))
                    pairOfNextEvent = nextPair;
            if (pairOfNextEvent==null)
                nextMidiEvent = null;
            else
            {
                nextMidiEvent = pairOfNextEvent.second;
                pairOfNextEvent.second = pairOfNextEvent.first.hasNext() ? pairOfNextEvent.first.next() : null;
                nextMidiEvent.setDeltaTime((int) (nextMidiEvent.getTotalTime() - (currentMidiEvent==null ? 0 : currentMidiEvent.getTotalTime())));
            }
            return currentMidiEvent;
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
    
    
    @SuppressWarnings("resource")
    private void readMidiFileInfo() throws IOException, MidiFileFormatException
    {
        byte[] bytes = new byte[255];
        FileHelpers.readBytes(stream,bytes,EXPECTED_MIDI_HEADER.length);
        if (! FileHelpers.bytesMatch(EXPECTED_MIDI_HEADER,bytes))
            throw new MidiFileFormatException("Invalid file format (bad header)");
        
        FileHelpers.readBytes(stream,bytes,2);
        int fileFormatIdx = FileHelpers.getUnsignedInt(bytes,2);
        if (fileFormatIdx>MidiFileFormat.values().length)
            throw new MidiFileFormatException("Invalid file format number: "+fileFormatIdx+". File format number must be between 0 and "+(MidiFileFormat.values().length - 1));
        MidiFileFormat fileFormat = MidiFileFormat.values()[fileFormatIdx];
        if ((fileFormat!=MidiFileFormat.SingleTrack) && (filePath==null))
            throw new MidiFileFormatException("MIDI file format "+fileFormat+" is supported only by a MidiReader that was created with a file path!");
        FileHelpers.readBytes(stream,bytes,2);
        int trackCount = FileHelpers.getUnsignedInt(bytes,2);
        FileHelpers.readBytes(stream,bytes,2);
        midiFileInfo = new MidiFileInfo(filePath,fileFormat,trackCount,FileHelpers.getUnsignedInt(bytes,2));
        trackReaders = new MidiTrackReader[trackCount];
        
        for (int trackNumber=0; trackNumber<trackCount; trackNumber++)
        {
            MidiTrackReader nextTrackReader;
            if (fileFormat==MidiFileFormat.SingleTrack)
                nextTrackReader = new MidiTrackReader(stream,trackNumber);
            else
            {
                FileInputStream mainStream = (FileInputStream) stream;
                InputStream trackStream = new FileInputStream(filePath);
                trackStream.skip(mainStream.getChannel().position());
                nextTrackReader = new MidiTrackReader(trackStream,trackNumber);
            }
            midiFileInfo.addTrack(nextTrackReader.readMidiTrackInfo(midiFileInfo));
            trackReaders[trackNumber] = nextTrackReader;
            if (trackNumber<trackCount - 1)
                stream.skip(nextTrackReader.getMidiTrackInfo().getTrackLengthBytes() + 8);
        }
    }
}
