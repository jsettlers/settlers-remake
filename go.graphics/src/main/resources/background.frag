#version 100

precision mediump float;

varying float frag_color;
varying vec2 frag_texcoord;

uniform sampler2D texHandle;

void main() {
	gl_FragColor = texture2D(texHandle, frag_texcoord);
	gl_FragColor.rgb *= frag_color;
}
