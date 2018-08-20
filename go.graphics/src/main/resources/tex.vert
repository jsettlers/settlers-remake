#version 100

attribute highp vec2 vertex;
attribute highp vec2 texcoord;

uniform mat4 globalTransform;
uniform vec3 transform[2];
uniform mat4 projection;

varying highp vec2 frag_texcoord;

void main() {
	vec4 transformed = vec4(vertex, 0, 1);
	transformed.xyz = (transformed.xyz*transform[1])+transform[0];
	gl_Position = projection * globalTransform * transformed;
	frag_texcoord = texcoord;
}
