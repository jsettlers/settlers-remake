package go.graphics.android;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import go.graphics.EGeometryFormatType;
import go.graphics.GeometryHandle;
import go.graphics.TextureHandle;

public class GLES20DrawContext extends GLES11DrawContext {
	public GLES20DrawContext(Context ctx, boolean gles3) {
		super(ctx);
		this.gles3 = gles3;
		Matrix.setIdentityM(global, 0);
	}

	private String[] uniform_names;
	private ArrayList<ShaderProgram> shaders;

	private final float[] global = new float[16];
	private final float[] mat = new float[16];
	private final float[] vec = new float[4];
	private boolean gles3;

	@Override
	public void init() {
		uniform_names = new String[] {"projection", "globalTransform", "transform", "texHandle", "color", "height"};
		shaders = new ArrayList<>();

		prog_background = new ShaderProgram("background");
		prog_texcolor = new ShaderProgram("tex-color");
		prog_color = new ShaderProgram("color");
		prog_tex = new ShaderProgram("tex");

		for(ShaderProgram shader : shaders) {
			useProgram(shader);
			if(shader.ufs[TEX] != -1) GLES20.glUniform1i(shader.ufs[TEX], 0);
		}
	}

	private ShaderProgram lastProgram = null;
	private void useProgram(ShaderProgram id) {
		if(id != lastProgram) {
			GLES20.glUseProgram(id.program);
			lastProgram = id;
		}
	}

	private ShaderProgram prog_background;
	private ShaderProgram prog_texcolor;
	private ShaderProgram prog_color;
	private ShaderProgram prog_tex;

	@Override
	public void draw2D(GeometryHandle geometry, TextureHandle texture, int primitive, int offset, int vertices, float x, float y, float z, float sx, float sy, float sz, float r, float g, float b, float a) {
		boolean colored;

		if(texture == null) {
			useProgram(prog_color);
			colored = true;
		} else {
			bindTexture(texture);
			colored = r != 1 || g != 1 || b != 1 || a != 1;

			if(colored) useProgram(prog_texcolor);
			else useProgram(prog_tex);
		}

		GLES20.glUniform3fv(lastProgram.ufs[TRANS], 2, new float[] {x, y, z, sx, sy, sz}, 0);

		if(colored) {
			vec[0] = r;
			vec[1] = g;
			vec[2] = b;
			vec[3] = a;
			GLES20.glUniform4fv(lastProgram.ufs[COLOR], 1, vec, 0);
		}

		if(gles3) {
			bindFormat(geometry.getInternalFormatId());
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
		Matrix.translateM(global, 0, x, y, z);
		Matrix.scaleM(global, 0, sx, sy, sz);

		for(ShaderProgram shader : shaders) {
			useProgram(shader);
			GLES20.glUniformMatrix4fv(shader.ufs[GLOBAL], 1, false, global, 0);
		}
	}

	@Override
	public void reinit(int width, int height) {
		GLES20.glViewport(0, 0, width, height);

		Matrix.setIdentityM(mat, 0);
		Matrix.orthoM(mat, 0, 0, width, 0, height, -1, 1);

		for(ShaderProgram shader : shaders) {
			useProgram(shader);
			GLES20.glUniformMatrix4fv(shader.ufs[PROJ], 1, false, mat, 0);
		}
	}

	@Override
	public void setHeightMatrix(float[] matrix) {
		useProgram(prog_background);
		GLES20.glUniformMatrix4fv(prog_background.ufs[HEIGHT], 1, false, matrix, 0);
	}

	@Override
	public boolean supports4Bcolors() {
		return false;
	}

	private int[] backgroundVAO = new int[] {-1};

	@Override
	public void drawTrianglesWithTextureColored(TextureHandle textureid, GeometryHandle shapeHandle, GeometryHandle colorHandle, int offset, int lines, int width, int stride, float x, float y) {
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


		GLES20.glUniform2f(prog_background.ufs[TRANS], x, y);

		bindFormat(backgroundVAO[0]);
		for (int i = starti; i != lines; i++) {
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, (offset + stride * i) * 3, width * 3);
		}
	}
	private static final int PROJ = 0;
	private static final int GLOBAL = 1;
	private static final int TRANS = 2;
	private static final int TEX = 3;
	private static final int COLOR = 4;
	private static final int HEIGHT = 5;


	private class ShaderProgram  {
		public final int program;
		public final int[] ufs = new int[6];

		private ShaderProgram(String name) {
			int vertexShader = -1;
			int fragmentShader;

			String vname = name;
			if(name.contains("-")) vname = name.split("-")[0];

			try {
				vertexShader = createShader(vname+".vert", GLES20.GL_VERTEX_SHADER);
				fragmentShader = createShader(name+".frag", GLES20.GL_FRAGMENT_SHADER);
			} catch (IOException e) {
				e.printStackTrace();

				if(vertexShader != -1) GLES20.glDeleteShader(vertexShader);
				throw new Error("could not read shader files", e);
			}

			program = GLES20.glCreateProgram();

			GLES20.glAttachShader(program, vertexShader);
			GLES20.glAttachShader(program, fragmentShader);

			GLES20.glLinkProgram(program);
			GLES20.glValidateProgram(program);

			//GLES20.glDetachShader(program, vertexShader);
			//GLES20.glDetachShader(program, fragmentShader);
			GLES20.glDeleteShader(vertexShader);
			GLES20.glDeleteShader(fragmentShader);

			String log = GLES20.glGetProgramInfoLog(program);
			if(!log.isEmpty()) System.out.print("info log of " + name + "=====\n" + log + "==== end\n");

			int[] link_status = new int[1];
			GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, link_status, 0);
			if(link_status[0] == 0) {
				GLES20.glDeleteProgram(program);
				throw new Error("Could not link " + name);
			}

			GLES20.glBindAttribLocation(program, 0, "vertex");
			GLES20.glBindAttribLocation(program, 1, "texcoord");
			GLES20.glBindAttribLocation(program, 2, "color");

			for(int i = 0;i != ufs.length;i++) {
				int uf = GLES20.glGetUniformLocation(program, uniform_names[i]);
				ufs[i] = uf;
			}
			shaders.add(this);
		}

		private int createShader(String name, int type) throws IOException {
			int shader = GLES20.glCreateShader(type);

			BufferedReader is = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/"+name)));
			StringBuilder source = new StringBuilder();
			String line;

			while((line = is.readLine()) != null) {
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
