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

public class MetaMidiEvent extends MidiEvent
{
    public enum MetaEventType { TRACK_SEQ_NUMBER, TEXT, COPYRIGHT_INFO, TRACK_NAME, TRACK_INSTRUMENT_NAME, LYRIC, MARKER, CUE_POINT, TRACK_END, SET_TEMPO, TIME_SIGNATURE, KEY_SIGNATURE, SEQUENCER_INFO, UNKNOWN };
    
    private MetaEventType metaEventType;
    private byte[] content;
    
    public MetaMidiEvent(int deltaTime,long totalTime,MetaEventType metaEventType,byte[] content)
    {
        super(deltaTime,totalTime);
        this.metaEventType = metaEventType;
        this.content = content;
    }
    
    public MetaEventType getMetaEventType()
    {
        return metaEventType;
    }
    
    public byte[] getContent()
    {
        return content;
    }
    
    public String getContentAsString()
    {
        return content==null ? null : new String(content);
    }
    
    public String toString()
    {
        return super.toString()+"[metaEventType="+metaEventType+";contentAsString="+getContentAsString()+']';
    }
}
