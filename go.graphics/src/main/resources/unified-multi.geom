#version 330 core

#extension GL_EXT_geometry_shader : enable

precision mediump float;

layout(points) in;
layout(triangle_strip, max_vertices = 4) out;


in vec2 geom_scale[];
in vec4 geom_color[];
in float geom_intensity[];
in float geom_index[];
in float geom_mode[];


uniform mat4 globalTransform;
uniform mat4 projection;
uniform geometryDataBuffer {
    vec2 geometryData[8*1000];
};

out vec4 frag_color;
flat out int frag_mode;
out vec2 frag_texCoord;
out float frag_intensity;

void setVertex(in float offset) {
	gl_Position = projection * globalTransform * (gl_in[0].gl_Position+vec4(geom_scale[0]*geometryData[int(geom_index[0]+offset)*2], 0, 0));
	frag_texCoord = geometryData[int(geom_index[0]+offset)*2+1];
}

void main() {
	frag_mode = int(geom_mode[0]);
	frag_color = geom_color[0];
	frag_intensity = geom_intensity[0];


	setVertex(0.0);
	EmitVertex();
	setVertex(1.0);
	EmitVertex();
	setVertex(3.0);
	EmitVertex();
	setVertex(2.0);
	EmitVertex();

	EndPrimitive();
}