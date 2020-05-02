#version 450
#extension GL_ARB_separate_shader_objects : enable

layout(location=0) in vec2 vertex;
layout(location=1) in vec2 texcoord;
layout(location=2) in vec2 co_vertex;

layout(constant_id=1) const int MAX_GLOBALTRANS_COUNT = 4;

layout(binding=0) uniform GlobalData {
	mat4 projection;
	mat4 globalTrans[MAX_GLOBALTRANS_COUNT];
} global;


layout(push_constant) uniform UnifiedPerCall {
	int globalTransIndex;
	int texIndex;
	vec2 localRot;
	vec4 localTrans;
	vec4 color;
	float intensity;
	int mode;
} local;

layout (location=0) out vec2 frag_texcoord;

void main() {
	vec2 local_vert;
	if(local.mode != 0) {
		frag_texcoord = texcoord;
		local_vert = vertex;
	} else {
		frag_texcoord = vec2(0, 0);
		local_vert = co_vertex;
	}

	vec4 transformed = vec4(local_vert*local.localRot, 0, 1);
	transformed.xyz += local.localTrans.xyz;

	gl_Position = global.projection * global.globalTrans[local.globalTransIndex] * transformed;
}