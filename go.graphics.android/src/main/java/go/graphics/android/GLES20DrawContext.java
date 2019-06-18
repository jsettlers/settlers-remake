package go.graphics.android;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLES31Ext;
import android.opengl.GLES32;
import android.opengl.Matrix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import go.graphics.AbstractColor;
import go.graphics.EGeometryFormatType;
import go.graphics.GL2DrawContext;
import go.graphics.GeometryHandle;
import go.graphics.TextureHandle;

public class GLES20DrawContext extends GLES11DrawContext implements GL2DrawContext {
	public GLES20DrawContext(Context ctx, boolean gles3) {
		super(ctx);
		this.gles3 = gles3;
		Matrix.setIdentityM(global, 0);
	}

	private ArrayList<ShaderProgram> shaders;

	private final float[] global = new float[16];
	private final float[] mat = new float[16];
	protected boolean gles3;

	@Override
	public void init() {
		shaders = new ArrayList<>();

		prog_background = new ShaderProgram("background");
		prog_unified = new ShaderProgram("tex-unified");
		prog_color = new ShaderProgram("color");
		prog_tex = new ShaderProgram("tex");
	}

	private ShaderProgram lastProgram = null;
	protected void useProgram(ShaderProgram id) {
		if(id != lastProgram) {
			GLES20.glUseProgram(id.program);
			lastProgram = id;
		}
	}

	private ShaderProgram prog_background;
	private ShaderProgram prog_unified;
	private ShaderProgram prog_color;
	private ShaderProgram prog_tex;

	private float clr, clg, clb, cla, tlr, tlg, tlb, tla;

	@Override
	public void draw2D(GeometryHandle geometry, TextureHandle texture, int primitive, int offset, int vertices, float x, float y, float z, float sx, float sy, float sz, AbstractColor color, float intensity) {
		boolean changeColor = false;

		float r, g, b, a;
		if(color != null) {
			r = color.red;
			g = color.green;
			b = color.blue;
			a = color.alpha;
		} else {
			r = g = b = a = 1;
		}

		if(texture == null) {
			useProgram(prog_color);
			if(clr != r || clg != g || clb != b || cla != a) {
				clr = r;
				clg = g;
				clb = b;
				cla = a;
				changeColor = true;
			}
		} else {
			bindTexture(texture);
			useProgram(prog_tex);
			if(tlr != r || tlg != g || tlb != b || tla != a) {
				tlr = r;
				tlg = g;
				tlb = b;
				tla = a;
				changeColor = true;
			}
		}

		GLES20.glUniform3fv(lastProgram.trans, 2, new float[] {x, y, z, sx, sy, sz}, 0);

		if(changeColor) {
			GLES20.glUniform4f(lastProgram.color, r, g, b, a);
		}

		if(gles3) {
			if(lastFormat != geometry.vao) GLES30.glBindVertexArray(lastFormat = geometry.vao);
		} else {
			bindGeometry(geometry);
			specifyFormat(geometry.getFormat());
		}
		GLES20.glDrawArrays(primitive, offset*vertices, vertices);
	}

	private float ulr, ulg, ulb, ula, uli;
	private boolean ulim, ulsh;

	@Override
	public void drawUnified2D(GeometryHandle geometry, TextureHandle texture, int primitive, int offset, int vertices, boolean image, boolean shadow, float x, float y, float z, float sx, float sy, float sz, AbstractColor color, float intensity) {
		useProgram(prog_unified);
		bindTexture(texture);

		if(image) {
			float r, g, b, a;
			if (color != null) {
				r = color.red;
				g = color.green;
				b = color.blue;
				a = color.alpha;
			} else {
				r = g = b = a = 1;
			}

			if(ulr != r || ulg != g || ulb != b || ula != a) {
				ulr = r;
				ulg = g;
				ulb = b;
				ula = a;
				GLES20.glUniform4f(prog_unified.color, r, g, b, a);
			}
		}

		if(ulim != image || ulsh != shadow || uli != intensity) {
			GLES20.glUniform3f(prog_unified.uni_info, image?1:0, shadow?1:0, intensity);
			ulim = image;
			ulsh = shadow;
			uli = intensity;
		}

		GLES20.glUniform3fv(lastProgram.trans, 2, new float[] {x, y, z, sx, sy, sz}, 0);

		if(gles3) {
			if(lastFormat != geometry.vao) GLES30.glBindVertexArray(lastFormat = geometry.vao);
		} else {
			bindGeometry(geometry);
			specifyFormat(geometry.getFormat());
		}
		GLES20.glDrawArrays(primitive, offset*vertices, vertices);
	}

	private int lastFormat = 0;
	protected void bindFormat(int format) {
		if(format != lastFormat) {
			GLES30.glBindVertexArray(format);
			lastFormat = format;
		}
	}

	@Override
	protected void specifyFormat(EGeometryFormatType format) {
		GLES20.glEnableVertexAttribArray(0);

		if (format.getTexCoordPos() == -1) {
			GLES20.glVertexAttribPointer(0, 2, GLES20.GL_FLOAT, false, 0, 0);
		} else {
			GLES20.glEnableVertexAttribArray(1);
			int stride = format.getBytesPerVertexSize();
			GLES20.glVertexAttribPointer(0, 2, GLES20.GL_FLOAT, false, stride, 0);
			GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, stride, format.getTexCoordPos());
		}
	}

	@Override
	GeometryHandle allocateVBO(EGeometryFormatType type) {
		GeometryHandle geometry =  super.allocateVBO(type);
		if (gles3 && type.isSingleBuffer()) {
			int[] vaos = new int[] {0};
			GLES30.glGenVertexArrays(1, vaos, 0);
			geometry.setInternalFormatId(vaos[0]);
			bindFormat(vaos[0]);

			specifyFormat(type);
		}

		return geometry;
	}

	@Override
	public void setGlobalAttributes(float x, float y, float z, float sx, float sy, float sz) {
		Matrix.setIdentityM(global, 0);
		Matrix.scaleM(global, 0, sx, sy, sz);
		Matrix.translateM(global, 0, x, y, z);

		for(ShaderProgram shader : shaders) {
			useProgram(shader);
			GLES20.glUniformMatrix4fv(shader.global, 1, false, global, 0);
		}
	}

	@Override
	public void reinit(int width, int height) {
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
		useProgram(prog_unified);
		GLES20.glUniform1f(prog_unified.shadow_depth, depth);
	}

	@Override
	public void setHeightMatrix(float[] matrix) {
		useProgram(prog_background);
		GLES20.glUniformMatrix4fv(prog_background.height, 1, false, matrix, 0);
	}

	private int[] backgroundVAO = new int[] {-1};

	@Override
	public void drawTrianglesWithTextureColored(TextureHandle textureid, GeometryHandle shapeHandle, GeometryHandle colorHandle, int offset, int lines, int width, int stride) {
		bindTexture(textureid);

		if(backgroundVAO[0] == -1) {
			if(gles3) {
				GLES30.glGenVertexArrays(1, backgroundVAO, 0);
				bindFormat(backgroundVAO[0]);
			}
			GLES20.glEnableVertexAttribArray(0);
			GLES20.glEnableVertexAttribArray(1);
			GLES20.glEnableVertexAttribArray(2);

			bindGeometry(shapeHandle);
			GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 5 * 4, 0);
			GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 5 * 4, 3 * 4);

			bindGeometry(colorHandle);
			GLES20.glVertexAttribPointer(2, 1, GLES20.GL_FLOAT, false, 0, 0);
		}
		int starti = offset < 0 ? (int)Math.ceil(-offset/(float)stride) : 0;

		useProgram(prog_background);

		bindFormat(backgroundVAO[0]);
		for (int i = starti; i != lines; i++) {
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, (offset + stride * i) * 3, width * 3);
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
		public final int uni_info;
		public final int shadow_depth;

		protected ShaderProgram(String name) {
			int vertexShader = -1;
			int geometryShader = -1;
			int fragmentShader;

			String vname = name;
			if(name.contains("-")) vname = name.split("-")[0];

			try {
				vertexShader = createShader(vname+".vert", GLES20.GL_VERTEX_SHADER);
				geometryShader = createShader(vname+".geom", 36313 /*GL_GEOMETRY_SHADER*/);
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
			uni_info = GLES20.glGetUniformLocation(program, "uni_info");
			shadow_depth = GLES20.glGetUniformLocation(program, "shadow_depth");

			useProgram(this);
			if(tex != -1) GLES20.glUniform1i(tex, 0);

			shaders.add(this);
		}

		private ArrayList<String> attributes = new ArrayList<>();

		private final String vendor_id = "//VENDOR=" +GLES20.glGetString(GLES20.GL_VENDOR) + " ";

		private int createShader(String name, int type) throws IOException {
			int shader = GLES20.glCreateShader(type);
			if(shader == 0) return -1;

			InputStream shaderFile = getClass().getResourceAsStream("/"+name);
			if(shaderFile == null) return -1;
			BufferedReader is = new BufferedReader(new InputStreamReader(shaderFile));
			StringBuilder source = new StringBuilder();
			String line;

			while((line = is.readLine()) != null) {
				if(line.startsWith("attribute") || line.endsWith("//attribute")) {
					attributes.add(line.split(" ")[2].replaceAll(";", ""));
				}

				int vendor_index = line.indexOf(vendor_id);
				if(vendor_index != -1) {
					String remaining = line.substring(vendor_index+vendor_id.length());
					String[] replace = remaining.split("=");
					line = line.substring(0, vendor_index).replaceFirst(replace[0], replace[1]);
				}

				source.append(line);
				if(line.startsWith("#version")) {
					//source.append(" es");
				}
				source.append("\n");
			}

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
}
