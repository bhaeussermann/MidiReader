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

public class MidiTrackInfo
{
    private int trackNumber;
    private long trackLengthBytes;
    private String trackName;
    private HashMap<Integer,Channel> channelMap = new HashMap<Integer,Channel>();
    
    public MidiTrackInfo(int trackNumber)
    {
        this.trackNumber = trackNumber;
    }
    
    public int getTrackNumber()
    {
        return trackNumber;
    }
    
    public long getTrackLengthBytes()
    {
        return trackLengthBytes;
    }
    
    public String getTrackName()
    {
        return trackName;
    }
    
    protected void setTrackName(String newTrackName)
    {
        trackName = newTrackName;
    }
    
    protected void setTrackLengthBytes(long newTrackLengthBytes)
    {
        trackLengthBytes = newTrackLengthBytes;
    }
    
    public Collection<Channel> getChannels()
    {
        return channelMap.values();
    }
    
    public Channel getChannel(int channelNumber)
    {
        return channelMap.get(channelNumber);
    }
    
    protected void addChannel(Channel newChannel)
    {
        channelMap.put(newChannel.getChannelNumber(),newChannel);
    }
    
    public String toString()
    {
        return getClass().getName()+"[trackNumber="+trackNumber+";trackLengthBytes="+trackLengthBytes+";trackName="+trackName+";channelMap="+channelMap+']';
    }
}
