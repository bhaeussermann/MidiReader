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

package midireader.util;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class FileHelpers
{
    public static final int MSB_MASK = 1<<7;
    
    public static void readBytes(InputStream stream,byte[] b,int byteCount) throws IOException
    {
        if (stream.read(b,0,byteCount)<byteCount)
            throw new EOFException("Unexpected end of file");
    }
    
    public static boolean bytesMatch(byte[] expectedBytes,byte[] bytes)
    {
        if (bytes.length<expectedBytes.length)
            return false;
        for (int i=0; i<expectedBytes.length; i++)
            if (expectedBytes[i]!=bytes[i])
                return false;
        return true;
    }
    
    public static int getUnsignedInt(byte[] bytes)
    {
        return getUnsignedInt(bytes,bytes.length);
    }
    
    public static int getUnsignedInt(byte[] bytes,int byteCount)
    {
        int result = 0;
        for (int i=0; i<byteCount; i++)
            result = result*256 + fixByte(bytes[i]);
        return result;
    }
    
    public static long getUnsignedLong(byte[] bytes)
    {
        return getUnsignedLong(bytes,bytes.length);
    }
    
    public static long getUnsignedLong(byte[] bytes,int byteCount)
    {
        long result = 0;
        for (int i=0; i<byteCount; i++)
            result = result*256 + fixByte(bytes[i]);
        return result;
    }
    
    public static int fixByte(byte b)
    {
        return b>=0 ? b : 256 + b;
    }
    
    public static boolean byteHasMsbSet(int b)
    {
        return (b & MSB_MASK)==MSB_MASK; 
    }
}
