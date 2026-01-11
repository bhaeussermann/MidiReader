# MidiReader

A simple zero-dependency Java library for reading events from a Midi file.

The MidiReader class creates an Iterator that reads all of the tracks in the Midi file at once (for synchronous multiple-track files). 
It mixes the Midi events into a single sequence so that the events are retrieved in chronological order.

This library is ideal for simple applications. If you are looking for something more sophisticated, then [javax.sound.midi.spi.MidiFileReader](https://docs.oracle.com/en/java/javase/17/docs/api/java.desktop/javax/sound/midi/spi/MidiFileReader.html)
might be a better option.

_Trivial disclosure: This project was created and used for playing music with floppy drives. See [my YouTube channel](https://www.youtube.com/@digitalmusician3264)._

## Example usage

The following example reads the specified Midi file to find all MidiEvent's of type MetaMidiEvent that represent lyrics, and prints 
them to the console in real time. Commenting the `Thread.sleep()` line will output the entire song's lyrics at once.

### Imports

```java
import midireader.*;
import midireader.midievent.*;
```

### Code

```java
MidiReader reader = new MidiReader("[ path to your midi file that contains lyrics ]");
try
{
    MidiFileInfo midiFileInfo = reader.getMidiFileInfo();
    for (MidiEvent nextEvent : reader)
    {
        long delayMillis = nextEvent.getDeltaTime() * midiFileInfo.getMicrosecondsPerTick() / 1000;
        Thread.sleep(delayMillis);
        if ((nextEvent instanceof MetaMidiEvent) && (((MetaMidiEvent)nextEvent).getMetaEventType() == MetaMidiEvent.MetaEventType.LYRIC))
        {
            System.out.print(((MetaMidiEvent)nextEvent).getContentAsString());
        }
    }
}
finally
{
    try
    {
        reader.close();
    } 
    catch (IOException e) {}
}
```

## Links

Here is a list of the most crucial resources that I used while developing the library:

* [(.mid) Standard MIDI File Format](http://faydoc.tripod.com/formats/mid.htm)
* [Running Status](http://web.archive.org/web/20130305092440/http://home.roadrunner.com/~jgglatt/tech/midispec/run.htm)
* [MIDI CC List](http://nickfever.com/music/midi-cc-list)
