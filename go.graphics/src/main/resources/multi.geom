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

void main() {
	frag_color = geom_color[0];

	float without_sign = geom_intensity[0]<0.0 ? -geom_intensity[0] : geom_intensity[0];

    frag_uniInfo = ivec2(geom_intensity[0]<0.0?1:0, without_sign>9.0?1:0);
	frag_intensity = without_sign - (frag_uniInfo.y==1 ? 10.0 : 5.0);
	frag_index = geom_index[0];

    //setVertex(int(index*4));
    gl_Position = projection * globalTransform * (gl_in[0].gl_Position + vec4(geometryData[int(geom_index[0])*8], 0, 0));
	frag_texCoord = geometryData[int(geom_index[0])*8+1];
	EmitVertex();
    gl_Position = projection * globalTransform * (gl_in[0].gl_Position + vec4(geometryData[int(geom_index[0])*8+2], 0, 0));
	frag_texCoord = geometryData[int(geom_index[0])*8+3];
    //setVertex(int(index*4+1));
	EmitVertex();
    //setVertex(int(index*4+2));
	gl_Position = projection * globalTransform * (gl_in[0].gl_Position + vec4(geometryData[int(geom_index[0])*8+6], 0, 0));
	frag_texCoord = geometryData[int(geom_index[0])*8+7];
	EmitVertex();
	gl_Position = projection * globalTransform * (gl_in[0].gl_Position + vec4(geometryData[int(geom_index[0])*8+4], 0, 0));
	frag_texCoord = geometryData[int(geom_index[0])*8+5];
    //setVertex(int(index*4+3));
	EmitVertex();

	EndPrimitive();
}