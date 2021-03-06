package vsp.data;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import vsp.display.FrameRecordingPlayer;

/**
 * A class that represents a Frame Recording.
 * @author adam
 */
public class FrameRecording {

    /** The ID of this StreamRecording. */
    private UUID m_id;

    /** The ID of the Stream Source  */
    private UUID m_videoSourceId;

    /** The name of this recording. */
    private String m_name;

    /** The number of Frames Per Second in this frame recording. */
    private int m_fps;

    /** The directory that contains the frame data. */
    private String m_frameDirectory;

    /** The fully qualified path to the original file that was processed into this FrameRecording. */
    private String m_originalFileName;

    /** The start time of this FrameRecording (in ms since Jan 1 1970). */
    private long m_startTime;

    /** The end time of this FrameRecording (in ms since Jan 1 1970). */
    private long m_endTime;

    /** Logger */
    private static final Logger LOGGER = Logger.getLogger(FrameRecordingPlayer.class.getName());


    /**
     * Creates a new, unnamed FrameRecording.
     * @param videoSource the VideoSource ID for this FrameRecording.
     * @param fps the number of frames per second for this FrameRecording.
     * @param frameDir the directory that contains the frame images.
     * @param originalFile the fully qualified path to the original file from which this FrameRecording was ripped.
     * @param startTime the start time of this Frame Recording (in ms since Jan 1, 1970.)
     * @param endTime the end time of this Frame Recording (in ms since Jan 1, 1970.)
     */
    public FrameRecording(UUID videoSource, int fps, String originalFile, String frameDir, long startTime, long endTime) {
        this(videoSource, fps, originalFile, frameDir, startTime, endTime, "Unnamed Frame Recording");
    }


    /**
     * Creates a new, named FrameRecording.
     * @param videoSource the VideoSource ID for this FrameRecording.
     * @param fps the number of frames per second for this FrameRecording.
     * @param frameDir the directory that contains the frame images.
     * @param originalFile the fully qualified path to the original file from which this FrameRecording was ripped.
     * @param startTime the start time of this Frame Recording (in ms since Jan 1, 1970.)
     * @param endTime the end time of this Frame Recording (in ms since Jan 1, 1970.)
     * @param name the name of this frame recording.
     */
    public FrameRecording(UUID videoSource, int fps, String originalFile, String frameDir, long startTime, long endTime, String name) {
        this(UUID.randomUUID(), videoSource, fps, originalFile, frameDir, startTime, endTime, name);
    }

    /**
     * A Private constructor for internal use of this class only.
     * @param id the ID of this FrameRecording.
     * @param videoSource the VideoSource ID for this FrameRecording.
     * @param fps the number of frames per second for this FrameRecording.
     * @param frameDir the directory that contains the frame images.
     * @param originalFile the fully qualified path to the original file from which this FrameRecording was ripped.
     * @param startTime the start time of this Frame Recording (in ms since Jan 1, 1970.)
     * @param endTime the end time of this Frame Recording (in ms since Jan 1, 1970.)
     * @param name the name of this frame recording.
     */
    private FrameRecording(UUID id, UUID videoSource, int fps, String originalFile, String frameDir, long startTime, long endTime, String name) {
        m_videoSourceId = videoSource;
        m_name = name;
        m_fps = fps;
        m_frameDirectory = frameDir;
        m_originalFileName = originalFile;
        m_startTime = startTime;
        m_endTime = endTime;
        m_id = UUID.randomUUID();
        m_id = id;
    }

    /**
     * Creates a new FrameRecording from the data in the supplied properties file.
     * @param fileName the file to read the properties from.
     * @return the FrameRecording described by the file.
     */
    public static FrameRecording fromFile(String fileName) {
        Properties recordingProperties = new Properties();
        try {
            recordingProperties.load(new FileReader(fileName));
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error loading Frame Recording properties.", ex);
        }
        UUID id = UUID.fromString(recordingProperties.getProperty("recording.id"));
        UUID sourceId = UUID.fromString(recordingProperties.getProperty("video.source.id"));
        String name = recordingProperties.getProperty("name");
        int fps = Integer.valueOf(recordingProperties.getProperty("fps"));
        String frameDir = recordingProperties.getProperty("frame.dir");
        System.out.println(frameDir);
        String originalFileName = recordingProperties.getProperty("original.file.name");
        long startTime = Long.valueOf(recordingProperties.getProperty("start.time"));
        long endTime = Long.valueOf(recordingProperties.getProperty("end.time"));

        return new FrameRecording(id, sourceId, fps, originalFileName, frameDir, startTime, endTime, name);
    }

    /**
     * Saves this Frame Recording to file.
     * @param outputFile the file to write out the properties to.
     */
    public void saveToFile(String outputFile) {
        Properties props = new Properties();
        props.put("recording.id", m_id.toString());
        props.put("video.source.id", m_videoSourceId.toString());
        props.put("name", m_name);
        props.put("fps", String.valueOf(m_fps));
        props.put("frame.dir", m_frameDirectory);
        props.put("original.file.name", m_originalFileName);
        props.put("start.time", String.valueOf(m_startTime));
        props.put("end.time", String.valueOf(m_endTime));

        // Write out to disk.
        try {
            FileWriter writer = new FileWriter(outputFile);
            props.store(writer, "Frame Recording Properties - " + new java.util.Date().toString());
            writer.close();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error writing Frame Recording properties.", ex);
        }
    }

    /**
     * Update the end time of a file.
     * 
     * @param outputFile the file to write out the properties to.
     * @param endTime The time that this recording ended.
     */
    public static void updateRecordingEndTime(String outputFile, long endTime) {
        FrameRecording recording = FrameRecording.fromFile(outputFile);
        recording.setEndTime(endTime);
        recording.saveToFile(outputFile);
    }
    
    /**
     * Returns a unique ID for this recording.
     * @return a unique ID for this recording.
     */
    public UUID getId() {
        return m_id;
    }

    /**
     * Returns the UUID of the VideoSource of this recording.
     * @return the UUID of the VideoSource of this recording.
     */
    public UUID getVideoSourceId() {
        return m_videoSourceId;
    }

    /**
     * Returns a user-consumable name for this recording.
     * @return a user-consumable name for this recording.
     */
    public String getName() {
        return m_name;
    }

    /**
     * Returns the frame rate in Frames Per Second (FPS).
     * @return the frame rate in Frames Per Second (FPS).
     */
    public int getFps() {
        return m_fps;
    }

    /**
     * Returns the original file from which this recording was ripped.
     * @return the original file from which this recording was ripped.
     */
    public String getOriginalFile() {
        return m_originalFileName;
    }

    /**
     * Returns the directory that contains the frames for this recording.
     * @return the directory that contains the frames for this recording.
     */
    public String getFrameDirectory() {
        return m_frameDirectory;
    }

    /**
     * Returns the start time of this recording.
     * @return the start time of this recording.
     */
    public long getStartTime() {
        return m_startTime;
    }

    /**
     * Gets the end time of this recording.
     * @return the end time of this recording.
     */
    public long getEndTime() {
        return m_endTime;
    }

    /**
     * Sets the end time of this recording.
     */
    private void setEndTime(long endTime) {
        m_endTime = endTime;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.m_id);
        hash = 37 * hash + Objects.hashCode(this.m_videoSourceId);
        hash = 37 * hash + Objects.hashCode(this.m_name);
        hash = 37 * hash + this.m_fps;
        hash = 37 * hash + Objects.hashCode(this.m_originalFileName);
        hash = 37 * hash + Objects.hashCode(this.m_frameDirectory);
        hash = 37 * hash + (int) (this.m_startTime ^ (this.m_startTime >>> 32));
        hash = 37 * hash + (int) (this.m_endTime ^ (this.m_endTime >>> 32));
        return hash;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FrameRecording other = (FrameRecording) obj;
        if (!Objects.equals(this.m_id, other.m_id)) {
            return false;
        }
        if (!Objects.equals(this.m_videoSourceId, other.m_videoSourceId)) {
            return false;
        }
        if (!Objects.equals(this.m_name, other.m_name)) {
            return false;
        }
        if (this.m_fps != other.m_fps) {
            return false;
        }
        if (!Objects.equals(this.m_originalFileName, other.m_originalFileName)) {
            return false;
        }
        if (!Objects.equals(this.m_frameDirectory, other.m_frameDirectory)) {
            return false;
        }
        if (this.m_startTime != other.m_startTime) {
            return false;
        }
        if (this.m_endTime != other.m_endTime) {
            return false;
        }
        return true;
    }
}