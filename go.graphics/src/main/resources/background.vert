#version 100

precision mediump float;

attribute vec3 vertex;
attribute vec2 texcoord;
attribute float color;

uniform mat4 globalTransform;
uniform mat4 projection;
uniform mat4 height;

varying float frag_color;
varying vec2 frag_texcoord;

void main() {
	vec4 transformed = height * vec4(vertex, 1);
	transformed.z = -.1;
	gl_Position = projection * globalTransform * transformed;

	frag_color = color;
	frag_texcoord = texcoord;
}
