package com.lts.core.ruptela;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

/**
 * @author Vincentas
 *
 */
public class LoggingInputStream extends InputStream {

	private final InputStream inputStream;
	
	private final OutputStream loggingStream;
	
	public LoggingInputStream(OutputStream loggingStream, InputStream inputStream) {
		this.inputStream = inputStream;
		this.loggingStream = loggingStream;
	}
	
	/* (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		int result = inputStream.read();
		loggingStream.write(result);
		loggingStream.flush();
		return result;
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#close()
	 */
	@Override
	public void close() throws IOException {
		loggingStream.flush();
		IOUtils.closeQuietly(inputStream);		
		IOUtils.closeQuietly(loggingStream);
	}

    @Override
        public int available(){
            try {
                return inputStream.available();
            } catch (IOException ex) {
                Logger.getLogger(LoggingInputStream.class.getName()).log(Level.SEVERE, null, ex);
            }
            return 0;
        }
}
