package go.graphics.android;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;

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

@SuppressWarnings("WeakerAccess")
public class GLESDrawContext extends GLDrawContext {
	private final Context context;
	private BufferHandle lastGeometry = null;
	private TextureHandle lastTexture = null;

	GLESDrawContext(Context ctx, boolean gles3, boolean gles32) {
		this.context = ctx;
		this.gles32 = gles32;
		this.gles3 = gles3;
		GLES20.glClearColor(0, 0, 0, 1);
		GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);

		init();
		Matrix.setIdentityM(global, 0);

		textDrawer = new AndroidTextDrawer(this);
	}

	private ArrayList<ShaderProgram> shaders;

	private final float[] global = new float[16];
	private final float[] mat = new float[16];
	private final boolean gles3;
	private final boolean gles32;

	public void init() {
		shaders = new ArrayList<>();

		if(gles32) prog_unified_multi = new ShaderProgram("unified-multi");
		if(gles3) prog_unified_array = new ShaderProgram("unified-array");
		prog_background = new ShaderProgram("background");
		prog_unified = new ShaderProgram("unified");
	}

	private ShaderProgram lastProgram = null;
	void useProgram(ShaderProgram id) {
		if(id != lastProgram) {
			GLES20.glUseProgram(id.program);
			lastProgram = id;
		}
	}

	private ShaderProgram prog_unified_multi = null;
	private ShaderProgram prog_unified_array = null;
	private ShaderProgram prog_background;
	private ShaderProgram prog_unified;

	private float ulr, ulg, ulb, ula, uli;
	private int ulm;

	/**
	 * Returns a texture id which is positive or 0. It returns a negative number on error.
	 *
	 * @param width
	 * @param height
	 *            The height of the image.
	 * @param data
	 *            The data as array. It needs to have a length of width * height and each element is a color with: 4 bits red, 4 bits green, 4 bits
	 *            blue and 4 bits alpha.
	 * @return The id of the generated texture.
	 */
	public TextureHandle generateTexture(int width, int height, ShortBuffer data, String name) {
		int[] textureIndexes = new int[1];
		GLES20.glGenTextures(1, textureIndexes, 0);
		TextureHandle texture = new TextureHandle(this, textureIndexes[0]);

		bindTexture(texture);
		resizeTexture(texture, width, height, data);

		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
		return texture;
	}

	public void resizeTexture(TextureHandle textureIndex, int width, int height, ShortBuffer data) {
		bindTexture(textureIndex);
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_SHORT_4_4_4_4, data);
	}

	public void updateTexture(TextureHandle textureIndex, int left, int bottom,
							  int width, int height, ShortBuffer data) {
		bindTexture(textureIndex);
		GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, left, bottom, width, height,
				GLES20.GL_RGBA, GLES20.GL_UNSIGNED_SHORT_4_4_4_4, data);
	}

	void bindTexture(TextureHandle texture) {
		if (texture != lastTexture) {
			int id = 0;
			if (texture != null) {
				id = texture.getTextureId();
			}
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id);
			lastTexture = texture;
		}
	}

	void bindGeometry(BufferHandle geometry) {
		if(geometry != lastGeometry) {
			int id = 0;
			if(geometry != null) {
				id = geometry.getBufferId();
			}
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, id);
			lastGeometry = geometry;
		}
	}

	private int lastFormat = 0;
	void bindFormat(int format) {
		if(format != lastFormat) {
			GLES30.glBindVertexArray(format);
			lastFormat = format;
		}
	}

	public void updateBufferAt(BufferHandle handle, int pos, ByteBuffer data) {
		bindGeometry(handle);
		GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, pos, data.remaining(), data);
	}

	public void setGlobalAttributes(float x, float y, float z, float sx, float sy, float sz) {
		finishFrame();

		Matrix.setIdentityM(global, 0);
		Matrix.scaleM(global, 0, sx, sy, sz);
		Matrix.translateM(global, 0, x, y, z);

		for(ShaderProgram shader : shaders) {
			useProgram(shader);
			GLES20.glUniformMatrix4fv(shader.global, 1, false, global, 0);
		}
	}

	void resize(int width, int height) {
		GLES20.glViewport(0, 0, width, height);

		Matrix.setIdentityM(mat, 0);
		Matrix.orthoM(mat, 0, 0, width, 0, height, -1, 1);

		for(ShaderProgram shader : shaders) {
			useProgram(shader);
			GLES20.glUniformMatrix4fv(shader.proj, 1, false, mat, 0);
		}
	}

	@Override
	public void setShadowDepthOffset(float depth) {
		for(ShaderProgram shader : shaders) {
			if(shader.shadow_depth != -1) {
				useProgram(shader);
				GLES20.glUniform1f(shader.shadow_depth, depth);

			}
		}
	}

	public void setHeightMatrix(float[] matrix) {
		useProgram(prog_background);
		GLES20.glUniformMatrix4fv(prog_background.height, 1, false, matrix, 0);
	}

	Context getAndroidContext() {
		return context;
	}

	public int genBuffer() {
		int[] buffer = new int[1];
		GLES20.glGenBuffers(1, buffer, 0);
		return buffer[0];
	}

	public int genVertexArray() {
		int[] vertexArray = new int[1];
		GLES30.glGenVertexArrays(1, vertexArray, 0);
		return  vertexArray[0];
	}

	@Override
	public BackgroundDrawHandle createBackgroundDrawCall(int vertices, TextureHandle texture) {
		int vao = -1;

		if(gles3) vao = genVertexArray();

		BufferHandle vertexBuffer = new BufferHandle(this, genBuffer());
		BufferHandle colorBuffer = new BufferHandle(this, genBuffer());

		bindGeometry(vertexBuffer);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, (vertices*5*4), null, GLES20.GL_DYNAMIC_DRAW);
		bindGeometry(colorBuffer);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, (vertices*4), null, GLES20.GL_DYNAMIC_DRAW);

		BackgroundDrawHandle handle = new BackgroundDrawHandle(this, vao, texture, vertexBuffer, colorBuffer);

		if(gles3) {
			bindFormat(vao);
			fillBackgroundFormat(handle);
		}

		return handle;
	}

	@Override
	public UnifiedDrawHandle createUnifiedDrawCall(int vertices, String name, TextureHandle texture, float[] data) {
		int vao = -1;

		if(gles3) vao = genVertexArray();

		BufferHandle vertexBuffer = new BufferHandle(this, genBuffer());

		bindGeometry(vertexBuffer);
		if(data != null) {
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, data.length*4, FloatBuffer.wrap(data), GLES20.GL_STATIC_DRAW);
		} else {
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertices*(texture!=null?4:2)*4*4, null, GLES20.GL_DYNAMIC_DRAW);
		}

		UnifiedDrawHandle handle = new UnifiedDrawHandle(this, vao, 0, vertices, texture, vertexBuffer);

		if(gles3) {
			bindFormat(vao);
			fillUnifiedFormat(handle);
		}

		return handle;
	}

	@Override
	protected MultiDrawHandle createMultiDrawCall(String name, ManagedHandle source) {
		if(prog_unified_multi == null) return null;
		int vao = -1;

		if(gles3) vao = genVertexArray();

		BufferHandle drawCalls = new BufferHandle(this, genBuffer());

		bindGeometry(drawCalls);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, MultiDrawHandle.MAX_CACHE_ENTRIES*12*4, null, GLES20.GL_STREAM_DRAW);

		MultiDrawHandle handle = new MultiDrawHandle(this, vao, MultiDrawHandle.MAX_CACHE_ENTRIES, source, drawCalls);

		if(gles3) {
			bindFormat(vao);
			fillMultiFormat(handle);
		}

		return handle;
	}

	private void fillBackgroundFormat(BackgroundDrawHandle dh) {
		GLES20.glEnableVertexAttribArray(0);
		GLES20.glEnableVertexAttribArray(1);
		GLES20.glEnableVertexAttribArray(2);

		bindGeometry(dh.vertices);
		GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 5 * 4, 0);
		GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 5 * 4, 3 * 4);

		bindGeometry(dh.colors);
		GLES20.glVertexAttribPointer(2, 1, GLES20.GL_FLOAT, false, 0, 0);
	}

	private void fillUnifiedFormat(UnifiedDrawHandle uh) {
		bindGeometry(uh.vertices);
		GLES20.glEnableVertexAttribArray(0);

		if(uh.texture!=null) {
			GLES20.glEnableVertexAttribArray(1);

			GLES20.glVertexAttribPointer(0, 2, GLES20.GL_FLOAT, false, 4 * 4, 0);
			GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 4 * 4, 2 * 4);
		} else {
			GLES20.glVertexAttribPointer(0, 2, GLES20.GL_FLOAT, false, 0, 0);
		}
	}

	private void fillMultiFormat(MultiDrawHandle mh) {
		GLES20.glEnableVertexAttribArray(0);
		GLES20.glEnableVertexAttribArray(1);
		GLES20.glEnableVertexAttribArray(2);
		GLES20.glEnableVertexAttribArray(3);

		bindGeometry(mh.drawCalls);
		GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 12*4, 0);
		GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 12*4, 3*4);
		GLES20.glVertexAttribPointer(2, 4, GLES20.GL_FLOAT, false, 12*4, 5*4);
		GLES20.glVertexAttribPointer(3, 3, GLES20.GL_FLOAT, false, 12*4, 9*4);
	}

	private boolean[] vertArrays = new boolean[3];

	public void enableVertArrays(boolean... vertArrays) {
		for(int i = 0;i != vertArrays.length; i++) {
			if(vertArrays[i] != this.vertArrays[i]) {
				if(vertArrays[i]) {
					GLES20.glEnableVertexAttribArray(i);
				} else {
					GLES20.glDisableVertexAttribArray(i);
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

		GLES30.glBindBufferBase(GLES30.GL_UNIFORM_BUFFER, 0, call.sourceQuads.vertices.getBufferId());

		GLES30.glDrawArrays(GLES30.GL_POINTS, 0, call.used);
	}

	public void drawUnifiedArray(UnifiedDrawHandle call, int primitive, int vertexCount, float[] trans, float[] colors, int array_len) {
		if(call.texture != null) bindTexture(call.texture);

		if(call.getVertexArrayId() != -1) {
			bindFormat(call.getVertexArrayId());
		} else {
			enableVertArrays(true, call.texture!=null, false);
			fillUnifiedFormat(call);
		}

		if(prog_unified_array != null) {
			useProgram(prog_unified_array);

			GLES20.glUniform4fv(prog_unified_array.color, array_len, colors, 0);
			GLES20.glUniform4fv(prog_unified_array.trans, array_len, trans, 0);

			GLES30.glDrawArraysInstanced(primitive, call.offset, vertexCount, array_len);
		} else {
			useProgram(prog_unified);

			for (int i = 0; i != array_len; i++) {

				float int_mode = trans[i*4+3]/10;
				int mode = (int) Math.floor(int_mode);
				float intensity = (int_mode-mode)*10-1;

				GLES20.glUniform1i(prog_unified.mode, mode);
				GLES20.glUniform1fv(prog_unified.color, 4, new float[] {colors[i*4], colors[i*4+1], colors[i*4+2], colors[i*4+3], intensity}, i * 4);
				GLES20.glUniform3fv(prog_unified.trans, 2, new float[] {trans[i*4], trans[i*4+1], trans[i*4+2], 1, 1, 0}, 0);

				GLES20.glDrawArrays(primitive, call.offset, vertexCount);
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
			enableVertArrays(true, call.texture!=null, false);
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
			GLES20.glUniform1fv(prog_unified.color, 5, new float[] {r, g, b, a, intensity}, 0);
		}

		if(ulm != mode) {
			ulm = mode;
			GLES20.glUniform1i(prog_unified.mode, mode);
		}

		GLES20.glUniform3fv(prog_unified.trans, 2, new float[] {x, y, z, sx, sy, 0}, 0);

		GLES20.glDrawArrays(primitive, call.offset, count);
	}

	public void drawBackground(BackgroundDrawHandle handle) {
		bindTexture(handle.texture);
		useProgram(prog_background);
		if(handle.getVertexArrayId() != -1) {
			bindFormat(handle.getVertexArrayId());
		} else {
			enableVertArrays(true, true, true);
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

		for(int i = 0; i != draw_lines; i++) {
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, firsts[i], counts[i]);
		}
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

		ShaderProgram(String name) {
			int vertexShader = -1;
			int geometryShader = -1;
			int fragmentShader;

			try {
				vertexShader = createShader(name+".vert", GLES20.GL_VERTEX_SHADER);
				geometryShader = createShader(name+".geom", 36313 /*GL_GEOMETRY_SHADER*/);
				fragmentShader = createShader(name+".frag", GLES20.GL_FRAGMENT_SHADER);
			} catch (IOException e) {
				e.printStackTrace();

				if(vertexShader != -1) GLES20.glDeleteShader(vertexShader);
				if(geometryShader != -1) GLES20.glDeleteShader(geometryShader);
				throw new Error("could not read shader files", e);
			}

			program = GLES20.glCreateProgram();

			GLES20.glAttachShader(program, vertexShader);
			if(geometryShader != -1) GLES20.glAttachShader(program, geometryShader);
			GLES20.glAttachShader(program, fragmentShader);

			for(int i = 0; i != attributes.size(); i++) {
				GLES20.glBindAttribLocation(program, i, attributes.get(i));
			}

			GLES20.glLinkProgram(program);
			GLES20.glValidateProgram(program);

			GLES20.glDetachShader(program, vertexShader);
			if(geometryShader != -1) GLES20.glDetachShader(program, geometryShader);
			GLES20.glDetachShader(program, fragmentShader);
			GLES20.glDeleteShader(vertexShader);
			if(geometryShader != -1) GLES20.glDeleteShader(geometryShader);
			GLES20.glDeleteShader(fragmentShader);

			String log = GLES20.glGetProgramInfoLog(program);
			if(!log.isEmpty()) System.out.print("info log of " + name + "=====\n" + log + "==== end\n");

			int[] link_status = new int[1];
			GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, link_status, 0);
			if(link_status[0] == 0) {
				GLES20.glDeleteProgram(program);
				throw new Error("Could not link " + name);
			}

			proj = GLES20.glGetUniformLocation(program, "projection");
			global = GLES20.glGetUniformLocation(program, "globalTransform");
			trans = GLES20.glGetUniformLocation(program, "transform");
			tex = GLES20.glGetUniformLocation(program, "texHandle");
			color = GLES20.glGetUniformLocation(program, "color");
			height = GLES20.glGetUniformLocation(program, "height");
			mode = GLES20.glGetUniformLocation(program, "mode");
			shadow_depth = GLES20.glGetUniformLocation(program, "shadow_depth");

			if(gles3) {
				geometry_data = GLES30.glGetUniformBlockIndex(program, "geometryDataBuffer");
				if (geometry_data != -1) GLES30.glUniformBlockBinding(program, geometry_data, 0);
			} else {
				geometry_data = -1;
			}

			useProgram(this);
			if(tex != -1) GLES20.glUniform1i(tex, 0);

			shaders.add(this);
		}

		private ArrayList<String> attributes = new ArrayList<>();

		private final String vendor_id = "//VENDOR=" +GLES20.glGetString(GLES20.GL_VENDOR) + " ";

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

					int vendor_index = line.indexOf(vendor_id);
					if (vendor_index != -1) {
						String remaining = line.substring(vendor_index + vendor_id.length());
						String[] replace = remaining.split("=");
						line = line.substring(0, vendor_index).replaceFirst(replace[0], replace[1]);
					}

					source.append(line);
					if (line.startsWith("#version")) {
						//source.append(" es");
					}
					source.append("\n");
				}
			}


			int shader = GLES20.glCreateShader(type);
			if (shader == 0) return -1;
			GLES20.glShaderSource(shader, source.toString());
			GLES20.glCompileShader(shader);


			String log = GLES20.glGetShaderInfoLog(shader);
			if(!log.isEmpty()) System.out.print("info log of " + name + "=====\n" + log + "==== end\n");

			int[] compile_status = new int[1];
			GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compile_status, 0);
			if(compile_status[0] == 0) {
				GLES20.glDeleteShader(shader);
				throw new Error("Could not compile " + name);
			}

			return shader;
		}
	}

	public void clearDepthBuffer() {
		finishFrame();
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
	}

	@Override
	public void startFrame() {
		super.startFrame();
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
	}
}
