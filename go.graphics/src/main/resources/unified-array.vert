#version 300 es

precision mediump float;

in vec2 vertex; //attribute
in vec2 texcoord; //attribute

uniform mat4 globalTransform;
uniform mat4 projection;

uniform vec4 color[100];
uniform vec4 transform[100];

out vec2 frag_texcoord;
out float frag_color[5];
flat out int frag_mode;

void main() {
	vec4 transformed = vec4(vertex, 0, 1);
	transformed.xyz += transform[gl_InstanceID].xyz;
	gl_Position = projection * globalTransform * transformed;


	frag_color[0] = color[gl_InstanceID].r;
	frag_color[1] = color[gl_InstanceID].g;
	frag_color[2] = color[gl_InstanceID].b;
	frag_color[3] = color[gl_InstanceID].a;

	float int_mode = transform[gl_InstanceID].w/10.0;


	frag_mode = int(floor(int_mode));
	frag_color[4] = (int_mode-float(frag_mode))*10.0-1.0;

	if(frag_mode != 0) frag_texcoord = texcoord;
}
