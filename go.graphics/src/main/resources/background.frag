#version 100

varying highp float frag_color;
varying highp vec2 frag_texcoord;

uniform sampler2D texHandle;

void main() {
	gl_FragColor = texture2D(texHandle, frag_texcoord);
	gl_FragColor.rgb *= frag_color;
}
