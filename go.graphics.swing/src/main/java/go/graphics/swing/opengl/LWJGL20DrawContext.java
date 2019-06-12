package go.graphics.swing.opengl;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.KHRDebug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import go.graphics.AbstractColor;
import go.graphics.EGeometryFormatType;
import go.graphics.GL2DrawContext;
import go.graphics.GeometryHandle;
import go.graphics.TextureHandle;

@SuppressWarnings("WeakerAccess")
public class LWJGL20DrawContext extends LWJGL15DrawContext implements GL2DrawContext{
	public LWJGL20DrawContext(GLCapabilities glcaps, boolean debug) {
		super(glcaps, debug);

	}

	private String[] uniform_names;
	private ArrayList<ShaderProgram> shaders;

	private final Matrix4f global = new Matrix4f();
	private final Matrix4f mat = new Matrix4f();
	private final FloatBuffer matBfr = BufferUtils.createFloatBuffer(16);

	@Override
	void init() {
		uniform_names = new String[] {"projection", "globalTransform", "transform", "texHandle", "color", "height", "uni_info"};
		shaders = new ArrayList<>();

		prog_background = new ShaderProgram("background");
		prog_unified = new ShaderProgram("tex-unified");
		prog_color = new ShaderProgram("color");
		prog_tex = new ShaderProgram("tex");

		for(ShaderProgram shader : shaders) {
			useProgram(shader);
			if(shader.ufs[TEX] != -1) GL20.glUniform1i(shader.ufs[TEX], 0);
		}
	}

	private ShaderProgram lastProgram = null;
	private void useProgram(ShaderProgram id) {
		if(id != lastProgram) {
			GL20.glUseProgram(id.program);
			lastProgram = id;
		}
	}

	private ShaderProgram prog_background;
	private ShaderProgram prog_unified;
	private ShaderProgram prog_color;
	private ShaderProgram prog_tex;

	private float clr, clg, clb, cla, tlr, tlg, tlb, tla;

	@Override
	public void draw2D(GeometryHandle geometry, TextureHandle texture, int primitive, int offset, int vertices, float x, float y, float z, float sx, float sy, float sz, AbstractColor color, float intensity){
		boolean changeColor = false;

		float r, g, b, a;
		if(color != null) {
			r = color.red*intensity;
			g = color.green*intensity;
			b = color.blue*intensity;
			a = color.alpha;
		} else {
			r = g = b = intensity;
			a = 1;
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

		GL20.glUniform3fv(lastProgram.ufs[TRANS], new float[] {x, y, z, sx, sy, sz});

		if(changeColor) {
			GL20.glUniform4f(lastProgram.ufs[COLOR], r, g, b, a);
		}

		if(glcaps.GL_ARB_vertex_array_object) {
			bindFormat(geometry.vao);
		} else {
			bindGeometry(geometry);
			specifyFormat(geometry.getFormat());
		}
		GL11.glDrawArrays(primitive, offset*vertices, vertices);
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
				r = color.red * intensity;
				g = color.green * intensity;
				b = color.blue * intensity;
				a = color.alpha;
			} else {
				r = g = b = intensity;
				a = 1;
			}

			if(ulr != r || ulg != g || ulb != b || ula != a) {
				ulr = r;
				ulg = g;
				ulb = b;
				ula = a;
				GL20.glUniform4f(prog_unified.ufs[COLOR], r, g, b, a);
			}
		}

		if(ulim != image || ulsh != shadow || uli != intensity) {
			GL20.glUniform3f(prog_unified.ufs[UNI_INFO], image?1:0, shadow?1:0, intensity);
			ulim = image;
			ulsh = shadow;
			uli = intensity;
		}

		GL20.glUniform3fv(lastProgram.ufs[TRANS], new float[] {x, y, z, sx, sy, sz});

		if(glcaps.GL_ARB_vertex_array_object) {
			bindFormat(geometry.vao);
		} else {
			bindGeometry(geometry);
			specifyFormat(geometry.getFormat());
		}
		GL11.glDrawArrays(primitive, offset*vertices, vertices);
	}

	@Override
	protected void specifyFormat(EGeometryFormatType format) {
		GL20.glEnableVertexAttribArray(0);

		if (format.getTexCoordPos() == -1) {
			GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);
		} else {
			GL20.glEnableVertexAttribArray(1);
			int stride = format.getBytesPerVertexSize();
			GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, stride, 0);
			GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, stride, format.getTexCoordPos());
		}
	}

	private int lastFormat = 0;
	protected void bindFormat(int format) {
		if(format != lastFormat) {
			ARBVertexArrayObject.glBindVertexArray(format);
			lastFormat = format;
		}
	}

	@Override
	GeometryHandle allocateVBO(EGeometryFormatType type, String name) {
		GeometryHandle geometry =  super.allocateVBO(type, name);
		if (glcaps.GL_ARB_vertex_array_object && type.isSingleBuffer()) {
			geometry.setInternalFormatId(ARBVertexArrayObject.glGenVertexArrays());
			bindFormat(geometry.getInternalFormatId());

			specifyFormat(type);
		}

		if(type.isSingleBuffer())  {
			setObjectLabel(GL11.GL_VERTEX_ARRAY, geometry.getInternalFormatId(), name + "-vao");
		}

		return geometry;
	}

	@Override
	public void setGlobalAttributes(float x, float y, float z, float sx, float sy, float sz) {
		global.identity();
		global.scale(sx, sy, sz);
		global.translate(x, y, z);
		global.get(matBfr);

		for(ShaderProgram shader : shaders) {
			useProgram(shader);
			GL20.glUniformMatrix4fv(shader.ufs[GLOBAL], false, matBfr);
		}
	}

	@Override
	public void resize(int width, int height) {
		GL11.glViewport(0, 0, width, height);

		mat.setOrtho(0, width, 0, height, -1, 1);
		mat.get(matBfr);

		for(ShaderProgram shader : shaders) {
			useProgram(shader);
			GL20.glUniformMatrix4fv(shader.ufs[PROJ], false, matBfr);
		}
	}

	@Override
	public void setHeightMatrix(float[] matrix) {
		useProgram(prog_background);
		GL20.glUniformMatrix4fv(prog_background.ufs[HEIGHT], false, matrix);
	}

	private int backgroundVAO = -1;

	@Override
	public void drawTrianglesWithTextureColored(TextureHandle textureid, GeometryHandle shapeHandle, GeometryHandle colorHandle, int offset, int lines, int width, int stride) {
		bindTexture(textureid);

		if(backgroundVAO == -1) {
			if(glcaps.GL_ARB_vertex_array_object) {
				backgroundVAO = ARBVertexArrayObject.glGenVertexArrays();
				bindFormat(backgroundVAO);
			}
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL20.glEnableVertexAttribArray(2);

			bindGeometry(shapeHandle);
			GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 5 * 4, 0);
			GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 5 * 4, 3 * 4);

			bindGeometry(colorHandle);
			GL20.glVertexAttribPointer(2, 1, GL11.GL_FLOAT, false, 0, 0);

			setObjectLabel(GL11.GL_VERTEX_ARRAY, backgroundVAO, "background-vao");
			setObjectLabel(KHRDebug.GL_BUFFER, shapeHandle.getInternalId(), "background-shape");
			setObjectLabel(KHRDebug.GL_BUFFER, colorHandle.getInternalId(), "background-color");
		}
		int starti = offset < 0 ? (int)Math.ceil(-offset/(float)stride) : 0;

		useProgram(prog_background);

		bindFormat(backgroundVAO);
		for (int i = starti; i != lines; i++) {
			GL11.glDrawArrays(GL11.GL_TRIANGLES, (offset + stride * i) * 3, width * 3);
		}
	}
	private static final int PROJ = 0;
	private static final int GLOBAL = 1;
	private static final int TRANS = 2;
	private static final int TEX = 3;
	private static final int COLOR = 4;
	private static final int HEIGHT = 5;
	private static final int UNI_INFO = 6;

	private class ShaderProgram  {
		public final int program;
		public final int[] ufs = new int[7];

		private ShaderProgram(String name) {
			int vertexShader = -1;
			int fragmentShader;

			String vname = name;
			if(name.contains("-")) vname = name.split("-")[0];

			try {
				vertexShader = createShader(vname+".vert", GL20.GL_VERTEX_SHADER);
				fragmentShader = createShader(name+".frag", GL20.GL_FRAGMENT_SHADER);
			} catch (IOException e) {
				e.printStackTrace();

				if(vertexShader != -1) GL20.glDeleteShader(vertexShader);
				throw new Error("could not read shader files", e);
			}

			program = GL20.glCreateProgram();
			setObjectLabel(KHRDebug.GL_PROGRAM, program, name);

			GL20.glAttachShader(program, vertexShader);
			GL20.glAttachShader(program, fragmentShader);

			GL20.glBindAttribLocation(program, 0, "vertex");
			GL20.glBindAttribLocation(program, 1, "texcoord");
			GL20.glBindAttribLocation(program, 2, "color");

			GL20.glLinkProgram(program);
			GL20.glValidateProgram(program);

			GL20.glDetachShader(program, vertexShader);
			GL20.glDetachShader(program, fragmentShader);
			GL20.glDeleteShader(vertexShader);
			GL20.glDeleteShader(fragmentShader);

			String log = GL20.glGetProgramInfoLog(program);
			if(debugOutput != null && !log.isEmpty()) System.out.print("info log of " + name + "=====\n" + log + "==== end\n");

			if(GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == 0) {

				GL20.glDeleteProgram(program);
				throw new Error("Could not link " + name);
			}

			for(int i = 0;i != ufs.length;i++) {
				int uf = GL20.glGetUniformLocation(program, uniform_names[i]);
				ufs[i] = uf;
			}
			shaders.add(this);
		}

		private int createShader(String name, int type) throws IOException {
			int shader = GL20.glCreateShader(type);
			setObjectLabel(KHRDebug.GL_SHADER, shader, name);

			BufferedReader is = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/"+name)));
			StringBuilder source = new StringBuilder();
			String line;

			while((line = is.readLine()) != null) {
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
}
