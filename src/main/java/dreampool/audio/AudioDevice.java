package dreampool.audio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.EXTThreadLocalContext;
import org.lwjgl.openal.SOFTHRTF;

import dreampool.IO.FileUtils;

import static org.lwjgl.system.MemoryUtil.NULL;

public class AudioDevice {
	public long ID;
	public long context;
	public String HRTFName = "Dreampool";
	
	public AudioDevice() {
		File linuxConf = new File(System.getenv("HOME") + "/.config/alsoft.conf");
		if(!linuxConf.exists()) {
			try {
				linuxConf.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(linuxConf), "utf-8"))) {
				writer.write(FileUtils.readTextResource("/audio/config/alsoftrc.sample"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		File linuxHRTF = new File(System.getenv("HOME") + "/.local/share/openal/hrtf/" + HRTFName + ".mhr");
		if(!linuxHRTF.exists()) {
			linuxHRTF.getParentFile().mkdirs();
			try (InputStream is = Class.class.getResourceAsStream("/audio/hrtf/" + HRTFName + ".mhr")) {
			    Files.copy(is, Paths.get(linuxHRTF.getAbsolutePath()));
			} catch (IOException e) {
			    // An error occurred copying the resource
			}
		}
		
		ID = ALC11.alcOpenDevice((ByteBuffer)null);
		if(ID == NULL) {
			throw new IllegalStateException("Failed to open the audio device");
		}
		
		ALCCapabilities deviceCaps = ALC.createCapabilities(ID);
		
		if(!deviceCaps.ALC_SOFT_HRTF) {
			throw new IllegalStateException("Error: ALC_SOFT_HRTF not supported");
		}
		
		context = ALC11.alcCreateContext(ID, (IntBuffer)null);
		
		EXTThreadLocalContext.alcSetThreadContext(context);
		AL.createCapabilities(deviceCaps);
		
		int num_hrtf = ALC11.alcGetInteger(ID, SOFTHRTF.ALC_NUM_HRTF_SPECIFIERS_SOFT);
		if(num_hrtf == 0) {
			System.out.println("No HRTFs found");
		} else {
			int index = -1;
			
			System.out.println("Available HRTFs:");
			for(int i = 0; i < num_hrtf; i++) {
				String name = Objects.requireNonNull(SOFTHRTF.alcGetStringiSOFT(ID, SOFTHRTF.ALC_HRTF_SPECIFIER_SOFT, i));
				System.out.format("	%d: %s\n", i, name);
				
				if (HRTFName != null && name.equals(HRTFName)) {
                    index = i;
                }
			}
			
			IntBuffer attr = BufferUtils.createIntBuffer(10).put(SOFTHRTF.ALC_HRTF_SOFT).put(ALC11.ALC_TRUE);
			
			if(index == -1) {
				if(HRTFName != null) {
					System.out.format("HRTF \"%s\" not found\n", HRTFName);
				}
				System.out.format("Using default HRTF...\n");
			} else {
                System.out.format("Selecting HRTF %d...\n", index);
                attr.put(SOFTHRTF.ALC_HRTF_ID_SOFT).put(index);
            }
			attr.put(0);
			attr.flip();
			
			if(!SOFTHRTF.alcResetDeviceSOFT(ID, attr)) {
				System.out.format("Failed to reset device: %s\n", ALC11.alcGetString(ID, ALC11.alcGetError(ID)));
			}
			
			int hrtf_state = ALC11.alcGetInteger(ID, SOFTHRTF.ALC_HRTF_SOFT);
			if(hrtf_state == 0) {
				System.out.format("HRTF not enabled!\n");
			} else {
                String name = ALC11.alcGetString(ID, SOFTHRTF.ALC_HRTF_SPECIFIER_SOFT);
                System.out.format("HRTF enabled, using %s\n", name);
			}
		}
	}
	public void destroy() {
		EXTThreadLocalContext.alcSetThreadContext(NULL);
		ALC11.alcDestroyContext(context);
		ALC11.alcCloseDevice(ID);
	}
}
