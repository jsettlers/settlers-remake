package go.graphics.swing.opengl;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBDrawInstanced;
import org.lwjgl.opengl.ARBUniformBufferObject;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.KHRDebug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import go.graphics.AbstractColor;
import go.graphics.BackgroundDrawHandle;
import go.graphics.GLDrawContext;
import go.graphics.BufferHandle;
import go.graphics.ManagedHandle;
import go.graphics.MultiDrawHandle;
import go.graphics.TextureHandle;
import go.graphics.UnifiedDrawHandle;
import go.graphics.swing.text.LWJGLTextDrawer;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

@SuppressWarnings("WeakerAccess")
public class LWJGLDrawContext extends GLDrawContext {
	public LWJGLDrawContext(GLCapabilities glcaps, boolean debug) {
		this.glcaps = glcaps;

		if(debug) debugOutput = new LWJGLDebugOutput(this);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);

		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

		init();

	}

	private TextDrawer[] sizedTextDrawers = new TextDrawer[EFontSize.values().length];
	private LWJGLTextDrawer textDrawer = null;

	private ArrayList<ShaderProgram> shaders;

	private final Matrix4f global = new Matrix4f();
	private final Matrix4f mat = new Matrix4f();
	private final FloatBuffer matBfr = BufferUtils.createFloatBuffer(16);
	protected LWJGLDebugOutput debugOutput = null;

	public final GLCapabilities glcaps;

	protected BufferHandle lastGeometry = null;
	protected TextureHandle lastTexture = null;

	void init() {
		shaders = new ArrayList<>();

		if(glcaps.GL_EXT_geometry_shader4 && glcaps.GL_ARB_uniform_buffer_object) prog_unified_multi = new ShaderProgram("unified-multi");
		if(glcaps.GL_EXT_draw_instanced) prog_unified_array = new ShaderProgram("unified-array");
		prog_background = new ShaderProgram("background");
		prog_unified = new ShaderProgram("unified");
	}

	private ShaderProgram lastProgram = null;
	protected void useProgram(ShaderProgram id) {
		if(id != lastProgram) {
			GL20.glUseProgram(id.program);
			lastProgram = id;
		}
	}

	private ShaderProgram prog_unified_multi = null;
	private ShaderProgram prog_unified_array = null;
	private ShaderProgram prog_background;
	private ShaderProgram prog_unified;

	private float ulr, ulg, ulb, ula, uli;
	private float ulm;

	
	public TextureHandle generateTexture(int width, int height, ShortBuffer data, String name) {
		int texture = GL11.glGenTextures();
		if (texture == 0) {
			return null;
		}

		TextureHandle textureHandle = new TextureHandle(this, texture);
		resizeTexture(textureHandle, width, height, data);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

		setObjectLabel(GL11.GL_TEXTURE, texture, name + "-tex");

		return textureHandle;
	}

	public void resizeTexture(TextureHandle textureIndex, int width, int height, ShortBuffer data) {
		bindTexture(textureIndex);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL12.GL_UNSIGNED_SHORT_4_4_4_4, data);
	}
	
	public void updateTexture(TextureHandle texture, int left, int bottom,
							  int width, int height, ShortBuffer data) {
		bindTexture(texture);
		GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, left, bottom, width, height,
				GL11.GL_RGBA, GL12.GL_UNSIGNED_SHORT_4_4_4_4, data);
	}

	protected void bindTexture(TextureHandle texture) {
		if(lastTexture != texture) {
			int id = 0;
			if (texture != null) {
				id = texture.getTextureId();
			}
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
			lastTexture = texture;
		}
	}

	protected void bindGeometry(BufferHandle geometry) {
		if(lastGeometry != geometry) {
			int id = 0;
			if (geometry != null) {
				id = geometry.getBufferId();
			}
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id);
			lastGeometry = geometry;
		}
	}

	/**
	 * Gets a text drawer for the given text size.
	 *
	 * @param size
	 *            The size for the drawer.
	 * @return An instance of a drawer for that size.
	 */
	
	public TextDrawer getTextDrawer(EFontSize size) {
		if(textDrawer == null) textDrawer = new LWJGLTextDrawer(this);

		if (sizedTextDrawers[size.ordinal()] == null) {
			sizedTextDrawers[size.ordinal()] = textDrawer.derive(size);
		}
		return sizedTextDrawers[size.ordinal()];
	}

	private int lastFormat = 0;
	protected void bindFormat(int format) {
		if(format != lastFormat) {
			ARBVertexArrayObject.glBindVertexArray(format);
			lastFormat = format;
		}
	}
	
	public void updateBufferAt(BufferHandle handle, int pos, ByteBuffer data) {
		bindGeometry(handle);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, pos, data);
	}

	protected void setObjectLabel(int type, int id, String name) {
		if(debugOutput != null && glcaps.GL_KHR_debug) {
			KHRDebug.glObjectLabel(type, id, name);
		}
	}
	
	public void setGlobalAttributes(float x, float y, float z, float sx, float sy, float sz) {
		finishFrame();

		global.identity();
		global.scale(sx, sy, sz);
		global.translate(x, y, z);
		global.get(matBfr);

		for(ShaderProgram shader : shaders) {
			useProgram(shader);
			GL20.glUniformMatrix4fv(shader.global, false, matBfr);
		}
	}

	
	public void resize(int width, int height) {
		GL11.glViewport(0, 0, width, height);

		mat.setOrtho(0, width, 0, height, -1, 1);
		mat.get(matBfr);

		for(ShaderProgram shader : shaders) {
			useProgram(shader);
			GL20.glUniformMatrix4fv(shader.proj, false, matBfr);
		}
	}

	
	public void setShadowDepthOffset(float depth) {
		for(ShaderProgram shader : shaders) {
			if(shader.shadow_depth != -1) {
				useProgram(shader);
				GL20.glUniform1f(shader.shadow_depth, depth);

			}
		}
	}

	
	public void setHeightMatrix(float[] matrix) {
		useProgram(prog_background);
		GL20.glUniformMatrix4fv(prog_background.height, false, matrix);
	}

	@Override
	public BackgroundDrawHandle createBackgroundDrawCall(int vertices, TextureHandle texture) {
		int vao = -1;

		if(glcaps.GL_ARB_vertex_array_object) vao = ARBVertexArrayObject.glGenVertexArrays();

		BufferHandle vertexBuffer = new BufferHandle(this, GL15.glGenBuffers());
		BufferHandle colorBuffer = new BufferHandle(this, GL15.glGenBuffers());

		bindGeometry(vertexBuffer);
		setObjectLabel(KHRDebug.GL_BUFFER, vertexBuffer.getBufferId(), "background-shape");
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices*5*4, GL15.GL_DYNAMIC_DRAW);
		bindGeometry(colorBuffer);
		setObjectLabel(KHRDebug.GL_BUFFER, colorBuffer.getBufferId(), "background-color");
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices*4, GL15.GL_DYNAMIC_DRAW);

		BackgroundDrawHandle handle = new BackgroundDrawHandle(this, vao, texture, vertexBuffer, colorBuffer);

		if(glcaps.GL_ARB_vertex_array_object) {
			bindFormat(vao);
			setObjectLabel(GL11.GL_VERTEX_ARRAY, vao, "background-vao");
			fillBackgroundFormat(handle);
		}

		return handle;
	}

	@Override
	public UnifiedDrawHandle createUnifiedDrawCall(int vertices, String name, TextureHandle texture, float[] data) {
		int vao = -1;

		if(glcaps.GL_ARB_vertex_array_object) vao = ARBVertexArrayObject.glGenVertexArrays();

		BufferHandle vertexBuffer = new BufferHandle(this, GL15.glGenBuffers());

		bindGeometry(vertexBuffer);
		setObjectLabel(KHRDebug.GL_BUFFER, vertexBuffer.getBufferId(), name + "-vertices");
		if(data != null) {
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
		} else {
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices*(texture!=null?4:2)*4*4, GL15.GL_DYNAMIC_DRAW);
		}

		UnifiedDrawHandle handle = new UnifiedDrawHandle(this, vao, 0, vertices, texture, vertexBuffer);

		if(glcaps.GL_ARB_vertex_array_object) {
			bindFormat(vao);
			setObjectLabel(GL11.GL_VERTEX_ARRAY, vao, name + "-vao");
			fillUnifiedFormat(handle);
		}

		return handle;
	}

	@Override
	protected MultiDrawHandle createMultiDrawCall(String name, ManagedHandle source) {
		if(prog_unified_multi == null) return null;

		int vao = -1;

		if(glcaps.GL_ARB_vertex_array_object) vao = ARBVertexArrayObject.glGenVertexArrays();

		BufferHandle drawCalls = new BufferHandle(this, GL15.glGenBuffers());

		bindGeometry(drawCalls);
		setObjectLabel(KHRDebug.GL_BUFFER, drawCalls.getBufferId(), name + "-drawcalls");
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, MultiDrawHandle.MAX_CACHE_ENTRIES*12*4, GL15.GL_STREAM_DRAW);

		MultiDrawHandle handle = new MultiDrawHandle(this, vao, MultiDrawHandle.MAX_CACHE_ENTRIES, source, drawCalls);

		if(glcaps.GL_ARB_vertex_array_object) {
			bindFormat(vao);
			setObjectLabel(GL11.GL_VERTEX_ARRAY, vao, name + "-vao");
			fillMultiFormat(handle);
		}

		return handle;
	}

	private void fillBackgroundFormat(BackgroundDrawHandle dh) {
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		bindGeometry(dh.vertices);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 5 * 4, 0);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 5 * 4, 3 * 4);

		bindGeometry(dh.colors);
		GL20.glVertexAttribPointer(2, 1, GL11.GL_FLOAT, false, 0, 0);
	}

	private void fillUnifiedFormat(UnifiedDrawHandle uh) {
		bindGeometry(uh.vertices);
		GL20.glEnableVertexAttribArray(0);

		if(uh.texture!=null) {
			GL20.glEnableVertexAttribArray(1);

			GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 4 * 4, 0);
			GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 4 * 4, 2 * 4);
		} else {
			GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);
		}
	}

	private void fillMultiFormat(MultiDrawHandle mh) {
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);

		bindGeometry(mh.drawCalls);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 12*4, 0);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 12*4, 3*4);
		GL20.glVertexAttribPointer(2, 4, GL11.GL_FLOAT, false, 12*4, 5*4);
		GL20.glVertexAttribPointer(3, 3, GL11.GL_FLOAT, false, 12*4, 9*4);
	}

	private boolean[] vertArrays = new boolean[4];

	public void enableVertArrays(boolean... vertArrays) {
		for(int i = 0;i != vertArrays.length; i++) {
			if(vertArrays[i] != this.vertArrays[i]) {
				if(vertArrays[i]) {
					GL20.glEnableVertexAttribArray(i);
				} else {
					GL20.glDisableVertexAttribArray(i);
				}
			}
		}

		this.vertArrays = vertArrays;
	}

	protected void drawMulti(MultiDrawHandle call) {
		bindTexture(call.sourceQuads.texture);

		if(call.getVertexArrayId() != -1) {
			bindFormat(call.getVertexArrayId());
		} else {
			enableVertArrays(true, true, true, true);
			fillMultiFormat(call);
		}

		useProgram(prog_unified_multi);

		ARBUniformBufferObject.glBindBufferBase(GL31.GL_UNIFORM_BUFFER, 0, call.sourceQuads.vertices.getBufferId());

		GL11.glDrawArrays(GL11.GL_POINTS, 0, call.used);
	}

	public void drawUnifiedArray(UnifiedDrawHandle call, int primitive, int vertexCount, float[] trans, float[] colors, int array_len) {
		if(call.texture != null) bindTexture(call.texture);

		if(call.getVertexArrayId() != -1) {
			bindFormat(call.getVertexArrayId());
		} else {
			enableVertArrays(true, call.texture!=null, false, false);
			fillUnifiedFormat(call);
		}

		if(prog_unified_array != null) {
			useProgram(prog_unified_array);

			GL20.glUniform4fv(prog_unified_array.color, colors);
			GL20.glUniform4fv(prog_unified_array.trans, trans);

			ARBDrawInstanced.glDrawArraysInstancedARB(primitive, call.offset, vertexCount, array_len);
		} else {
			useProgram(prog_unified);

			for (int i = 0; i != array_len; i++) {

				float intensity = 0;
				int mode = 0;

				GL20.glUniform1i(prog_unified.mode, mode);
				GL20.glUniform1fv(prog_unified.color, new float[]{colors[i*4], colors[i*4+1], colors[i*4+2], colors[i*4+3], intensity});
				GL20.glUniform3fv(prog_unified.trans, new float[]{trans[i*6], trans[i*6+1], trans[i*6+2], trans[i*6+3], trans[i*6+4], trans[i*6+5]});

				GL11.glDrawArrays(primitive, call.offset, vertexCount);
			}

			ulr = -1;
			ulm = -1;
		}
	}

	@Override
	public void drawUnified(UnifiedDrawHandle call, int primitive, int count, int mode, float x, float y, float z, float sx, float sy, AbstractColor color, float intensity) {
		if(call.texture != null) bindTexture(call.texture);
		useProgram(prog_unified);

		if(call.getVertexArrayId() != -1) {
			bindFormat(call.getVertexArrayId());
		} else {
			enableVertArrays(true, call.texture!=null, false, false);
			fillUnifiedFormat(call);
		}

		float r, g, b, a;
		if (color != null) {
			r = color.red;
			g = color.green;
			b = color.blue;
			a = color.alpha;
		} else {
			r = g = b = a = 1;
		}

		if(ulr != r || ulg != g || ulb != b || ula != a || uli != intensity) {
			ulr = r;
			ulg = g;
			ulb = b;
			ula = a;
			uli = intensity;
			GL20.glUniform1fv(prog_unified.color, new float[] {r, g, b, a, intensity});
		}


		if(ulm != mode) {
			ulm = mode;
			GL20.glUniform1i(prog_unified.mode, mode);
		}

		GL20.glUniform3fv(prog_unified.trans, new float[] {x, y, z, sx, sy, 0});

		GL11.glDrawArrays(primitive, call.offset, count);
	}

	public void drawBackground(BackgroundDrawHandle handle) {
		bindTexture(handle.texture);
		useProgram(prog_background);
		if(handle.getVertexArrayId() != -1) {
			bindFormat(handle.getVertexArrayId());
		} else {
			enableVertArrays(true, true, true, false);
			fillBackgroundFormat(handle);
		}

		int starti = handle.offset < 0 ? (int)Math.ceil(-handle.offset/(float)handle.stride) : 0;
		int draw_lines = handle.lines-starti;

		int[] firsts = new int[draw_lines];
		int[] counts = new int[draw_lines];
		for (int i = 0; i != draw_lines; i++) {
			firsts[i] = (handle.offset + handle.stride * (i+starti)) * 3;
		}
		Arrays.fill(counts, handle.width*3);

		GL14.glMultiDrawArrays(GL11.GL_TRIANGLES, firsts, counts);
	}

	protected class ShaderProgram  {
		public final int program;

		public final int proj;
		public final int global;
		public final int trans;
		public final int tex;
		public final int color;
		public final int height;
		public final int mode;
		public final int shadow_depth;
		public final int geometry_data;


		protected ShaderProgram(String name) {
			int vertexShader = -1;
			int geometryShader = -1;
			int fragmentShader;


			try {
				vertexShader = createShader(name+".vert", GL20.GL_VERTEX_SHADER);
				geometryShader = createShader(name+".geom", GL32.GL_GEOMETRY_SHADER);
				fragmentShader = createShader(name+".frag", GL20.GL_FRAGMENT_SHADER);
			} catch (IOException e) {
				e.printStackTrace();

				if(vertexShader != -1) GL20.glDeleteShader(vertexShader);
				if(geometryShader != -1) GL20.glDeleteShader(geometryShader);
				throw new Error("could not read shader files", e);
			}

			program = GL20.glCreateProgram();
			setObjectLabel(KHRDebug.GL_PROGRAM, program, name);

			GL20.glAttachShader(program, vertexShader);
			if(geometryShader != -1) GL20.glAttachShader(program, geometryShader);
			GL20.glAttachShader(program, fragmentShader);

			for(int i = 0; i != attributes.size(); i++) {
				GL20.glBindAttribLocation(program, i, attributes.get(i));
			}

			GL20.glLinkProgram(program);
			GL20.glValidateProgram(program);

			GL20.glDetachShader(program, vertexShader);
			if(geometryShader != -1) GL20.glDetachShader(program, geometryShader);
			GL20.glDetachShader(program, fragmentShader);
			GL20.glDeleteShader(vertexShader);
			if(geometryShader != -1) GL20.glDeleteShader(geometryShader);
			GL20.glDeleteShader(fragmentShader);

			String log = GL20.glGetProgramInfoLog(program);
			if(debugOutput != null && !log.isEmpty()) System.out.print("info log of " + name + "=====\n" + log + "==== end\n");

			if(GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == 0) {

				GL20.glDeleteProgram(program);
				throw new Error("Could not link " + name);
			}

			proj = GL20.glGetUniformLocation(program, "projection");
			global = GL20.glGetUniformLocation(program, "globalTransform");
			trans = GL20.glGetUniformLocation(program, "transform");
			tex = GL20.glGetUniformLocation(program, "texHandle");
			color = GL20.glGetUniformLocation(program, "color");
			height = GL20.glGetUniformLocation(program, "height");
			mode = GL20.glGetUniformLocation(program, "mode");
			shadow_depth = GL20.glGetUniformLocation(program, "shadow_depth");

			if(glcaps.GL_ARB_uniform_buffer_object) {
				geometry_data = ARBUniformBufferObject.glGetUniformBlockIndex(program, "geometryDataBuffer");
				if (geometry_data != -1) ARBUniformBufferObject.glUniformBlockBinding(program, geometry_data, 0);
			} else {
				geometry_data = -1;
			}

			useProgram(this);
			if(tex != -1) GL20.glUniform1i(tex, 0);

			shaders.add(this);
		}

		private ArrayList<String> attributes = new ArrayList<>();

		private int createShader(String name, int type) throws IOException {
			int shader = GL20.glCreateShader(type);
			if(shader == 0) return -1;
			setObjectLabel(KHRDebug.GL_SHADER, shader, name);

			InputStream shaderFile = getClass().getResourceAsStream("/"+name);
			if(shaderFile == null) return -1;
			BufferedReader is = new BufferedReader(new InputStreamReader(shaderFile));
			StringBuilder source = new StringBuilder();
			String line;

			while((line = is.readLine()) != null) {
				if(line.startsWith("attribute") || line.endsWith("//attribute")) {
					attributes.add(line.split(" ")[2].replaceAll(";", ""));
				}

				source.append(line).append("\n");
			}

			GL20.glShaderSource(shader, source);
			GL20.glCompileShader(shader);


			String log = GL20.glGetShaderInfoLog(shader);
			if(debugOutput != null && !log.isEmpty()) System.out.print("info log of " + name + "=====\n" + log + "==== end\n");

			if(GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == 0) {

				GL20.glDeleteShader(shader);
				throw new Error("Could not compile " + name);
			}

			return shader;
		}
	}

	
	public void clearDepthBuffer() {
		finishFrame();
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
	}
}
