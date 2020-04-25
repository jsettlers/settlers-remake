package go.graphics.swing.opengl;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
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

import static org.lwjgl.opengl.ARBDrawInstanced.*;
import static org.lwjgl.opengl.ARBVertexArrayObject.*;
import static org.lwjgl.opengl.ARBUniformBufferObject.*;
import static org.lwjgl.opengl.GL20C.*;

public class LWJGLDrawContext extends GLDrawContext {
	public LWJGLDrawContext(GLCapabilities glcaps, boolean debug, float guiScale) {
		this.glcaps = glcaps;
		shaders = new ArrayList<>();

		if(debug) debugOutput = new LWJGLDebugOutput(this);

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);

		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

		if(glcaps.OpenGL32) prog_unified_multi = new ShaderProgram("unified-multi");
		if(glcaps.GL_EXT_draw_instanced) prog_unified_array = new ShaderProgram("unified-array");
		prog_background = new ShaderProgram("background");
		prog_unified = new ShaderProgram("unified");

		textDrawer = new LWJGLTextDrawer(this, guiScale);
	}

	private ArrayList<ShaderProgram> shaders;

	private final Matrix4f global = new Matrix4f();
	private final Matrix4f mat = new Matrix4f();
	private final FloatBuffer matBfr = BufferUtils.createFloatBuffer(16);
	private LWJGLDebugOutput debugOutput = null;

	final GLCapabilities glcaps;

	private BufferHandle lastGeometry = null;
	private TextureHandle lastTexture = null;

	private ShaderProgram lastProgram = null;
	private void useProgram(ShaderProgram id) {
		if(id != lastProgram) {
			glUseProgram(id.program);
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
		int texture = glGenTextures();
		if (texture == 0) {
			return null;
		}

		TextureHandle textureHandle = new TextureHandle(this, texture);
		resizeTexture(textureHandle, width, height, data);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		setObjectLabel(GL11.GL_TEXTURE, texture, name + "-tex");

		return textureHandle;
	}

	public void resizeTexture(TextureHandle textureIndex, int width, int height, ShortBuffer data) {
		bindTexture(textureIndex);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_SHORT_4_4_4_4, data);
	}
	
	public void updateTexture(TextureHandle texture, int left, int bottom,
							  int width, int height, ShortBuffer data) {
		bindTexture(texture);
		glTexSubImage2D(GL_TEXTURE_2D, 0, left, bottom, width, height,
				GL_RGBA, GL_UNSIGNED_SHORT_4_4_4_4, data);
	}

	private void bindTexture(TextureHandle texture) {
		if(lastTexture != texture) {
			int id = 0;
			if (texture != null) {
				id = texture.getTextureId();
			}
			glBindTexture(GL_TEXTURE_2D, id);
			lastTexture = texture;
		}
	}

	private void bindGeometry(BufferHandle geometry) {
		if(lastGeometry != geometry) {
			int id = 0;
			if (geometry != null) {
				id = geometry.getBufferId();
			}
			glBindBuffer(GL_ARRAY_BUFFER, id);
			lastGeometry = geometry;
		}
	}

	private int lastFormat = 0;
	private void bindFormat(int format) {
		if(format != lastFormat) {
			glBindVertexArray(format);
			lastFormat = format;
		}
	}
	
	public void updateBufferAt(BufferHandle handle, int pos, ByteBuffer data) {
		bindGeometry(handle);
		glBufferSubData(GL_ARRAY_BUFFER, pos, data);
	}

	private void setObjectLabel(int type, int id, String name) {
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
			glUniformMatrix4fv(shader.global, false, matBfr);
		}
	}

	private float nativeScale = 0;

	public void resize(int width, int height) {
		if(nativeScale == 0) {
			int[] vp = new int[4];
			glGetIntegerv(GL_VIEWPORT, vp);
			nativeScale = vp[2] / (float)width;
		}

		glViewport(0, 0, (int)(width*nativeScale), (int)(height*nativeScale));
		mat.setOrtho(0, width, 0, height, -1, 1);
		mat.get(matBfr);

		for(ShaderProgram shader : shaders) {
			useProgram(shader);
			glUniformMatrix4fv(shader.proj, false, matBfr);
		}
	}

	
	public void setShadowDepthOffset(float depth) {
		for(ShaderProgram shader : shaders) {
			if(shader.shadow_depth != -1) {
				useProgram(shader);
				glUniform1f(shader.shadow_depth, depth);

			}
		}
	}

	
	public void setHeightMatrix(float[] matrix) {
		useProgram(prog_background);
		glUniformMatrix4fv(prog_background.height, false, matrix);
	}

	@Override
	public BackgroundDrawHandle createBackgroundDrawCall(int vertices, TextureHandle texture) {
		int vao = -1;

		if(glcaps.GL_ARB_vertex_array_object) vao = glGenVertexArrays();

		BufferHandle vertexBuffer = new BufferHandle(this, glGenBuffers());
		BufferHandle colorBuffer = new BufferHandle(this, glGenBuffers());

		bindGeometry(vertexBuffer);
		setObjectLabel(KHRDebug.GL_BUFFER, vertexBuffer.getBufferId(), "background-shape");
		glBufferData(GL_ARRAY_BUFFER, vertices*5*4, GL_DYNAMIC_DRAW);
		bindGeometry(colorBuffer);
		setObjectLabel(KHRDebug.GL_BUFFER, colorBuffer.getBufferId(), "background-color");
		glBufferData(GL_ARRAY_BUFFER, vertices*4, GL_DYNAMIC_DRAW);

		BackgroundDrawHandle handle = new BackgroundDrawHandle(this, vao, texture, vertexBuffer, colorBuffer);

		if(glcaps.GL_ARB_vertex_array_object) {
			bindFormat(vao);
			setObjectLabel(GL_VERTEX_ARRAY, vao, "background-vao");
			fillBackgroundFormat(handle);
		}

		return handle;
	}

	@Override
	public UnifiedDrawHandle createUnifiedDrawCall(int vertices, String name, TextureHandle texture, float[] data) {
		int vao = -1;

		if(glcaps.GL_ARB_vertex_array_object) vao = glGenVertexArrays();

		BufferHandle vertexBuffer = new BufferHandle(this, glGenBuffers());

		bindGeometry(vertexBuffer);
		setObjectLabel(KHRDebug.GL_BUFFER, vertexBuffer.getBufferId(), name + "-vertices");
		if(data != null) {
			glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
		} else {
			glBufferData(GL_ARRAY_BUFFER, vertices*(texture!=null?4:2)*4, GL_DYNAMIC_DRAW);
		}

		UnifiedDrawHandle handle = new UnifiedDrawHandle(this, vao, 0, vertices, texture, vertexBuffer);

		if(glcaps.GL_ARB_vertex_array_object) {
			bindFormat(vao);
			setObjectLabel(GL_VERTEX_ARRAY, vao, name + "-vao");
			fillUnifiedFormat(handle);
		}

		return handle;
	}

	@Override
	protected MultiDrawHandle createMultiDrawCall(String name, ManagedHandle source) {
		if(prog_unified_multi == null) return null;

		int vao = -1;

		if(glcaps.GL_ARB_vertex_array_object) vao = glGenVertexArrays();

		BufferHandle drawCalls = new BufferHandle(this, glGenBuffers());

		bindGeometry(drawCalls);
		setObjectLabel(KHRDebug.GL_BUFFER, drawCalls.getBufferId(), name + "-drawcalls");
		glBufferData(GL_ARRAY_BUFFER, MultiDrawHandle.MAX_CACHE_ENTRIES*12*4, GL_STREAM_DRAW);

		MultiDrawHandle handle = new MultiDrawHandle(this, vao, MultiDrawHandle.MAX_CACHE_ENTRIES, source, drawCalls);

		if(glcaps.GL_ARB_vertex_array_object) {
			bindFormat(vao);
			setObjectLabel(GL_VERTEX_ARRAY, vao, name + "-vao");
			fillMultiFormat(handle);
		}

		return handle;
	}

	private void fillBackgroundFormat(BackgroundDrawHandle dh) {
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);

		bindGeometry(dh.vertices);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * 4, 0);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * 4, 3 * 4);

		bindGeometry(dh.colors);
		glVertexAttribPointer(2, 1, GL_FLOAT, false, 0, 0);
	}

	private void fillUnifiedFormat(UnifiedDrawHandle uh) {
		bindGeometry(uh.vertices);
		glEnableVertexAttribArray(0);

		if(uh.texture!=null) {
			glEnableVertexAttribArray(1);

			glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * 4, 0);
			glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * 4, 2 * 4);
		} else {
			glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
		}
	}

	private void fillMultiFormat(MultiDrawHandle mh) {
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);

		bindGeometry(mh.drawCalls);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 12*4, 0);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 12*4, 3*4);
		glVertexAttribPointer(2, 4, GL_FLOAT, false, 12*4, 5*4);
		glVertexAttribPointer(3, 3, GL_FLOAT, false, 12*4, 9*4);
	}

	private boolean[] vertArrays = new boolean[4];

	private void enableVertArrays(boolean... vertArrays) {
		for(int i = 0;i != vertArrays.length; i++) {
			if(vertArrays[i] != this.vertArrays[i]) {
				if(vertArrays[i]) {
					glEnableVertexAttribArray(i);
				} else {
					glDisableVertexAttribArray(i);
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

		glBindBufferBase(GL_UNIFORM_BUFFER, 0, call.sourceQuads.vertices.getBufferId());

		glDrawArrays(GL_POINTS, 0, call.used);
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

			glUniform4fv(prog_unified_array.color, colors);
			glUniform4fv(prog_unified_array.trans, trans);

			glDrawArraysInstancedARB(primitive, call.offset, vertexCount, array_len);
		} else {
			useProgram(prog_unified);

			for (int i = 0; i != array_len; i++) {

				float int_mode = trans[i*4+3]/10;
				int mode = (int) Math.floor(int_mode);
				float intensity = (int_mode-mode)*10-1;

				glUniform1i(prog_unified.mode, mode);
				glUniform1fv(prog_unified.color, new float[] {colors[i*4], colors[i*4+1], colors[i*4+2], colors[i*4+3], intensity});
				glUniform3fv(prog_unified.trans, new float[] {trans[i*4], trans[i*4+1], trans[i*4+2], 1, 1, 0});

				glDrawArrays(primitive, call.offset, vertexCount);
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
			glUniform1fv(prog_unified.color, new float[] {r, g, b, a, intensity});
		}


		if(ulm != mode) {
			ulm = mode;
			glUniform1i(prog_unified.mode, mode);
		}

		glUniform3fv(prog_unified.trans, new float[] {x, y, z, sx, sy, 0});

		glDrawArrays(primitive, call.offset, count);
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

		glMultiDrawArrays(GL_TRIANGLES, firsts, counts);
	}

	@SuppressWarnings("WeakerAccess")
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
				vertexShader = createShader(name+".vert", GL_VERTEX_SHADER);
				geometryShader = createShader(name+".geom", GL32.GL_GEOMETRY_SHADER);
				fragmentShader = createShader(name+".frag", GL_FRAGMENT_SHADER);
			} catch (IOException e) {
				e.printStackTrace();

				if(vertexShader != -1) glDeleteShader(vertexShader);
				if(geometryShader != -1) glDeleteShader(geometryShader);
				throw new Error("could not read shader files", e);
			}

			program = glCreateProgram();
			setObjectLabel(KHRDebug.GL_PROGRAM, program, name);

			glAttachShader(program, vertexShader);
			if(geometryShader != -1) glAttachShader(program, geometryShader);
			glAttachShader(program, fragmentShader);

			for(int i = 0; i != attributes.size(); i++) {
				glBindAttribLocation(program, i, attributes.get(i));
			}

			glLinkProgram(program);
			glValidateProgram(program);

			glDetachShader(program, vertexShader);
			if(geometryShader != -1) glDetachShader(program, geometryShader);
			glDetachShader(program, fragmentShader);
			glDeleteShader(vertexShader);
			if(geometryShader != -1) glDeleteShader(geometryShader);
			glDeleteShader(fragmentShader);

			String log = glGetProgramInfoLog(program);
			if(debugOutput != null && !log.isEmpty()) System.out.print("info log of " + name + "=====\n" + log + "==== end\n");

			if(glGetProgrami(program, GL_LINK_STATUS) == 0) {

				glDeleteProgram(program);
				throw new Error("Could not link " + name);
			}

			proj = glGetUniformLocation(program, "projection");
			global = glGetUniformLocation(program, "globalTransform");
			trans = glGetUniformLocation(program, "transform");
			tex = glGetUniformLocation(program, "texHandle");
			color = glGetUniformLocation(program, "color");
			height = glGetUniformLocation(program, "height");
			mode = glGetUniformLocation(program, "mode");
			shadow_depth = glGetUniformLocation(program, "shadow_depth");

			if(glcaps.GL_ARB_uniform_buffer_object) {
				geometry_data = glGetUniformBlockIndex(program, "geometryDataBuffer");
				if (geometry_data != -1) glUniformBlockBinding(program, geometry_data, 0);
			} else {
				geometry_data = -1;
			}

			useProgram(this);
			if(tex != -1) glUniform1i(tex, 0);

			shaders.add(this);
		}

		private ArrayList<String> attributes = new ArrayList<>();

		private int createShader(String name, int type) throws IOException {
			StringBuilder source = new StringBuilder();
			try(InputStream shaderFile = getClass().getResourceAsStream("/"+name)) {
				if (shaderFile == null) return -1;
				BufferedReader is = new BufferedReader(new InputStreamReader(shaderFile));

				String line;
				while ((line = is.readLine()) != null) {
					if (line.startsWith("attribute") || line.endsWith("//attribute")) {
						attributes.add(line.split(" ")[2].replaceAll(";", ""));
					}

					source.append(line).append("\n");
				}
			}

			int shader = glCreateShader(type);
			if (shader == 0) return -1;
			setObjectLabel(KHRDebug.GL_SHADER, shader, name);
			glShaderSource(shader, source);
			glCompileShader(shader);


			String log = glGetShaderInfoLog(shader);
			if(debugOutput != null && !log.isEmpty()) System.out.print("info log of " + name + "=====\n" + log + "==== end\n");

			if(glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {

				glDeleteShader(shader);
				throw new Error("Could not compile " + name);
			}

			return shader;
		}
	}

	
	public void clearDepthBuffer() {
		finishFrame();
		glClear(GL_DEPTH_BUFFER_BIT);
	}

	@Override
	public void startFrame() {
		super.startFrame();
		glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
	}
}
