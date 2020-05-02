#version 450
#extension GL_ARB_separate_shader_objects : enable

layout(location=0) in vec3 vertex;
layout(location=1) in vec2 texcoord;
layout(location=2) in float color;


layout(constant_id=1) const int MAX_GLOBALTRANS_COUNT = 4;

layout(binding=0) uniform GlobalData {
	mat4 projection;
	mat4 globalTrans[MAX_GLOBALTRANS_COUNT];
} global;

layout(binding=2) uniform BackgroundData {
	mat4 height;
} background;

layout(push_constant) uniform LocalData {
	int globalTransIndex;
	int texIndex;
} local;

layout (location=0) out float frag_color;
layout (location=1) out vec2 frag_texcoord;

void main() {
	vec4 transformed = background.height * vec4(vertex, 1);
	transformed.z = -.1;

	gl_Position = global.projection * global.globalTrans[local.globalTransIndex] * transformed;

	frag_color = color;
	frag_texcoord = texcoord;
}