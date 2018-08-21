#version 100

precision mediump float;

attribute vec2 vertex;
attribute vec2 texcoord;

uniform mat4 globalTransform;
uniform vec3 transform[2];
uniform mat4 projection;

varying vec2 frag_texcoord;

void main() {
	vec4 transformed = vec4(vertex, 0, 1);
	transformed.xyz = (transformed.xyz*transform[1])+transform[0];
	gl_Position = projection * globalTransform * transformed;
	frag_texcoord = texcoord;
}
