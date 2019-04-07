package de.sos.script;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import de.sos.script.impl.lang.js.JSScriptManager;
import de.sos.script.impl.lang.py.PyScriptManager;


public abstract class ScriptSource implements IScriptSource {

	
	
	
	public interface WriteableScriptSource {
		/** this method ask if the script can be written and is called whenever writeContent shall be called. 
		 * 
		 * @return true if the source can write the content
		 */
		boolean canWrite(final String newContent);
		/** Writes back changes in the content of the script. 
		 * 
		 * @param content the new (changed) content
		 * @return true, if the content has been written, false if an error occured.
		 */
		boolean writeContent(final String content);
	}
	
	private final String	mLanguage;
	private final String 	mIdentifier;
	
	protected ScriptSource(final String identifier, final String lang) {
		mLanguage = lang;
		mIdentifier = identifier;
	}
	
	/* (non-Javadoc)
	 * @see de.sos.script.IScriptSource#getContentAsString()
	 */
	@Override
	public abstract String getContentAsString();
	
	/* (non-Javadoc)
	 * @see de.sos.script.IScriptSource#getLanguage()
	 */
	@Override
	public String getLanguage() { return mLanguage; }
	/* (non-Javadoc)
	 * @see de.sos.script.IScriptSource#getIdentifier()
	 */
	@Override
	public String getIdentifier() { return mIdentifier;}
	
	public static abstract class WritableScriptSource extends ScriptSource implements WriteableScriptSource {
		protected boolean 		mWriteable = true;
	
		protected WritableScriptSource(final String identifier, final String lang) {
			this(identifier, lang, true);
		}
		protected WritableScriptSource(final String identifier, final String lang, boolean writeable) {
			super(identifier, lang);
			mWriteable = writeable;
		}
		
		public void setReadonly(boolean readonly) {
			mWriteable = !readonly;
		}
		public boolean isReadOnly() { return !mWriteable;}
		
		@Override
		public boolean canWrite(String newContent) {
			return mWriteable;
		}
	}
	
	
	public static class StringSource extends WritableScriptSource {
		private String 			mContent;
		
		public StringSource(final String identifier, String content, String lang) {
			super(identifier, lang);
			mContent = content;
		}
		
		@Override
		public String getContentAsString() {
			return mContent;
		}
		@Override
		public boolean writeContent(String content) {
			mContent = content;
			return true;
		}
	}
	
	public static class StreamSource extends ScriptSource {
		private final BufferedInputStream mStream;
		
		public StreamSource(final String identifier, final InputStream stream, String lang) {
			super(identifier, lang);
			mStream = new BufferedInputStream(stream);
		}
		
		@Override
		public String getContentAsString() {
			try {
				byte[] data = new byte[mStream.available()];
				mStream.read(data);
				return new String(data);
			}catch(Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	public static class FileSource extends WritableScriptSource {
		protected File 	mFile;
		
		public static String languageFromFile(File file) {
			if (file.getName().endsWith(".js"))
				return JSScriptManager.LANG_JAVASCRIPT;
			if (file.getName().endsWith(".py"))
				return PyScriptManager.LANG_JYPTHON;
			return null;
		}
			
		public FileSource(final File file) {
			this(file, languageFromFile(file));
		}
		
		public FileSource(final File file, final String lang) {
			super(file.getPath(), lang);
			mFile = file;
		}
		
		public File getFile() {
			return mFile;
		}
		
		@Override
		public String getContentAsString() {
			try {
				return new String(Files.readAllBytes(getFile().toPath()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		public boolean writeContent(String content) {
			try {
				FileOutputStream fos = new FileOutputStream(getFile());
				fos.write(content.getBytes());
				fos.close();
				return true;
			}catch(Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	}
	
}
