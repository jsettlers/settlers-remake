#version 310 es

#extension GL_NV_fragdepth : enable

precision mediump float;

in vec4 frag_color;
flat in ivec2 frag_uniInfo; // x=image, y=shadow
in float frag_intensity;
in float frag_index;
in vec2 frag_texCoord;

uniform sampler2D texHandle;
uniform float shadow_depth;

out vec4 color;
out float gl_FragDepth;

void main() {
	vec4 tex_color = texture
	(texHandle, frag_texCoord);
    color = vec4(0,0,0,0);

	if(frag_uniInfo.x == 1) { // draw image
		if(tex_color.a < 0.1 && tex_color.r > 0.1) { // torso pixel
            color.rgb = frag_color.rgb*tex_color.b;
            color.a = frag_color.a;
		} else {
            color = tex_color;
		}
	}
	gl_FragDepth = gl_FragCoord.z;

	if(frag_uniInfo.y == 1 && tex_color.g > 0.1 && tex_color.a < 0.1) { // shadow pixel
		color.rgba = tex_color.aaag;
		gl_FragDepth += shadow_depth;
	}

    color.rgb *= frag_intensity;

	if(color.a < 0.5) discard;
}
