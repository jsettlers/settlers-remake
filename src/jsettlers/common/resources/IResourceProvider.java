package jsettlers.common.resources;

import java.io.IOException;
import java.io.InputStream;

public interface IResourceProvider {
	InputStream getFile(String name) throws IOException;
}
