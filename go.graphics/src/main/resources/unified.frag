#version 100

#extension GL_NV_fragdepth : enable

precision mediump float;

varying vec2 frag_texcoord;

uniform sampler2D texHandle;
uniform float shadow_depth;

uniform lowp int mode;
uniform float color[5]; // r,g,b,a, intensity

void main() {
	float fragDepth = gl_FragCoord.z;
	vec4 fragColor = vec4(color[0], color[1], color[2], color[3]);

	bool textured = mode!=0;

	if(textured) {
		bool progress_fence = mode > 3;

		vec4 tex_color;
		if(progress_fence) {
			tex_color = texture2D(texHandle, fragColor.rg+(fragColor.ba-fragColor.rg)*frag_texcoord);
		} else {
			tex_color = texture2D(texHandle, frag_texcoord);
		}

		bool image_fence = mode>0;
		bool torso_fence = mode>1 && !progress_fence;
		bool shadow_fence = abs(float(mode))>2.0 && !progress_fence;

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

	fragColor.rgb *= color[4];

	gl_FragColor = fragColor;

	#ifdef GL_NV_fragdepth
	gl_FragDepth = fragDepth;
	#else
	#ifndef GL_ES
	gl_FragDepth = fragDepth;
	#endif
	#endif
}
