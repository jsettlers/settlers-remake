#version 310 es

#extension GL_EXT_geometry_shader : enable

precision mediump float;

layout(points) in;
layout(triangle_strip, max_vertices = 4) out;

in vec4 geom_color[];
in float geom_index[];
in float geom_intensity[];


uniform mat4 globalTransform;
uniform mat4 projection;
uniform geometryDataBuffer {
    vec2 geometryData[8*1000];
};

out vec4 frag_color;
out vec2 frag_texCoord;
flat out ivec2 frag_uniInfo;
out float frag_intensity;
out float frag_index;

void setVertex(in float offset) {
	gl_Position = projection * globalTransform * (gl_in[0].gl_Position + vec4(geometryData[int(geom_index[0]+offset)*2], 0, 0));
	frag_texCoord = geometryData[int(geom_index[0]+offset)*2+1];
}

void main() {
	frag_color = geom_color[0];

	float without_sign = geom_intensity[0]<0.0 ? -geom_intensity[0] : geom_intensity[0];

    frag_uniInfo = ivec2(geom_intensity[0]<0.0?1:0, without_sign>9.0?1:0);
	frag_intensity = without_sign - (frag_uniInfo.y==1 ? 10.0 : 5.0);
	frag_index = geom_index[0];


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