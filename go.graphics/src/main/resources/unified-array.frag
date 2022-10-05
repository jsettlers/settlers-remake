#version 300 es

#extension GL_NV_fragdepth : enable

precision mediump float;

in vec2 frag_texcoord;
flat in int frag_mode;
in float frag_color[5];

uniform sampler2D texHandle;
uniform float shadow_depth;

out vec4 fragColor;

void main() {
	float fragDepth = gl_FragCoord.z;
	fragColor = vec4(frag_color[0], frag_color[1], frag_color[2], frag_color[3]);

	bool textured = frag_mode!=0;

	if(textured) {
		bool progress_fence = frag_mode>3;

		vec4 tex_color;
		if(progress_fence) {
			tex_color = texture(texHandle, fragColor.rg+(fragColor.ba-fragColor.rg)*frag_texcoord);
		} else {
			tex_color = texture(texHandle, frag_texcoord);
		}

		bool image_fence = frag_mode>0;
		bool torso_fence = frag_mode>1 && !progress_fence;
		bool shadow_fence = abs(float(frag_mode))>2.0 && !progress_fence;

		if(torso_fence && tex_color.a < 0.1 && tex_color.r > 0.1) { // torso pixel
			fragColor.rgb *= tex_color.b;
		} else if(shadow_fence && tex_color.a < 0.1 && tex_color.g > 0.1) { // shadow pixel
			fragColor.rgba = tex_color.aaag;
			fragDepth += shadow_depth;
		} else if(image_fence) { // image pixel
			if(!torso_fence && !shadow_fence && !progress_fence) {
				fragColor *= tex_color;
			} else {
				fragColor = tex_color;
			}
		}
	}

	if(fragColor.a < 0.5) discard;

	fragColor.rgb *= frag_color[4];

	gl_FragDepth = fragDepth;
}
