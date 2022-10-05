#version 150

#extension GL_NV_fragdepth : enable

precision mediump float;

in vec4 frag_color;
flat in int frag_mode;
in float frag_intensity;
in vec2 frag_texCoord;

uniform sampler2D texHandle;
uniform float shadow_depth;

out vec4 fragColor;

void main() {
	float fragDepth = gl_FragCoord.z;
	fragColor = frag_color;

	bool textured = frag_mode!=0;

	if(textured) {
		vec4 tex_color = texture(texHandle, frag_texCoord);

		bool image_fence = frag_mode>0;
		bool torso_fence = frag_mode>1;
		bool shadow_fence = abs(float(frag_mode))>2.0;

		if(torso_fence && tex_color.a < 0.1 && tex_color.r > 0.1) { // torso pixel
			fragColor.rgb *= tex_color.b;
		} else if(shadow_fence && tex_color.a < 0.1 && tex_color.g > 0.1) { // shadow pixel
			fragColor.rgba = tex_color.aaag;
			fragDepth += shadow_depth;
		} else if(image_fence) { // image pixel
			if(!torso_fence && !shadow_fence) {
				fragColor *= tex_color;
			} else {
				fragColor = tex_color;
			}
		}
	}

	if(fragColor.a < 0.5) discard;

	fragColor.rgb *= frag_intensity;

	gl_FragDepth = fragDepth;
}
