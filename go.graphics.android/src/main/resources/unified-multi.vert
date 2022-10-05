#version 300 es

precision mediump float;

in vec3 position; //attribute
in vec2 scale; //attribute
in vec4 color; //attribute
in vec3 additional; //attribute

uniform mat4 globalTransform;
uniform mat4 projection;

layout(std140) uniform geometryDataBuffer {
	vec4 geometryData[4*1000];
};

out vec4 frag_color;
flat out int frag_mode;
out float frag_intensity;
out vec2 frag_texCoord;

void main() {
	frag_mode = int(additional.z);
	frag_color = color;
	frag_intensity = additional.x;
	int index = int(additional.y)+gl_VertexID;

	gl_Position = projection * globalTransform * vec4(position+vec3(scale*geometryData[index].xy, 0.f), 1.f);
	frag_texCoord = geometryData[index].zw;
}
