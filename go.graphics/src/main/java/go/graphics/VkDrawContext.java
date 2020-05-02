package go.graphics;

import java.nio.ByteBuffer;
import java.util.List;

public interface VkDrawContext {
    void updateTexture(TextureHandle handle, List<int[]> diff, ByteBuffer data);
    void updateBufferAt(BufferHandle handle, List<Integer> pos, List<Integer> len, ByteBuffer data);
}
