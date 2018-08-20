#version 100

varying highp vec2 frag_texcoord;

uniform sampler2D texHandle;
uniform highp vec4 color;

void main() {
	gl_FragColor = texture2D(texHandle, frag_texcoord)*color;
	if(gl_FragColor.a < 0.5) discard;
}
